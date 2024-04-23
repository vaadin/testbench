/*
 * Copyright (C) 2000-2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.virtuallist;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.testbench.unit.UIUnitTest;
import com.vaadin.testbench.unit.ViewPackages;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@ViewPackages
class VirtualListTesterTest extends UIUnitTest {

    private VirtualListViewTester $virtualListView;

    @BeforeEach
    void init() {
        RouteConfiguration.forApplicationScope()
                .setAnnotatedRoute(VirtualListView.class);

        $virtualListView = test(VirtualListViewTester.class, navigate(VirtualListView.class));
    }

    @Test
    void virtualList_verifyComponents() {
        Assertions.assertNotNull($virtualListView,
                "Tester for VirtualListView not initialized.");

        var $valueProviderVirtualList = $virtualListView.$valueProviderVirtualList();
        Assertions.assertNotNull($valueProviderVirtualList,
                "Tester for value provider VirtualList not initialized.");

        var $componentRendererVirtualList = $virtualListView.$componentRendererVirtualList();
        Assertions.assertNotNull($componentRendererVirtualList,
                "Tester for component renderer VirtualList not initialized.");

        var $callbackLitRendererVirtualList = $virtualListView.$callbackLitRendererVirtualList();
        Assertions.assertNotNull($callbackLitRendererVirtualList,
                "Tester for callback lit renderer VirtualList not initialized.");
    }

    @Test
    void virtualList_verifyUserData() {
        var users = UserData.all();

        Assertions.assertEquals(UserData.USER_COUNT, users.size());
    }

    @Test
    void virtualList_verifyValueProvider() {
        var $valueProviderVirtualList = $virtualListView.$valueProviderVirtualList();

        Assertions.assertEquals(UserData.USER_COUNT, $valueProviderVirtualList.size());

        var firstUser = UserData.first();
        Assertions.assertEquals(firstUser, $valueProviderVirtualList.getItem(0));

        Assertions.assertEquals(expectedValueProviderText(firstUser),
                $valueProviderVirtualList.getItemText(0));

        var lastUser = UserData.last();
        Assertions.assertEquals(lastUser, $valueProviderVirtualList.getItem(UserData.USER_COUNT - 1));

        Assertions.assertEquals(expectedValueProviderText(lastUser),
                $valueProviderVirtualList.getItemText(UserData.USER_COUNT - 1));

        try {
            $valueProviderVirtualList.getItemComponent(0);
            Assertions.fail("valueProviderVirtualList should not have a ComponentRenderer");
        } catch (IllegalArgumentException ignore) {
            //
        }
    }

    @NotNull
    private static String expectedValueProviderText(User user) {
        return String.join(" ",
                "Name:", user.getFirstName(), user.getLastName(),
                ";",
                "Active:", user.isActive() ? "Yes" : "No");
    }

    @Test
    void virtualList_verifyComponentRenderer() {
        var $componentRendererVirtualList = $virtualListView.$componentRendererVirtualList();

        Assertions.assertEquals(UserData.USER_COUNT, $componentRendererVirtualList.size());

        var firstUser = UserData.first();
        Assertions.assertEquals(firstUser, $componentRendererVirtualList.getItem(0));

        Assertions.assertEquals(expectedRendererText(firstUser),
                $componentRendererVirtualList.getItemText(0));

        var lastUser = UserData.last();
        Assertions.assertEquals(lastUser, $componentRendererVirtualList.getItem(UserData.USER_COUNT - 1));

        Assertions.assertEquals(expectedRendererText(lastUser),
                $componentRendererVirtualList.getItemText(UserData.USER_COUNT - 1));


        var itemComponent = test((Div) $componentRendererVirtualList.getItemComponent(0));

        var $firstNameSpan = $virtualListView.$firstNameSpanFor(itemComponent);
        Assertions.assertEquals(firstUser.getFirstName(), $firstNameSpan.getText());

        var $lastNameSpan = $virtualListView.$lastNameSpanFor(itemComponent);
        Assertions.assertEquals(firstUser.getLastName(), $lastNameSpan.getText());

        var originalActive = firstUser.isActive();
        var $activeSpan = $virtualListView.$activeSpanFor(itemComponent);
        Assertions.assertEquals(originalActive ? "Yes" : "No", $activeSpan.getText());

        $virtualListView.$activeToggleButtonFor(itemComponent).click();
        var active = firstUser.isActive();
        Assertions.assertEquals(active ? "Yes" : "No", $activeSpan.getText());
        Assertions.assertEquals(!originalActive, active);

    }

    @Test
    void virtualList_verifyCallbackLitRenderer() {
        var $callbackLitRendererVirtualList = $virtualListView.$callbackLitRendererVirtualList();

        Assertions.assertEquals(UserData.USER_COUNT, $callbackLitRendererVirtualList.size());

        var firstUser = UserData.first();
        Assertions.assertEquals(firstUser, $callbackLitRendererVirtualList.getItem(0));

        var lastUser = UserData.last();
        Assertions.assertEquals(lastUser, $callbackLitRendererVirtualList.getItem(UserData.USER_COUNT - 1));


        var firstName = $callbackLitRendererVirtualList.getLitRendererPropertyValue(0, "firstName", String.class);
        Assertions.assertEquals(firstUser.getFirstName(), firstName);

        var lastName = $callbackLitRendererVirtualList.getLitRendererPropertyValue(0, "lastName", String.class);
        Assertions.assertEquals(firstUser.getLastName(), lastName);

        var originalActive = firstUser.isActive();
        var active = $callbackLitRendererVirtualList.getLitRendererPropertyValue(0, "active", Boolean.class);
        Assertions.assertEquals(originalActive, active);

        $callbackLitRendererVirtualList.invokeLitRendererFunction(0, "onActiveToggleClick");
        active = $callbackLitRendererVirtualList.getLitRendererPropertyValue(0, "active", Boolean.class);
        Assertions.assertEquals(!originalActive, active);
    }

    @NotNull
    private static String expectedRendererText(User user) {
        return String.join("",
                "Name:", user.getFirstName(), user.getLastName(),
                ";",
                "Active:", user.isActive() ? "Yes" : "No",
                "Toggle");
    }

}
