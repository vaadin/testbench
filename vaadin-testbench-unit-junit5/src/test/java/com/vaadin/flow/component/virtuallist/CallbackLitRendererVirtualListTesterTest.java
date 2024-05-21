/*
 * Copyright (C) 2000-2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.virtuallist;

import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.testbench.unit.UIUnitTest;
import com.vaadin.testbench.unit.ViewPackages;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@ViewPackages
class CallbackLitRendererVirtualListTesterTest extends UIUnitTest {

    private VirtualListTester<VirtualList<User>, User> $virtualList;

    @BeforeEach
    void init() {
        RouteConfiguration.forApplicationScope()
                .setAnnotatedRoute(CallbackLitRendererVirtualListView.class);

        var view = navigate(CallbackLitRendererVirtualListView.class);
        $virtualList = test(view.callbackLitRendererVirtualList);
    }

    @Test
    void virtualList_verifyComponent() {
        Assertions.assertNotNull($virtualList,
                "Tester for callback lit renderer VirtualList not initialized.");
    }

    @Test
    void virtualList_verifyFirstItemText() {
        var firstUser = UserData.first();
        Assertions.assertEquals(firstUser, $virtualList.getItem(0));
    }

    @Test
    void virtualList_verifyLastItemText() {
        var lastUser = UserData.last();
        Assertions.assertEquals(lastUser, $virtualList.getItem(UserData.USER_COUNT - 1));
    }

    @Test
    void virtualList_verifyUnderItemTextFails() {
        Assertions.assertThrows(IndexOutOfBoundsException.class,
                () -> $virtualList.getItem( -1),
                "VirtualList index out of bounds (low)");
    }

    @Test
    void virtualList_verifyOverItemTextFails() {
        Assertions.assertThrows(IndexOutOfBoundsException.class,
                () -> $virtualList.getItem(UserData.USER_COUNT),
                "VirtualList index out of bounds (high)");
    }

    @Test
    void virtualList_verifyHiddenItemTextFails() {
        $virtualList.getComponent().setVisible(false);

        Assertions.assertThrows(IllegalStateException.class,
                () -> $virtualList.getItem(UserData.USER_COUNT),
                "Tester should not be accessible for hidden virtual list");
    }

    @Test
    void virtualList_verifyFirstNameProperty() {
        var firstUser = UserData.first();

        var firstName = $virtualList.getLitRendererPropertyValue(0, "firstName", String.class);
        Assertions.assertEquals(firstUser.getFirstName(), firstName);
    }

    @Test
    void virtualList_verifyLastNameProperty() {
        var firstUser = UserData.first();

        var lastName = $virtualList.getLitRendererPropertyValue(0, "lastName", String.class);
        Assertions.assertEquals(firstUser.getLastName(), lastName);
    }

    @Test
    void virtualList_verifyNonexistentPropertyFails() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> $virtualList.getLitRendererPropertyValue(0, "nonexistent", String.class),
                "Nonexistent property request should throw an exception");
    }

    @Test
    void virtualList_verifyActivePropertyAndToggleButton() {
        var firstUser = UserData.first();

        var originalActive = firstUser.isActive();

        var beforeActive = $virtualList.getLitRendererPropertyValue(0, "active", Boolean.class);
        Assertions.assertEquals(firstUser.isActive(), beforeActive);

        $virtualList.invokeLitRendererFunction(0, "onActiveToggleClick");

        var afterActive = $virtualList.getLitRendererPropertyValue(0, "active", Boolean.class);
        Assertions.assertEquals(firstUser.isActive(), afterActive);

        Assertions.assertEquals(!originalActive, firstUser.isActive());
    }

    @Test
    void virtualList_verifyNonexistentFunctionFails() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> $virtualList.invokeLitRendererFunction(0, "nonexistent"),
                "Nonexistent function invocation should throw an exception");
    }

}
