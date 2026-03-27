/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.example.base.signals;

import java.util.concurrent.CompletableFuture;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.signals.Signal;
import com.vaadin.flow.signals.shared.SharedNumberSignal;

@Route(value = "signals")
public class SignalsView extends Div {

    public final NativeButton incrementButton;
    public final NativeButton quickBackgroundTaskButton;
    public final NativeButton slowBackgroundTaskButton;
    public final Span counter;
    public final Span asyncCounter;
    public final Span asyncWithDelayCounter;
    public final SharedNumberSignal numberSignal;
    public final SharedNumberSignal asyncNumberSignal;
    public final SharedNumberSignal asyncWithDelayNumberSignal;

    public SignalsView() {
        numberSignal = new SharedNumberSignal();

        Signal<String> computedSignal = numberSignal
                .mapIntValue(counter -> "Counter: " + counter);
        incrementButton = new NativeButton("Increment",
                ev -> numberSignal.incrementBy(1.0));
        counter = new Span("Counter: -");
        counter.bindText(computedSignal);

        asyncNumberSignal = new SharedNumberSignal();
        Signal<String> asyncComputedSignal = asyncNumberSignal
                .mapIntValue(counter -> "Counter: " + counter);
        asyncCounter = new Span("Counter: -");
        asyncCounter.bindText(asyncComputedSignal);

        asyncWithDelayNumberSignal = new SharedNumberSignal();
        asyncWithDelayCounter = new Span("Counter: -");

        quickBackgroundTaskButton = new NativeButton("Quick background task",
                event -> {
                    CompletableFuture.runAsync(
                            () -> asyncNumberSignal.incrementBy(10.0),
                            CompletableFuture.delayedExecutor(100,
                                    java.util.concurrent.TimeUnit.MILLISECONDS));
                });

        Signal.effect(asyncWithDelayCounter, () -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
            asyncWithDelayCounter.setText("Counter: "
                    + asyncWithDelayNumberSignal.getAsInt() + " (delayed)");
        });
        slowBackgroundTaskButton = new NativeButton("Quick background task",
                event -> CompletableFuture.runAsync(() -> {
                    asyncWithDelayNumberSignal.incrementBy(10.0);
                }));

        add(incrementButton, quickBackgroundTaskButton,
                slowBackgroundTaskButton, counter, asyncCounter,
                asyncWithDelayCounter);
    }
}
