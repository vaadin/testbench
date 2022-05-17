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

import com.example.base.WelcomeView;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.Tag;

public class ComponentWrapTest extends UIUnitTest {

    private WelcomeView home;

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
        final ComponentWrap<WelcomeView> home_ = $(home);
        Assertions.assertTrue(home_.isUsable(),
                "Home should be visible and interactable");
    }

    @Test
    public void componentIsDisabled_isUsableReturnsFalse() {
        home.getElement().setEnabled(false);

        final ComponentWrap<WelcomeView> home_ = $(home);
        Assertions.assertFalse(home_.isUsable(),
                "Home should be visible but not interactable");
    }

    @Test
    public void componentIsHidden_isUsableReturnsFalse() {
        home.setVisible(false);

        final ComponentWrap<WelcomeView> home_ = $(home);
        Assertions.assertFalse(home_.isUsable(),
                "Home should not be interactable when component is not visible");
    }

    @Test
    public void componentModality_componentIsUsableReturnsCorrectly() {
        final ComponentWrap<WelcomeView> home_ = $(home);

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
        final ComponentWrap<WelcomeView> home_ = $(home);

        final Span span = new Span();
        home.add(span);
        final ComponentWrap<Span> span_ = $(span);

        Assertions.assertTrue(span_.isUsable(),
                "Span should be attached to the ui");

        span_.setModal(true);

        Assertions.assertTrue(span_.isUsable(),
                "Span should be interactable when it is modal");
        Assertions.assertFalse(home_.isUsable(),
                "Home should not be interactable when Span is modal");

        home.remove(span);

        // TODO: can we have this automated?
        ComponentWrap.flushChanges();

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

    @Test
    public void nonAttachedComponent_isNotInteractable() {
        Span span = new Span();

        ComponentWrap<Span> span_ = $(span);

        Assertions.assertFalse(span_.isUsable(),
                "Span is not attached so it is not usable.");
    }

    private WelcomeView getHome() {
        final HasElement view = getCurrentView();
        Assertions.assertTrue(view instanceof WelcomeView,
                "Home should be navigated to by default");
        return (WelcomeView) view;
    }

    @Tag("span")
    public static class Span extends Component {
        // NOOP
    }
}
