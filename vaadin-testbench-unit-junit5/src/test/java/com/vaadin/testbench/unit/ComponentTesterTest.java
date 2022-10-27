/**
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.testbench.unit;

import java.util.List;
import java.util.Optional;

import com.example.base.WelcomeView;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.Tag;

@ViewPackages(packages = "com.example")
public class ComponentTesterTest extends UIUnitTest {

    private WelcomeView home;

    @BeforeEach
    public void initHome() {
        home = getHome();
    }

    @Test
    public void canGetWrapperForView_viewIsUsable() {
        final ComponentTester<WelcomeView> home_ = test(home);
        Assertions.assertTrue(home_.isUsable(),
                "Home should be visible and interactable");
    }

    @Test
    public void componentIsDisabled_isUsableReturnsFalse() {
        home.getElement().setEnabled(false);

        final ComponentTester<WelcomeView> home_ = test(home);
        Assertions.assertFalse(home_.isUsable(),
                "Home should be visible but not interactable");
    }

    @Test
    public void componentIsHidden_isUsableReturnsFalse() {
        home.setVisible(false);

        final ComponentTester<WelcomeView> home_ = test(home);
        Assertions.assertFalse(home_.isUsable(),
                "Home should not be interactable when component is not visible");
    }

    @Test
    public void componentModality_componentIsUsableReturnsCorrectly() {
        final ComponentTester<WelcomeView> home_ = test(home);

        final Span span = new Span();
        home.add(span);
        final ComponentTester<Span> span_ = test(span);

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
        final ComponentTester<WelcomeView> home_ = test(home);

        final Span span = new Span();
        home.add(span);
        final ComponentTester<Span> span_ = test(span);

        Assertions.assertTrue(span_.isUsable(),
                "Span should be attached to the ui");

        span_.setModal(true);

        Assertions.assertTrue(span_.isUsable(),
                "Span should be interactable when it is modal");
        Assertions.assertFalse(home_.isUsable(),
                "Home should not be interactable when Span is modal");

        home.remove(span);

        Assertions.assertTrue(home_.isUsable(),
                "Home should be interactable when Span is removed");
    }

    @Test
    public void parentNotVisible_childIsNotInteractable() {
        final Span span = new Span();
        home.add(span);
        final ComponentTester<Span> span_ = test(span);

        Assertions.assertTrue(span_.isUsable(),
                "Span should be attached to the ui");

        home.setVisible(false);

        Assertions.assertFalse(span_.isUsable(),
                "Span should not be interactable when parent is hidden");
    }

    @Test
    public void nonAttachedComponent_isNotInteractable() {
        Span span = new Span();

        ComponentTester<Span> span_ = test(span);

        Assertions.assertFalse(span_.isUsable(),
                "Span is not attached so it is not usable.");
    }

    @Test
    void findByQuery_matchingComponent_getsComponent() {
        Span one = new Span("One");
        Span two = new Span("Two");
        Div container = new Div(new Div(new Div(one)), new Div(two), new Div());

        ComponentTester<Div> wrapper_ = test(container);

        Optional<Span> result = wrapper_.findByQuery(Span.class,
                query -> query.withText("One"));
        Assertions.assertTrue(result.isPresent());
        Assertions.assertSame(one, result.get());

        result = wrapper_.findByQuery(Span.class,
                query -> query.withText("Two"));
        Assertions.assertTrue(result.isPresent());
        Assertions.assertSame(two, result.get());
    }

    @Test
    void findByQuery_notMatchingComponent_empty() {
        Span one = new Span("One");
        Span two = new Span("Two");
        Div container = new Div(new Div(new Div(one)), new Div(two), new Div());

        ComponentTester<Div> wrapper_ = test(container);

        Optional<Span> result = wrapper_.findByQuery(Span.class,
                query -> query.withText("Three"));
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void findByQuery_multipleMatchingComponents_throws() {
        Span one = new Span("Span One");
        Span two = new Span("Span Two");
        Div container = new Div(new Div(new Div(one)), new Div(two), new Div());

        ComponentTester<Div> wrapper_ = test(container);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> wrapper_.findByQuery(Span.class,
                        query -> query.withTextContaining("Span")));
    }

    @Test
    void findAllByQuery_matchingComponent_getsComponents() {
        Span one = new Span("Span One");
        Span two = new Span("Span Two");
        Span three = new Span("Span Two bis");
        Div container = new Div(new Div(new Div(one)), new Div(two),
                new Div(three));

        ComponentTester<Div> wrapper_ = test(container);

        List<Span> result = wrapper_.findAllByQuery(Span.class,
                query -> query.withTextContaining("One"));
        Assertions.assertIterableEquals(List.of(one), result);

        result = wrapper_.findAllByQuery(Span.class,
                query -> query.withTextContaining("Two"));
        Assertions.assertIterableEquals(List.of(two, three), result);

        result = wrapper_.findAllByQuery(Span.class,
                query -> query.withTextContaining("Span"));
        Assertions.assertIterableEquals(List.of(one, two, three), result);
    }

    @Test
    void findAllByQuery_notMatchingComponent_empty() {
        Span one = new Span("Span One");
        Span two = new Span("Span Two");
        Span three = new Span("Span Two bis");
        Div container = new Div(new Div(new Div(one)), new Div(two),
                new Div(three));

        ComponentTester<Div> wrapper_ = test(container);

        List<Span> result = wrapper_.findAllByQuery(Span.class,
                query -> query.withTextContaining("Three"));
        Assertions.assertTrue(result.isEmpty());
    }

    private WelcomeView getHome() {
        final HasElement view = getCurrentView();
        Assertions.assertTrue(view instanceof WelcomeView,
                "Home should be navigated to by default");
        return (WelcomeView) view;
    }

    @Tag("span")
    public static class Span extends Component implements HasText {
        public Span() {
        }

        public Span(String text) {
            setText(text);
        }
    }

    @Tag("div")
    public static class Div extends Component implements HasComponents {
        public Div(Component... components) {
            add(components);
        }
    }

}
