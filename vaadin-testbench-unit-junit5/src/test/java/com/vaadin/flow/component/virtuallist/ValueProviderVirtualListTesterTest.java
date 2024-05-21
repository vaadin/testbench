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
class ValueProviderVirtualListTesterTest extends UIUnitTest {

    private VirtualListTester<VirtualList<User>, User> $virtualList;

    @BeforeEach
    void init() {
        RouteConfiguration.forApplicationScope()
                .setAnnotatedRoute(ValueProviderVirtualListView.class);

        var view = navigate(ValueProviderVirtualListView.class);
        $virtualList = test(view.valueProviderVirtualList);
    }

    @Test
    void virtualList_verifyComponent() {
        Assertions.assertNotNull($virtualList,
                "Tester for value provider VirtualList not initialized.");
    }

    @Test
    void virtualList_verifySize() {
        Assertions.assertEquals(UserData.USER_COUNT, $virtualList.size());
    }

    @Test
    void virtualList_verifyFirstItemIndex() {
        var firstUser = UserData.first();
        Assertions.assertEquals(firstUser, $virtualList.getItem(0));
    }

    @Test
    void virtualList_verifyLastItemIndex() {
        var lastUser = UserData.last();
        Assertions.assertEquals(lastUser, $virtualList.getItem(UserData.USER_COUNT - 1));
    }

    @Test
    void virtualList_verifyUnderItemIndexFails() {
        Assertions.assertThrows(IndexOutOfBoundsException.class,
                () -> $virtualList.getItem(-1),
                "VirtualList index out of bounds (low)");
    }

    @Test
    void virtualList_verifyOverItemIndexFails() {
        Assertions.assertThrows(IndexOutOfBoundsException.class,
                () -> $virtualList.getItem(UserData.USER_COUNT),
                "VirtualList index out of bounds (high)");
    }

    @Test
    void virtualList_verifyHiddenFails() {
        $virtualList.getComponent().setVisible(false);

        Assertions.assertThrows(IllegalStateException.class,
                () -> $virtualList.getItem(0),
                "Tester should not be accessible for hidden virtual list");
    }

    @Test
    void virtualList_verifyFirstItemText() {
        var firstUser = UserData.first();
        Assertions.assertEquals(expectedValueProviderText(firstUser),
                $virtualList.getItemText(0));
    }

    @Test
    void virtualList_verifyLastItemText() {
        var lastUser = UserData.last();
        Assertions.assertEquals(expectedValueProviderText(lastUser),
                $virtualList.getItemText(UserData.USER_COUNT - 1));
    }

    @Test
    void virtualList_verifyNotComponentRenderer() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> $virtualList.getItemComponent(0),
                "valueProviderVirtualList should not have a ComponentRenderer");
    }

    private static String expectedValueProviderText(User user) {
        return String.join(" ",
                "Name:", user.getFirstName(), user.getLastName(),
                ";",
                "Active:", user.isActive() ? "Yes" : "No");
    }

}
