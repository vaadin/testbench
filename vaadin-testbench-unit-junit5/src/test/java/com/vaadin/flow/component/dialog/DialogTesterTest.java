/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.dialog;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vaadin.flow.component.ModalityMode;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonTester;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.testbench.unit.UIUnitTest;
import com.vaadin.testbench.unit.ViewPackages;

@ViewPackages
class DialogTesterTest extends UIUnitTest {

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

    @Test
    void headerTitle_getHeaderTitleReturnsCorrect() {
        String title = "Test Title";
        view.dialog.setHeaderTitle(title);

        dialog_.open();

        Assertions.assertEquals(title, dialog_.getHeaderTitle(),
                "Dialog header title should match");
    }

}
