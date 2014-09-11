/*
 * Copyright 2000-2014 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.tests.testbenchapi.components.checkbox;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.tests.MultiBrowserTest;

/**
 * Testcase used to validate {@link CheckBoxElement#click()} works as expected.
 * See #13763
 */
public class ClickCheckBoxUITest extends MultiBrowserTest {

    @Before
    public void init() {
        openTestURL();
    }

    @Test
    public void testClickToggleCheckboxMark() {
        CheckBoxElement checkbox = $(CheckBoxElement.class).first();
        assertFalse(isChecked(checkbox));

        checkbox.click();
        assertTrue(isChecked(checkbox));

        checkbox.click();
        assertFalse(isChecked(checkbox));
    }

    private boolean isChecked(CheckBoxElement checkbox) {
        return "checked".equals(checkbox.getValue());
    }
}
