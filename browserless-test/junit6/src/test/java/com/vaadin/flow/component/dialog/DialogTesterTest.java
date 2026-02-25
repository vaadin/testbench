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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vaadin.browserless.BrowserlessTest;
import com.vaadin.browserless.ViewPackages;
import com.vaadin.flow.component.ModalityMode;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonTester;
import com.vaadin.flow.router.RouteConfiguration;

@ViewPackages
class DialogTesterTest extends BrowserlessTest {

    DialogView view;
    DialogTester dialog_;

    @BeforeEach
    void init() {
        RouteConfiguration.forApplicationScope()
                .setAnnotatedRoute(DialogView.class);
        view = navigate(DialogView.class);
        dialog_ = test(view.dialog);
    }

    @Test
    void openAndClose_dialogIsAttachedAndDetached() {
        dialog_.open();
        Assertions.assertTrue(dialog_.isUsable(),
                "Dialog should be attached on open");

        dialog_.close();
        Assertions.assertFalse(dialog_.isUsable(),
                "Dialog should not be usable after close");
        Assertions.assertFalse(view.dialog.isAttached(),
                "Dialog should be detached on close");
    }

    @Test
    void programmaticallyClose_dialogIsDetached() {
        dialog_.open();

        view.dialog.close();

        Assertions.assertFalse(view.dialog.isAttached(),
                "Dialog should be detached on close");
    }

    @Test
    void modalDialog_visual_doNotBlockUIComponents() {
        view.dialog.setModality(ModalityMode.VISUAL);
        dialog_.open();
        ButtonTester<Button> button_ = test(view.button);
        Assertions.assertTrue(button_.isUsable(),
                "Default VISUAL modal dialog should not block button");
    }

    @Test
    void modalDialog_strict_blocksUIComponents() {
        view.dialog.setModality(ModalityMode.STRICT);
        dialog_.open();
        ButtonTester<Button> button_ = test(view.button);
        Assertions.assertFalse(button_.isUsable(),
                "Dialog should be modal by default blocking button");

        dialog_.close();

        Assertions.assertTrue(button_.isUsable(),
                "Closing dialog should enable button");
    }

    @Test
    void nonModalDialog_UIComponentsUsable() {
        view.dialog.setModality(ModalityMode.MODELESS);
        dialog_.open();
        ButtonTester<Button> button_ = test(view.button);
        Assertions.assertTrue(button_.isUsable(),
                "Non-modal dialog should not block button");
    }

}
