/**
 * Copyright (C) 2000-2025 Vaadin Ltd
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
    void virtualList_initTester() {
        Assertions.assertNotNull($virtualList,
                "Tester for callback lit renderer VirtualList not initialized.");
    }

    @Test
    void getLitRendererPropertyValue_propertyValuesEqual() {
        var index = UserData.getAnyValidIndex();
        var user = UserData.get(index);

        var firstName = $virtualList.getLitRendererPropertyValue(index,
                "firstName", String.class);
        Assertions.assertEquals(user.getFirstName(), firstName);

        var lastName = $virtualList.getLitRendererPropertyValue(index,
                "lastName", String.class);
        Assertions.assertEquals(user.getLastName(), lastName);

        var active = $virtualList.getLitRendererPropertyValue(index,
                "active", Boolean.class);
        Assertions.assertEquals(user.isActive(), active);
    }

    @Test
    void getLitRendererPropertyValue_nonexistentPropertyFails() {
        var index = UserData.getAnyValidIndex();
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> $virtualList.getLitRendererPropertyValue(index,
                        "nonexistent", String.class),
                "Nonexistent property request should throw an exception");
    }

    @Test
    void getLitRendererPropertyValue_outOfBoundsIndexFails() {
        Assertions.assertThrows(IndexOutOfBoundsException.class,
                () -> $virtualList.getLitRendererPropertyValue( -1,
                        "firstName", String.class),
                "VirtualList index out of bounds (low)");

        Assertions.assertThrows(IndexOutOfBoundsException.class,
                () -> $virtualList.getLitRendererPropertyValue(UserData.USER_COUNT,
                        "firstName", String.class),
                "VirtualList index out of bounds (high)");
    }

    @Test
    void getLitRendererPropertyValue_hiddenFails() {
        $virtualList.getComponent().setVisible(false);

        var index = UserData.getAnyValidIndex();
        Assertions.assertThrows(IllegalStateException.class,
                () -> $virtualList.getLitRendererPropertyValue(index,
                        "firstName", String.class),
                "Tester should not be accessible for hidden virtual list");
    }

    @Test
    void invokeLitRendererFunction_actionSucceeds() {
        var index = UserData.getAnyValidIndex();
        var user = UserData.get(index);

        var originalActive = user.isActive();

        var beforeActive = $virtualList.getLitRendererPropertyValue(index,
                "active", Boolean.class);
        Assertions.assertEquals(user.isActive(), beforeActive);

        $virtualList.invokeLitRendererFunction(index, "onActiveToggleClick");

        var afterActive = $virtualList.getLitRendererPropertyValue(index,
                "active", Boolean.class);
        Assertions.assertEquals(user.isActive(), afterActive);

        Assertions.assertEquals(!originalActive, user.isActive());
    }

    @Test
    void invokeLitRendererFunction_nonexistentFunctionFails() {
        var index = UserData.getAnyValidIndex();
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> $virtualList.invokeLitRendererFunction(index,
                        "nonexistent"),
                "Nonexistent function invocation should throw an exception");
    }

    @Test
    void invokeLitRendererFunction_outOfBoundsIndexFails() {
        Assertions.assertThrows(IndexOutOfBoundsException.class,
                () -> $virtualList.invokeLitRendererFunction( -1,
                        "onActiveToggleClick"),
                "VirtualList index out of bounds (low)");

        Assertions.assertThrows(IndexOutOfBoundsException.class,
                () -> $virtualList.invokeLitRendererFunction(UserData.USER_COUNT,
                        "onActiveToggleClick"),
                "VirtualList index out of bounds (high)");
    }

    @Test
    void invokeLitRendererFunction_hiddenFails() {
        $virtualList.getComponent().setVisible(false);

        var index = UserData.getAnyValidIndex();
        Assertions.assertThrows(IllegalStateException.class,
                () -> $virtualList.invokeLitRendererFunction(index,
                        "firstName"),
                "Tester should not be accessible for hidden virtual list");
    }

}
