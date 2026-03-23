package com.vaadin.laboratory;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.ServiceInitEvent;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.ApplicationScope;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Exposes bit of stats from the Vaadin app via Spring Boot's Actuator mechanism.
 */
@Component
@ApplicationScope
public class VaadinActuator {

    private final Set<UI> activeUis = Collections.synchronizedSet(new HashSet<>());
    private final Map<Class, AtomicInteger> viewToCount = Collections.synchronizedMap(new HashMap<>());
    private final MeterRegistry meterRegistry;

    public VaadinActuator(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        // Register a gauge metric for active UI count
        Gauge.builder("vaadin.view.count", activeUis::size)
                .description("Number of active Vaadin UI instances")
                .register(meterRegistry);
    }

    @EventListener
    public void onServiceInit(ServiceInitEvent serviceInitEvent) {

        System.err.println("Starting UI registering....");

        serviceInitEvent.getSource().addUIInitListener(event -> {
            UI ui = event.getUI();

            ui.addAfterNavigationListener(navEvt -> {
                activeUis.add(ui);
                com.vaadin.flow.component.Component currentView = ui.getCurrentView();
                Class<? extends com.vaadin.flow.component.Component> aClass = currentView.getClass();
                // Get or create counter for this view type (thread-safe)
                AtomicInteger viewCounter = viewToCount.computeIfAbsent(aClass, k -> {
                    AtomicInteger counter = new AtomicInteger(0);
                    // Register gauge for this specific view type
                    Gauge.builder("vaadin.view.count", counter::get)
                            .tag("view", aClass.getSimpleName())
                            .description("Number of active " + aClass.getSimpleName() + " view instances")
                            .register(meterRegistry);
                    return counter;
                });
                
                viewCounter.incrementAndGet();
                
                currentView.addDetachListener(event1 -> {
                    viewCounter.decrementAndGet();
                });

            });

            ui.addDetachListener(detachEvent -> {
                activeUis.remove(ui);
            });

        });
    }

    public int getActiveUiCount() {
        return activeUis.size();
    }
}
