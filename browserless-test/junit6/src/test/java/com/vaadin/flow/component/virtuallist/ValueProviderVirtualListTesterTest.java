/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vaadin.flow.component.virtuallist;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vaadin.browserless.BrowserlessTest;
import com.vaadin.browserless.ViewPackages;
import com.vaadin.flow.router.RouteConfiguration;

@ViewPackages
class ValueProviderVirtualListTesterTest extends BrowserlessTest {

    private VirtualListTester<VirtualList<User>, User> $virtualList;

    @BeforeEach
    void init() {
        RouteConfiguration.forApplicationScope()
                .setAnnotatedRoute(ValueProviderVirtualListView.class);

        var view = navigate(ValueProviderVirtualListView.class);
        $virtualList = test(view.valueProviderVirtualList);
    }

    @Test
    void virtualList_initTester() {
        Assertions.assertNotNull($virtualList,
                "Tester for value provider VirtualList not initialized.");
    }

    @Test
    void size_equalsItemCount() {
        Assertions.assertEquals(UserData.USER_COUNT, $virtualList.size());
    }

    @Test
    void size_hiddenFails() {
        $virtualList.getComponent().setVisible(false);

        Assertions.assertThrows(IllegalStateException.class,
                () -> $virtualList.size(),
                "Tester should not be accessible for hidden virtual list");
    }

    @Test
    void getItem_existsAndEquals() {
        var firstUser = UserData.first();
        Assertions.assertEquals(firstUser, $virtualList.getItem(0));

        var index = UserData.getAnyValidIndex();
        var anyUser = UserData.get(index);
        Assertions.assertEquals(anyUser, $virtualList.getItem(index));

        var lastUser = UserData.last();
        Assertions.assertEquals(lastUser,
                $virtualList.getItem(UserData.USER_COUNT - 1));
    }

    @Test
    void getItem_outOfBoundsIndexFails() {
        Assertions.assertThrows(IndexOutOfBoundsException.class,
                () -> $virtualList.getItem(-1),
                "VirtualList index out of bounds (low)");

        Assertions.assertThrows(IndexOutOfBoundsException.class,
                () -> $virtualList.getItem(UserData.USER_COUNT),
                "VirtualList index out of bounds (high)");
    }

    @Test
    void getItem_hiddenFails() {
        $virtualList.getComponent().setVisible(false);

        var index = UserData.getAnyValidIndex();
        Assertions.assertThrows(IllegalStateException.class,
                () -> $virtualList.getItem(index),
                "Tester should not be accessible for hidden virtual list");
    }

    @Test
    void getItemText_existsAndEquals() {
        var firstUser = UserData.first();
        Assertions.assertEquals(expectedValueProviderText(firstUser),
                $virtualList.getItemText(0));

        var index = UserData.getAnyValidIndex();
        var anyUser = UserData.get(index);
        Assertions.assertEquals(expectedValueProviderText(anyUser),
                $virtualList.getItemText(index));

        var lastUser = UserData.last();
        Assertions.assertEquals(expectedValueProviderText(lastUser),
                $virtualList.getItemText(UserData.USER_COUNT - 1));
    }

    @Test
    void getItemText_outOfBoundsIndexFails() {
        Assertions.assertThrows(IndexOutOfBoundsException.class,
                () -> $virtualList.getItemText(-1),
                "VirtualList index out of bounds (low)");

        Assertions.assertThrows(IndexOutOfBoundsException.class,
                () -> $virtualList.getItemText(UserData.USER_COUNT),
                "VirtualList index out of bounds (high)");
    }

    @Test
    void getItemText_hiddenFails() {
        $virtualList.getComponent().setVisible(false);

        var index = UserData.getAnyValidIndex();
        Assertions.assertThrows(IllegalStateException.class,
                () -> $virtualList.getItemText(index),
                "Tester should not be accessible for hidden virtual list");
    }

    @Test
    void getItemComponent_verifyNotComponentRenderer() {
        var index = UserData.getAnyValidIndex();
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> $virtualList.getItemComponent(index),
                "valueProviderVirtualList should not have a ComponentRenderer");
    }

    private static String expectedValueProviderText(User user) {
        return String.join(" ", "Name:", user.getFirstName(),
                user.getLastName(), ";", "Active:",
                user.isActive() ? "Yes" : "No");
    }

}
