/*
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.flow.component.dialog;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonWrap;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.testbench.unit.TestBenchUnit;
import com.vaadin.testbench.unit.ViewPackages;

@ViewPackages
class DialogWrapTest extends TestBenchUnit {

    DialogView view;
    DialogWrap dialog_;

    @BeforeEach
    void init() {
        RouteConfiguration.forApplicationScope()
                .setAnnotatedRoute(DialogView.class);
        view = navigate(DialogView.class);
        dialog_ = wrap(view.dialog);
    }

    @Test
    void dialogOpen_dialogIsUsable() {
        dialog_.open();
        Assertions.assertTrue(dialog_.isUsable(),
                "Dialog should be attached on open");
    }

    @Test
    void modalDialog_blocksUIComponents() {
        dialog_.open();
        ButtonWrap<Button> button_ = wrap(view.button);
        Assertions.assertFalse(button_.isUsable(),
                "Dialog should be modal by default blocking button");

        dialog_.close();

        Assertions.assertTrue(button_.isUsable(),
                "Closing dialog should enable button");
    }

    @Test
    void nonModalDialog_UIComponentsUsable() {
        view.dialog.setModal(false);
        dialog_.open();
        ButtonWrap<Button> button_ = wrap(view.button);
        Assertions.assertTrue(button_.isUsable(),
                "Non-modal dialog should not block button");
    }

}
