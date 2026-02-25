/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
