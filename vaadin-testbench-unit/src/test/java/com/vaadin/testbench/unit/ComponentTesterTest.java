/**
 * Copyright (C) 2000-2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.unit;

import java.util.List;
import java.util.Optional;

import com.example.base.WelcomeView;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.Tag;

@ViewPackages(packages = "com.example")
public class ComponentTesterTest extends UIUnit4Test {

    private WelcomeView home;

    @Before
    public void initHome() {
        home = getHome();
    }

    @Test
    public void canGetWrapperForView_viewIsUsable() {
        final ComponentTester<WelcomeView> home_ = test(home);
        Assert.assertTrue("Home should be visible and interactable",
                home_.isUsable());
    }

    @Test
    public void componentIsDisabled_isUsableReturnsFalse() {
        home.getElement().setEnabled(false);

        final ComponentTester<WelcomeView> home_ = test(home);
        Assert.assertFalse("Home should be visible but not interactable",
                home_.isUsable());
    }

    @Test
    public void componentIsHidden_isUsableReturnsFalse() {
        home.setVisible(false);

        final ComponentTester<WelcomeView> home_ = test(home);
        Assert.assertFalse(
                "Home should not be interactable when component is not visible",
                home_.isUsable());
    }

    @Test
    public void componentModality_componentIsUsableReturnsCorrectly() {
        final ComponentTester<WelcomeView> home_ = test(home);

        final Span span = new Span();
        home.add(span);
        final ComponentTester<Span> span_ = test(span);

        Assert.assertTrue("Span should be attached to the ui",
                span_.isUsable());

        span_.setModal(true);

        Assert.assertTrue("Span should interactable when it is modal",
                span_.isUsable());
        Assert.assertFalse("Home should not be interactable when Span is modal",
                home_.isUsable());

        span_.setModal(false);

        Assert.assertTrue("Home should be interactable when Span is not modal",
                home_.isUsable());
    }

    @Test
    public void componentModality_modalityDropsOnComponentRemoval() {
        final ComponentTester<WelcomeView> home_ = test(home);

        final Span span = new Span();
        home.add(span);
        final ComponentTester<Span> span_ = test(span);

        Assert.assertTrue("Span should be attached to the ui",
                span_.isUsable());

        span_.setModal(true);

        Assert.assertTrue("Span should be interactable when it is modal",
                span_.isUsable());
        Assert.assertFalse("Home should not be interactable when Span is modal",
                home_.isUsable());

        home.remove(span);

        Assert.assertTrue("Home should be interactable when Span is removed",
                home_.isUsable());
    }

    @Test
    public void parentNotVisible_childIsNotInteractable() {
        final Span span = new Span();
        home.add(span);
        final ComponentTester<Span> span_ = test(span);

        Assert.assertTrue("Span should be attached to the ui",
                span_.isUsable());

        home.setVisible(false);

        Assert.assertFalse(
                "Span should not be interactable when parent is hidden",
                span_.isUsable());
    }

    @Test
    public void nonAttachedComponent_isNotInteractable() {
        Span span = new Span();

        ComponentTester<Span> span_ = test(span);

        Assert.assertFalse("Span is not attached so it is not usable.",
                span_.isUsable());
    }

    @Test
    public void findByQuery_matchingComponent_getsComponent() {
        Span one = new Span("One");
        Span two = new Span("Two");
        Div container = new Div(new Div(new Div(one)), new Div(two), new Div());

        ComponentTester<Div> wrapper_ = test(container);

        Optional<Span> result = wrapper_.findByQuery(Span.class,
                query -> query.withText("One"));
        Assert.assertTrue(result.isPresent());
        Assert.assertSame(one, result.get());

        result = wrapper_.findByQuery(Span.class,
                query -> query.withText("Two"));
        Assert.assertTrue(result.isPresent());
        Assert.assertSame(two, result.get());
    }

    @Test
    public void findByQuery_notMatchingComponent_empty() {
        Span one = new Span("One");
        Span two = new Span("Two");
        Div container = new Div(new Div(new Div(one)), new Div(two), new Div());

        ComponentTester<Div> wrapper_ = test(container);

        Optional<Span> result = wrapper_.findByQuery(Span.class,
                query -> query.withText("Three"));
        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void findByQuery_multipleMatchingComponents_throws() {
        Span one = new Span("Span One");
        Span two = new Span("Span Two");
        Div container = new Div(new Div(new Div(one)), new Div(two), new Div());

        ComponentTester<Div> wrapper_ = test(container);

        Assert.assertThrows(IllegalArgumentException.class,
                () -> wrapper_.findByQuery(Span.class,
                        query -> query.withTextContaining("Span")));
    }

    @Test
    public void findAllByQuery_matchingComponent_getsComponents() {
        Span one = new Span("Span One");
        Span two = new Span("Span Two");
        Span three = new Span("Span Two bis");
        Div container = new Div(new Div(new Div(one)), new Div(two),
                new Div(three));

        ComponentTester<Div> wrapper_ = test(container);

        List<Span> result = wrapper_.findAllByQuery(Span.class,
                query -> query.withTextContaining("One"));
        Assert.assertEquals(List.of(one), result);

        result = wrapper_.findAllByQuery(Span.class,
                query -> query.withTextContaining("Two"));
        Assert.assertEquals(List.of(two, three), result);

        result = wrapper_.findAllByQuery(Span.class,
                query -> query.withTextContaining("Span"));
        Assert.assertEquals(List.of(one, two, three), result);
    }

    @Test
    public void findAllByQuery_notMatchingComponent_empty() {
        Span one = new Span("Span One");
        Span two = new Span("Span Two");
        Span three = new Span("Span Two bis");
        Div container = new Div(new Div(new Div(one)), new Div(two),
                new Div(three));

        ComponentTester<Div> wrapper_ = test(container);

        List<Span> result = wrapper_.findAllByQuery(Span.class,
                query -> query.withTextContaining("Three"));
        Assert.assertTrue(result.isEmpty());
    }

    private WelcomeView getHome() {
        final HasElement view = getCurrentView();
        Assert.assertTrue("Home should be navigated to by default",
                view instanceof WelcomeView);
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
