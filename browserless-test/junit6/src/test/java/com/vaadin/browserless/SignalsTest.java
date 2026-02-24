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
package com.vaadin.testbench.unit;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import com.example.base.signals.SignalsView;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

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
