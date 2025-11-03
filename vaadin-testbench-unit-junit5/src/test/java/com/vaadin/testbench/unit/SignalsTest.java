/*
 * Copyright (C) 2000-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */

package com.vaadin.testbench.unit;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import com.example.base.signals.SignalsView;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import com.vaadin.flow.component.ComponentEffect;
import com.vaadin.flow.component.UI;

@ViewPackages(packages = "com.example.base.signals")
@Timeout(10)
public class SignalsTest extends UIUnitTest {

    @Test
    void attachedComponent_triggerSignal_effectEvaluatedSynchronously() {
        var view = navigate(SignalsView.class);
        var counterTester = test(view.counter);
        Assertions.assertEquals("Counter: 0", counterTester.getText());

        test(view.incrementButton).click();
        Assertions.assertEquals("Counter: 1", counterTester.getText());
    }

    @Test
    void detachedComponent_triggerSignal_effectEvaluatedOnAttach() {
        var view = navigate(SignalsView.class);
        var counterTester = test(view.counter);
        Assertions.assertEquals("Counter: 0", counterTester.getText());
        view.counter.removeFromParent();
        Assertions.assertFalse(counterTester.isUsable());

        test(view.incrementButton).click();
        Assertions.assertEquals("Counter: 0", view.counter.getText());

        view.add(view.counter);
        Assertions.assertEquals("Counter: 1", view.counter.getText());
    }

    @Test
    void detachedComponent_triggerEffect_effectEvaluatedAsynchronously() {
        var view = navigate(SignalsView.class);
        var counterTester = test(view.asyncCounter);
        Assertions.assertEquals("Counter: 0", counterTester.getText());
        UI ui = UI.getCurrent();
        UI.setCurrent(null);

        ComponentEffect.effect(ui,
                () -> view.asyncCounter.setText("Counter: async"));
        runPendingSignalsTasks();

        UI.setCurrent(ui);
        Assertions.assertEquals("Counter: async", view.asyncCounter.getText());
    }

    @Test
    void attachedComponent_triggerSignalFromNonUIThread_effectEvaluatedAsynchronously() {
        var view = navigate(SignalsView.class);
        var counterTester = test(view.asyncCounter);
        Assertions.assertEquals("Counter: 0", counterTester.getText());
        CompletableFuture.runAsync(() -> {
            view.asyncNumberSignal.incrementBy(10.0);
        });
        runPendingSignalsTasks();
        Assertions.assertEquals("Counter: 10", counterTester.getText());
    }

    @Test
    void attachedComponent_triggerSignalFromNonUIThreadThroughComponentEffect_effectEvaluatedAsynchronously() {
        var view = navigate(SignalsView.class);
        var counterTester = test(view.asyncCounter);
        Assertions.assertEquals("Counter: 0", counterTester.getText());
        test(view.quickBackgroundTaskButton).click();
        runPendingSignalsTasks(300, TimeUnit.MILLISECONDS);
        Assertions.assertEquals("Counter: 10", counterTester.getText());
    }

    @Test
    void attachedComponent_slowEffect_effectEvaluatedAsynchronously() {
        var view = navigate(SignalsView.class);
        var counterTester = test(view.asyncWithDelayCounter);
        Assertions.assertEquals("Counter: 0 (delayed)",
                counterTester.getText());
        test(view.slowBackgroundTaskButton).click();
        Assertions.assertTrue(
                runPendingSignalsTasks(300, TimeUnit.MILLISECONDS),
                "Expected pending signals tasks to be run");
        Assertions.assertEquals("Counter: 10 (delayed)",
                counterTester.getText());
    }

}
