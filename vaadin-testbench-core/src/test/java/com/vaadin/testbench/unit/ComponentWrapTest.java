/*
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.testbench.unit;

import com.example.base.Home;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;

public class ComponentWrapTest extends UIUnitTest {

    private Home home;

    @BeforeEach
    public void initHome() {
        home = getHome();
    }

    @Override
    protected String scanPackage() {
        return "com.example";
    }

    @Test
    public void canGetWrapperForView_viewIsUsable() {
        final ComponentWrap<Home> home_ = $(home);
        Assertions.assertTrue(home_.isUsable(),
                "Home should be visible and interactable");
    }

    @Test
    public void componentIsDisabled_isUsableReturnsFalse() {
        home.getElement().setEnabled(false);

        final ComponentWrap<Home> home_ = $(home);
        Assertions.assertFalse(home_.isUsable(),
                "Home should be visible and interactable");
    }

    @Test
    public void componentIsHidden_isUsableReturnsFalse() {
        home.setVisible(false);

        final ComponentWrap<Home> home_ = $(home);
        Assertions.assertFalse(home_.isUsable(),
                "Home should be visible and interactable");
    }

    @Test
    public void componentModality_componentIsUsableReturnsCorrectly() {
        final ComponentWrap<Home> home_ = $(home);

        final Span span = new Span();
        home.add(span);
        final ComponentWrap<Span> span_ = $(span);

        Assertions.assertTrue(span_.isUsable(),
                "Span should be attached to the ui");

        span_.setModal(true);

        Assertions.assertTrue(span_.isUsable(),
                "Span should interactable when it is modal");
        Assertions.assertFalse(home_.isUsable(),
                "Home should not be interactable when Span is modal");

        span_.setModal(false);

        Assertions.assertTrue(home_.isUsable(),
                "Home should be interactable when Span is not modal");
    }

    @Test
    public void componentModality_modalityDropsOnComponentRemoval() {
        final ComponentWrap<Home> home_ = $(home);

        final Span span = new Span();
        home.add(span);
        final ComponentWrap<Span> span_ = $(span);

        Assertions.assertTrue(span_.isUsable(),
                "Span should be attached to the ui");

        span_.setModal(true);

        Assertions.assertTrue(span_.isUsable(),
                "Span should interactable when it is modal");
        Assertions.assertFalse(home_.isUsable(),
                "Home should not be interactable when Span is modal");

        home.remove(span);

        // TODO: can we have this automated?
        UI.getCurrent().getInternals().getStateTree()
                .collectChanges(nodeChange -> {
                });

        Assertions.assertTrue(home_.isUsable(),
                "Home should be interactable when Span is removed");
    }

    @Test
    public void parentNotVisible_childIsNotInteractable() {
        final Span span = new Span();
        home.add(span);
        final ComponentWrap<Span> span_ = $(span);

        Assertions.assertTrue(span_.isUsable(),
                "Span should be attached to the ui");

        home.setVisible(false);

        Assertions.assertFalse(span_.isUsable(),
                "Span should not be interactable when parent is hidden");
    }

    private Home getHome() {
        final HasElement view = getCurrentView();
        Assertions.assertTrue(view instanceof Home,
                "Home should be navigated to by default");
        return (Home) view;
    }

    @Tag("span")
    public static class Span extends Component {
        // NOOP
    }
}
