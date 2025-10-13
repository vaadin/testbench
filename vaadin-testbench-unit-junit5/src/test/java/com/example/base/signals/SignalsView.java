/*
 * Copyright (C) 2000-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */

package com.example.base.signals;

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
    public final NumberSignal numberSignal;

    public SignalsView() {
        numberSignal = new NumberSignal();
        Signal<String> computedSignal = numberSignal
                .mapIntValue(counter -> "Counter: " + counter);
        incrementButton = new NativeButton("Increment",
                ev -> numberSignal.incrementBy(1.0));
        counter = new Span("Counter: -");
        ComponentEffect.bind(counter, computedSignal, Span::setText);

        quickBackgroundTaskButton = new NativeButton("Quick background task",
                event -> {
                    CompletableFuture.runAsync(
                            () -> numberSignal.incrementBy(10.0),
                            CompletableFuture.delayedExecutor(100,
                                    java.util.concurrent.TimeUnit.MILLISECONDS));
                });
        slowBackgroundTaskButton = new NativeButton("Quick background task",
                event -> CompletableFuture.runAsync(() -> {
                    ComponentEffect.effect(counter, () -> {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            throw new RuntimeException(e);
                        }
                        numberSignal.incrementBy(10.0);
                    });
                }));

        add(incrementButton, quickBackgroundTaskButton,
                slowBackgroundTaskButton, counter);
    }
}
