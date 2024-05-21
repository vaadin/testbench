/*
 * Copyright (C) 2000-2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.virtuallist;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.testbench.unit.UIUnitTest;
import com.vaadin.testbench.unit.ViewPackages;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@ViewPackages
class ComponentRendererVirtualListTesterTest extends UIUnitTest {

    private VirtualListTester<VirtualList<User>, User> $virtualList;

    @BeforeEach
    void init() {
        RouteConfiguration.forApplicationScope()
                .setAnnotatedRoute(ComponentRendererVirtualListView.class);

        var view = navigate(ComponentRendererVirtualListView.class);
        $virtualList = test(view.componentRendererVirtualList);
    }

    @Test
    void virtualList_initTester() {
        Assertions.assertNotNull($virtualList,
                "Tester for component renderer VirtualList not initialized.");
    }

    @Test
    void getItemText_existsAndEquals() {
        var firstUser = UserData.first();
        Assertions.assertEquals(expectedRendererText(firstUser),
                $virtualList.getItemText(0));

        var index = UserData.getAnyValidIndex();
        var anyUser = UserData.get(index);
        Assertions.assertEquals(expectedRendererText(anyUser),
                $virtualList.getItemText(index));

        var lastUser = UserData.last();
        Assertions.assertEquals(expectedRendererText(lastUser),
                $virtualList.getItemText(UserData.USER_COUNT - 1));
    }

    @Test
    void getItemComponent_exists() {
        var $itemComponent = test((Div) $virtualList.getItemComponent(UserData.getAnyValidIndex()));
        Assertions.assertNotNull($itemComponent, "Item component should not be null");
    }

    @Test
    void getItemComponent_hasExpectedChildrenAndValues() {
        var index = UserData.getAnyValidIndex();
        var user = UserData.get(index);

        var $itemComponent = test((Div) $virtualList.getItemComponent(index));

        var $firstNameSpan = test($itemComponent.find(Span.class)
                .withId("first-name")
                .single());
        Assertions.assertThrows(IllegalStateException.class,
                $firstNameSpan::getText,
                "Component is a copy and not usable via a ComponentTester");
        var firstNameSpan = $firstNameSpan.getComponent();
        Assertions.assertEquals(user.getFirstName(), firstNameSpan.getText());

        var $lastNameSpan = test($itemComponent.find(Span.class)
                .withId("last-name")
                .single());
        Assertions.assertThrows(IllegalStateException.class,
                $lastNameSpan::getText,
                "Component is a copy and not usable via a ComponentTester");
        var lastNameSpan = $lastNameSpan.getComponent();
        Assertions.assertEquals(user.getLastName(), lastNameSpan.getText());

        var $activeSpan = test($itemComponent.find(Span.class)
                .withId("active")
                .single());
        Assertions.assertThrows(IllegalStateException.class,
                $activeSpan::getText,
                "Component is a copy and not usable via a ComponentTester");
        var activeSpan = $activeSpan.getComponent();
        Assertions.assertEquals(user.isActive() ? "Yes" : "No", activeSpan.getText());
    }

    // this is a more complicated test
    // because it tests that the button toggles the state of the active indicator
    @Test
    void getItemComponent_buttonActionsFire() {
        var index = UserData.getAnyValidIndex();
        var user = UserData.get(index);

        // BEFORE
        var $beforeItemComponent = test((Div) $virtualList.getItemComponent(index));

        var beforeActive = user.isActive();
        var beforeActiveSpan = $beforeItemComponent.find(Span.class)
                .withId("active")
                .single();
        Assertions.assertEquals(beforeActive ? "Yes" : "No", beforeActiveSpan.getText());

        // TOGGLE
        var $toggleButton = test($beforeItemComponent.find(NativeButton.class)
                .withText("Toggle")
                .single());
        Assertions.assertThrows(IllegalStateException.class,
                $toggleButton::click,
                "Component is a copy and not usable via a ComponentTester");
        var toggleButton = $toggleButton.getComponent();
        ComponentUtil.fireEvent(toggleButton,
                new ClickEvent<>(toggleButton, true,
                        0, 0, 0, 0,
                        0, 0,
                        false, false, false, false));

        // AFTER
        // re-obtain the item component as the item has changed
        var $afterItemComponent = test((Div) $virtualList.getItemComponent(index));

        var afterActive = user.isActive();
        var afterActiveSpan = $afterItemComponent.find(Span.class)
                .withId("active")
                .single();
        Assertions.assertEquals(afterActive ? "Yes" : "No", afterActiveSpan.getText());
        Assertions.assertEquals(!beforeActive, afterActive);
    }

    @Test
    void getItemComponent_outOfBoundsIndexFails() {
        Assertions.assertThrows(IndexOutOfBoundsException.class,
                () -> $virtualList.getItemComponent( -1),
                "VirtualList index out of bounds (low)");

        Assertions.assertThrows(IndexOutOfBoundsException.class,
                () -> $virtualList.getItemComponent(UserData.USER_COUNT),
                "VirtualList index out of bounds (high)");
    }

    @Test
    void getItemComponent_hiddenFails() {
        $virtualList.getComponent().setVisible(false);

        var index = UserData.getAnyValidIndex();
        Assertions.assertThrows(IllegalStateException.class,
                () -> $virtualList.getItemComponent(index),
                "Tester should not be accessible for hidden virtual list");
    }

    private static String expectedRendererText(User user) {
        return String.join("",
                "Name:", user.getFirstName(), user.getLastName(),
                ";",
                "Active:", user.isActive() ? "Yes" : "No",
                "Toggle");
    }

}
