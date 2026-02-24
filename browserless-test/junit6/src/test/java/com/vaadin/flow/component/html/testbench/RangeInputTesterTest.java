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
package com.vaadin.flow.component.html.testbench;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vaadin.browserless.BrowserlessTest;
import com.vaadin.browserless.ViewPackages;
import com.vaadin.flow.component.html.RangeInput;
import com.vaadin.flow.router.RouteConfiguration;

@ViewPackages
class RangeInputTesterTest extends BrowserlessTest {

    RangeInputTester tester;
    RangeInput component;

    @BeforeEach
    void init() {
        RouteConfiguration.forApplicationScope()
                .setAnnotatedRoute(RangeInputView.class);
        component = navigate(RangeInputView.class).rangeInput;
        tester = test(component);
    }

    @Test
    void setValue_unusable_throws() {
        component.setEnabled(false);
        Assertions.assertThrows(IllegalStateException.class,
                () -> tester.setValue(12.3));

        component.setEnabled(true);
        component.setVisible(false);
        Assertions.assertThrows(IllegalStateException.class,
                () -> tester.setValue(12.3));

    }

    @Test
    void setValue_allowedValue_valueSet() {
        double newValue = 12.0;
        tester.setValue(newValue);
        Assertions.assertEquals(newValue, component.getValue());

        newValue = component.getMin();
        tester.setValue(newValue);
        Assertions.assertEquals(newValue, component.getValue());

        newValue = component.getMax();
        tester.setValue(newValue);
        Assertions.assertEquals(newValue, component.getValue());
    }

    @Test
    void setValue_null_throws() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> tester.setValue(null));
    }

    @Test
    void setValue_valueNotRespectingStep_throws() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> tester.setValue(12.3),
                "Accepted value not respecting step");
        component.setValue(4.0);
        Assertions.assertEquals(4, 0, component.getValue());
        component.setValue(3.0);
        Assertions.assertEquals(3.0, component.getValue());
    }

    @Test
    void setValue_customStep() {
        double step = 3.3;
        double initialValue = 1.3;
        component.setStep(step);
        component.setValue(initialValue);

        double newValue = initialValue + (step * 2);
        tester.setValue(newValue);
        Assertions.assertEquals(newValue, component.getValue());

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> tester.setValue(newValue + 1),
                "Accepted value not matching step");
    }

    @Test
    void setValue_undefinedStep_valueAccepted() {
        component.setStep(null);
        double newValue = 12.3;
        tester.setValue(newValue);
        Assertions.assertEquals(newValue, component.getValue());
    }

    @Test
    void setValue_lessThanMin_throws() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> tester.setValue(-100.0), "Accepted value less than min");
    }

    @Test
    void setValue_greaterThanMax_throws() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> tester.setValue(100.0),
                "Accepted value greater than max");
    }

    @Test
    void getValue_unusable_getsValue() {
        double newValue = 12.4;
        component.setValue(newValue);
        Assertions.assertEquals(newValue, component.getValue());
    }

    @Test
    void getValue_hidden_throws() {
        component.setVisible(false);
        Assertions.assertThrows(IllegalStateException.class,
                () -> tester.getValue());
    }

    @Test
    void increase_addStepToValue() {
        tester.increase();
        Assertions.assertEquals(1.0, component.getValue());
        tester.increase();
        Assertions.assertEquals(2.0, component.getValue());
        tester.increase(5);
        Assertions.assertEquals(7.0, component.getValue());

    }

    @Test
    void increase_negativeTimes_throws() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> tester.increase(-1));
    }

    @Test
    void increase_greaterThanMax_throws() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> tester.increase(30));
    }

    @Test
    void increase_undefinedStep_throws() {
        component.setStep(null);
        Assertions.assertThrows(IllegalStateException.class,
                () -> tester.increase());
    }

    @Test
    void increase_unusable_throws() {
        component.setEnabled(false);
        Assertions.assertThrows(IllegalStateException.class,
                () -> tester.increase());
    }

    @Test
    void decrease_addStepToValue() {
        tester.decrease();
        Assertions.assertEquals(-1.0, component.getValue());
        tester.decrease();
        Assertions.assertEquals(-2.0, component.getValue());
        tester.decrease(5);
        Assertions.assertEquals(-7.0, component.getValue());

    }

    @Test
    void decrease_lessThanMin_throws() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> tester.decrease(30));
    }

    @Test
    void decrease_undefinedStep_throws() {
        component.setStep(null);
        Assertions.assertThrows(IllegalStateException.class,
                () -> tester.decrease());
    }

    @Test
    void decrease_unusable_throws() {
        component.setEnabled(false);
        Assertions.assertThrows(IllegalStateException.class,
                () -> tester.decrease());
    }

    @Test
    void decrease_negativeTimes_throws() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> tester.decrease(-1));
    }

}
