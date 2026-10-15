/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.loadtest;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;

/**
 * Tracks active Vaadin UIs and view counts, exposing them as Micrometer metrics
 * via Spring Boot Actuator (if Micrometer is on the classpath).
 * <p>
 * Registers gauges on Micrometer's {@code Metrics.globalRegistry}, which Spring
 * Boot auto-populates with its managed registries. If Micrometer is not
 * present, tracking still works internally but no metrics are exposed.
 * <p>
 * Auto-registered via {@link LoadTestServiceInitListener} — no code changes
 * needed.
 */
class LoadTestMetrics {

    private static final Logger log = LoggerFactory
            .getLogger(LoadTestMetrics.class);

    private final Set<UI> activeUis = Collections
            .synchronizedSet(new HashSet<>());
    private final Map<Class<? extends Component>, Set<Component>> activeViews = new ConcurrentHashMap<>();
    private final boolean micrometerAvailable;

    LoadTestMetrics() {
        micrometerAvailable = isMicrometerAvailable();
        if (micrometerAvailable) {
            registerTotalGauge();
            log.debug(
                    "LoadTestMetrics: Micrometer detected, gauges registered");
        } else {
            log.debug(
                    "LoadTestMetrics: Micrometer not on classpath, metrics tracking only");
        }
    }

    /**
     * Registers UI lifecycle listeners for tracking.
     */
    void trackUI(UI ui) {
        ui.addAfterNavigationListener(navEvent -> {
            activeUis.add(ui);

            Component currentView = ui.getCurrentView();
            Class<? extends Component> viewClass = currentView.getClass();

            Set<Component> views = activeViews.computeIfAbsent(viewClass, k -> {
                Set<Component> v = ConcurrentHashMap.newKeySet();
                if (micrometerAvailable) {
                    registerViewGauge(k, v);
                }
                return v;
            });

            // A reused view instance (e.g. only a route parameter changed)
            // is already tracked, so it is neither counted again nor given
            // another detach listener.
            if (views.add(currentView)) {
                currentView.addDetachListener(event -> {
                    views.remove(currentView);
                    event.unregisterListener();
                });
            }
        });

        ui.addDetachListener(event -> activeUis.remove(ui));
    }

    int getActiveUiCount() {
        return activeUis.size();
    }

    int getActiveViewCount(Class<? extends Component> viewClass) {
        Set<Component> views = activeViews.get(viewClass);
        return views == null ? 0 : views.size();
    }

    private static boolean isMicrometerAvailable() {
        try {
            Class.forName("io.micrometer.core.instrument.Metrics");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private void registerTotalGauge() {
        try {
            io.micrometer.core.instrument.Gauge
                    .builder("vaadin.ui.count", activeUis::size)
                    .description("Number of active Vaadin UI instances")
                    .register(
                            io.micrometer.core.instrument.Metrics.globalRegistry);
        } catch (Exception e) {
            log.debug("Failed to register total UI gauge: " + e.getMessage());
        }
    }

    private void registerViewGauge(Class<? extends Component> viewClass,
            Set<Component> views) {
        try {
            io.micrometer.core.instrument.Gauge
                    .builder("vaadin.view.count", views::size)
                    .tag("view", viewClass.getSimpleName())
                    .description("Number of active " + viewClass.getSimpleName()
                            + " view instances")
                    .register(
                            io.micrometer.core.instrument.Metrics.globalRegistry);
        } catch (Exception e) {
            log.debug("Failed to register view gauge for "
                    + viewClass.getSimpleName() + ": " + e.getMessage());
        }
    }
}
