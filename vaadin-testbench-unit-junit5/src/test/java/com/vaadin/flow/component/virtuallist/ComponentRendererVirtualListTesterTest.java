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
    void virtualList_verifyComponent() {
        Assertions.assertNotNull($virtualList,
                "Tester for component renderer VirtualList not initialized.");
    }

    @Test
    void virtualList_verifyFirstItemText() {
        var firstUser = UserData.first();
        Assertions.assertEquals(expectedRendererText(firstUser),
                $virtualList.getItemText(0));
    }

    @Test
    void virtualList_verifyLastItemText() {
        var lastUser = UserData.last();
        Assertions.assertEquals(expectedRendererText(lastUser),
                $virtualList.getItemText(UserData.USER_COUNT - 1));
    }

    @Test
    void virtualList_verifyItemComponent() {
        var $itemComponent = test((Div) $virtualList.getItemComponent(0));
        Assertions.assertNotNull($itemComponent, "Item component should not be null");
    }

    @Test
    void virtualList_verifyItemComponent_firstNameSpan() {
        var firstUser = UserData.first();

        var $itemComponent = test((Div) $virtualList.getItemComponent(0));

        var $firstNameSpan = test($itemComponent.find(Span.class)
                .withId("first-name")
                .single());
        // use of SpanTester will fail due to component being a copy and thus not attached
//        Assertions.assertEquals(firstUser.getFirstName(), $firstNameSpan.getText());
        Assertions.assertThrows(IllegalStateException.class,
                $firstNameSpan::getText,
                "Component is a copy and not usable via a ComponentTester");
        var firstNameSpan = $firstNameSpan.getComponent();
        Assertions.assertEquals(firstUser.getFirstName(), firstNameSpan.getText());
    }

    @Test
    void virtualList_verifyItemComponent_lastNameSpan() {
        var firstUser = UserData.first();

        var $itemComponent = test((Div) $virtualList.getItemComponent(0));

        var $lastNameSpan = test($itemComponent.find(Span.class)
                .withId("last-name")
                .single());
        // use of SpanTester will fail due to component being a copy and thus not attached
//        Assertions.assertEquals(firstUser.getLastName(), $lastNameSpan.getText());
        Assertions.assertThrows(IllegalStateException.class,
                $lastNameSpan::getText,
                "Component is a copy and not usable via a ComponentTester");
        var lastNameSpan = $lastNameSpan.getComponent();
        Assertions.assertEquals(firstUser.getLastName(), lastNameSpan.getText());
    }

    // this is a more complicated test
    // because it tests that the button toggles the state of the active indicator
    @Test
    void virtualList_verifyItemComponent_activeSpanAndToggleButton() {
        var firstUser = UserData.first();

        // BEFORE
        var $beforeItemComponent = test((Div) $virtualList.getItemComponent(0));

        var beforeActive = firstUser.isActive();
        var $beforeActiveSpan = test($beforeItemComponent.find(Span.class)
                .withId("active")
                .single());
        // use of SpanTester will fail due to component being a copy and thus not attached
//        Assertions.assertEquals(beforeActive ? "Yes" : "No", $beforeActiveSpan.getText());
        Assertions.assertThrows(IllegalStateException.class,
                $beforeActiveSpan::getText,
                "Component is a copy and not usable via a ComponentTester");
        var beforeActiveSpan = $beforeActiveSpan.getComponent();
        Assertions.assertEquals(beforeActive ? "Yes" : "No", beforeActiveSpan.getText());

        // TOGGLE
        var $toggleButton = test($beforeItemComponent.find(NativeButton.class)
                .withText("Toggle")
                .single());
        // use of NativeButtonTester will fail due to component being a copy and thus not attached
//        $toggleButton.click();
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
        var $afterItemComponent = test((Div) $virtualList.getItemComponent(0));

        var afterActive = firstUser.isActive();
        var $afterActiveSpan = test($afterItemComponent.find(Span.class)
                .withId("active")
                .single());
        // use of SpanTester will fail due to component being a copy and thus not attached
//        Assertions.assertEquals(afterActive ? "Yes" : "No", $afterActiveSpan.getText());
        var afterActiveSpan = $afterActiveSpan.getComponent();
        Assertions.assertEquals(afterActive ? "Yes" : "No", afterActiveSpan.getText());
        Assertions.assertEquals(!beforeActive, afterActive);
    }

    @Test
    void virtualList_verifyItemComponent_underIndexFails() {
        Assertions.assertThrows(IndexOutOfBoundsException.class,
                () -> $virtualList.getItemComponent(-1),
                "VirtualList index out of bounds (low)");
    }

    @Test
    void virtualList_verifyItemComponent_overIndexFails() {
        Assertions.assertThrows(IndexOutOfBoundsException.class,
                () -> $virtualList.getItemComponent(UserData.USER_COUNT),
                "VirtualList index out of bounds (high)");
    }

    @Test
    void virtualList_verifyHiddenFails() {
        $virtualList.getComponent().setVisible(false);

        Assertions.assertThrows(IllegalStateException.class,
                () -> $virtualList.getItemComponent(0),
                "Item component should not be accessible for hidden virtual list");
    }

    private static String expectedRendererText(User user) {
        return String.join("",
                "Name:", user.getFirstName(), user.getLastName(),
                ";",
                "Active:", user.isActive() ? "Yes" : "No",
                "Toggle");
    }

}
