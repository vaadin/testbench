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
package com.vaadin.flow.component.dialog;

import com.vaadin.browserless.ComponentTester;
import com.vaadin.browserless.Tests;

@Tests(Dialog.class)
public class DialogTester extends ComponentTester<Dialog> {
    /**
     * Wrap given component for testing.
     *
     * @param component
     *            target component
     */
    public DialogTester(Dialog component) {
        super(component);
    }

    /**
     * Open the dialog.
     */
    public void open() {
        getComponent().open();
        roundTrip();
    }

    /**
     * Close the dialog.
     */
    public void close() {
        getComponent().close();
    }
}
