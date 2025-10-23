/*
 * Copyright (C) 2000-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */

package com.example.base.signals;

import com.vaadin.flow.shared.Registration;
import java.util.concurrent.CompletableFuture;

import com.vaadin.flow.component.ComponentEffect;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.Route;
import com.vaadin.signals.NumberSignal;
import com.vaadin.signals.Signal;

@Route(value = "signals")
public class SignalsView extends Div {

    public final NativeButton incrementButton;
    public final NativeButton quickBackgroundTaskButton;
    public final NativeButton slowBackgroundTaskButton;
    public final Span counter;
    public final Span asyncCounter;
    public final Span asyncWithDelayCounter;
    public final NumberSignal numberSignal;
    public final NumberSignal asyncNumberSignal;
    public final NumberSignal asyncWithDelayNumberSignal;

    public SignalsView() {
        numberSignal = new NumberSignal();

        Signal<String> computedSignal = numberSignal
                .mapIntValue(counter -> "Counter: " + counter);
        incrementButton = new NativeButton("Increment",
                ev -> numberSignal.incrementBy(1.0));
        counter = new Span("Counter: -");
        ComponentEffect.bind(counter, computedSignal, Span::setText);

        asyncNumberSignal = new NumberSignal();
        Signal<String> asyncComputedSignal = asyncNumberSignal
                .mapIntValue(counter -> "Counter: " + counter);
        asyncCounter = new Span("Counter: -");
        ComponentEffect.bind(asyncCounter, asyncComputedSignal, Span::setText);

        asyncWithDelayNumberSignal = new NumberSignal();
        Signal<String> asyncWithDelayComputedSignal = asyncWithDelayNumberSignal
                .mapIntValue(counter -> "Counter: " + counter);
        asyncWithDelayCounter = new Span("Counter: -");
        ComponentEffect.bind(asyncWithDelayCounter, asyncWithDelayComputedSignal, Span::setText);

        quickBackgroundTaskButton = new NativeButton("Quick background task",
                event -> {
                    CompletableFuture.runAsync(
                            () -> asyncNumberSignal.incrementBy(10.0),
                            CompletableFuture.delayedExecutor(100,
                                    java.util.concurrent.TimeUnit.MILLISECONDS));
                });

        ComponentEffect.effect(asyncWithDelayCounter, () -> {
            // read to trigger the signal effect
            asyncWithDelayNumberSignal.value();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        });
        slowBackgroundTaskButton = new NativeButton("Quick background task",
                event -> CompletableFuture.runAsync(() -> {
                    asyncWithDelayNumberSignal.incrementBy(10.0);
                }));

        add(incrementButton, quickBackgroundTaskButton,
                slowBackgroundTaskButton, counter, asyncCounter, asyncWithDelayCounter);
    }
}
