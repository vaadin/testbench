/**
 * Copyright (C) 2000-2025 Vaadin Ltd
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

    @Test
    void componentTester_click_firesClickEvent() {
        // Create a simple component to test clicking
        Span span = new Span("Click me");
        boolean[] clicked = {false};
        
        // Add a click listener to the component
        span.addClickListener(event -> {
            clicked[0] = true;
            Assertions.assertTrue(event.isFromClient(),
                    "Click event should be marked as from client");
        });
        
        // Add to view so it's usable
        home.add(span);
        
        ComponentTester<Span> spanTester = test(span);
        
        // Verify the component is usable
        Assertions.assertTrue(spanTester.isUsable(),
                "Span should be usable when attached");
        
        // Test the click functionality
        spanTester.click();
        
        // Verify the click event was fired
        Assertions.assertTrue(clicked[0], "Click event should have been fired");
    }

    @Test
    void componentTester_clickWithMetaKeys_firesClickEventWithMetaKeys() {
        // Create a simple component to test clicking with meta keys
        Span span = new Span("Click me with meta keys");
        boolean[] clickedWithCtrl = {false};
        
        // Add a click listener to check meta keys
        span.addClickListener(event -> {
            clickedWithCtrl[0] = event.isCtrlKey();
        });
        
        // Add to view so it's usable
        home.add(span);
        
        ComponentTester<Span> spanTester = test(span);
        
        // Test the click functionality with meta keys
        MetaKeys metaKeys = new MetaKeys().ctrl();
        spanTester.click(metaKeys);
        
        // Verify the click event was fired with ctrl key
        Assertions.assertTrue(clickedWithCtrl[0], 
                "Click event should have been fired with ctrl key pressed");
    }

    @Test
    void componentTester_middleClick_firesMiddleClickEvent() {
        // Create a simple component to test middle clicking
        Span span = new Span("Middle click me");
        int[] buttonPressed = {-1};
        
        // Add a click listener to check button
        span.addClickListener(event -> {
            buttonPressed[0] = event.getButton();
        });
        
        // Add to view so it's usable
        home.add(span);
        
        ComponentTester<Span> spanTester = test(span);
        
        // Test the middle click functionality
        spanTester.middleClick();
        
        // Verify the middle click event was fired (button 1)
        Assertions.assertEquals(1, buttonPressed[0], 
                "Middle click should use button 1");
    }

    @Test
    void componentTester_rightClick_firesRightClickEvent() {
        // Create a simple component to test right clicking
        Span span = new Span("Right click me");
        int[] buttonPressed = {-1};
        
        // Add a click listener to check button
        span.addClickListener(event -> {
            buttonPressed[0] = event.getButton();
        });
        
        // Add to view so it's usable
        home.add(span);
        
        ComponentTester<Span> spanTester = test(span);
        
        // Test the right click functionality
        spanTester.rightClick();
        
        // Verify the right click event was fired (button 2)
        Assertions.assertEquals(2, buttonPressed[0], 
                "Right click should use button 2");
    }

    @Test
    void componentTester_clickNotUsableComponent_throwsException() {
        // Create a component that is not usable
        Span span = new Span("Not usable");
        // Don't add to view, so it's not attached/usable
        
        ComponentTester<Span> spanTester = test(span);
        
        // Verify the component is not usable
        Assertions.assertFalse(spanTester.isUsable(),
                "Span should not be usable when not attached");
        
        // Test that clicking throws an exception
        Assertions.assertThrows(IllegalStateException.class, () -> {
            spanTester.click();
        }, "Clicking non-usable component should throw IllegalStateException");
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
