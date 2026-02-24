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
package com.vaadin.flow.component.sidenav;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.testbench.unit.UIUnitTest;
import com.vaadin.testbench.unit.ViewPackages;

@ViewPackages
class SideNavTesterTest extends UIUnitTest {

    SideNavView view;
    SideNavTester<SideNav> sideNav_;

    @BeforeEach
    void init() {
        RouteConfiguration routeConfiguration = RouteConfiguration
                .forApplicationScope();
        routeConfiguration.setAnnotatedRoute(SideNavView.class);
        routeConfiguration.setAnnotatedRoute(TargetView.class);
        navigateToSideNavView();
    }

    @Test
    void sideNav_notVisible_throws() {
        view.sideNav.setVisible(false);
        Assertions.assertThrows(IllegalStateException.class, sideNav_::click);
        Assertions.assertThrows(IllegalStateException.class,
                () -> sideNav_.clickItem("Messages"));
    }

    @Test
    void sideNav_notAttached_throws() {
        view.sideNav.removeFromParent();
        Assertions.assertThrows(IllegalStateException.class, sideNav_::click);
        Assertions.assertThrows(IllegalStateException.class,
                () -> sideNav_.clickItem("Messages"));
    }

    @Test
    void click_collapsible_sideNavExpandsAndCollapse() {
        view.sideNav.setCollapsible(true);
        Assertions.assertTrue(view.sideNav.isExpanded(),
                "Expected SideNav to be expanded by default");
        sideNav_.click();
        Assertions.assertFalse(view.sideNav.isExpanded(),
                "Expected SideNav to be collapsed after click on SideNav top");
        sideNav_.click();
        Assertions.assertTrue(view.sideNav.isExpanded(),
                "Expected SideNav to be expanded again after second click on SideNav top");
    }

    @Test
    void click_notCollapsible_noAction() {
        view.sideNav.setCollapsible(false);
        Assertions.assertTrue(view.sideNav.isExpanded(),
                "Expected SideNav to be expanded by default");
        sideNav_.click();
        Assertions.assertTrue(view.sideNav.isExpanded(),
                "Expected SideNav to be expanded because click should have no effect");
        sideNav_.click();
        Assertions.assertTrue(view.sideNav.isExpanded(),
                "Expected SideNav to be expanded because click should have no effect");
    }

    @Test
    void toggle_collapsible_sideNavExpandsAndCollapse() {
        view.sideNav.setCollapsible(true);
        Assertions.assertTrue(view.sideNav.isExpanded(),
                "Expected SideNav to be expanded by default");
        sideNav_.toggle();
        Assertions.assertFalse(view.sideNav.isExpanded(),
                "Expected SideNav to be collapsed after click on SideNav top");
        sideNav_.toggle();
        Assertions.assertTrue(view.sideNav.isExpanded(),
                "Expected SideNav to be expanded again after second click on SideNav top");
    }

    @Test
    void toggle_notCollapsible_throws() {
        view.sideNav.setCollapsible(false);
        Assertions.assertThrows(IllegalStateException.class, sideNav_::toggle);
    }

    @Test
    void clickItem_byText_navigationHappens() {
        sideNav_.clickItem("Messages");
        assertNavigatedToTargetViewWithParam("N/A");
    }

    @Test
    void clickItem_byText_leafWithoutPath_noNavigation() {
        sideNav_.clickItem("No Link");
        assertNoNavigation();
    }

    @Test
    void clickItem_byText_parentWithoutPath_expandsAndCollapse() {
        Assertions.assertFalse(view.adminSection.isExpanded(),
                "Expected SideNavItem to be collapsed by default");
        sideNav_.clickItem("Admin");
        Assertions.assertTrue(view.adminSection.isExpanded(),
                "Expected SideNavItem to be expanded after click");
        sideNav_.clickItem("Admin");
        Assertions.assertFalse(view.adminSection.isExpanded(),
                "Expected SideNav to be collapsed again after second click");
    }

    @Test
    void clickItem_notExisting_throws() {
        String label = "Not existing item";
        IllegalArgumentException exception = Assertions.assertThrowsExactly(
                IllegalArgumentException.class,
                () -> sideNav_.clickItem(label));
        Assertions.assertTrue(exception.getMessage()
                .contains("Cannot find SideNav item '" + label + "'"));
    }

    @Test
    void clickItem_multipleMatches_throws() {
        IllegalStateException exception = Assertions.assertThrows(
                IllegalStateException.class,
                () -> sideNav_.clickItem("Top Level Duplicated"));
        Assertions.assertTrue(exception.getMessage()
                .contains("Found 2 items with label 'Top Level Duplicated'"));
    }

    @Test
    void clickItem_hidden_throws() {
        view.adminSection.setVisible(false);
        IllegalStateException exception = Assertions.assertThrows(
                IllegalStateException.class, () -> sideNav_.clickItem("Admin"));
        Assertions.assertTrue(exception.getMessage().contains("SideNavItem"));
        Assertions.assertTrue(exception.getMessage().contains("is not usable"));
    }

    @Test
    void clickItem_nested_navigationHappens() {
        view.adminSection.setExpanded(true);
        view.securitySection.setExpanded(true);
        sideNav_.clickItem("Admin", "Security", "Users");
        assertNavigatedToTargetViewWithParam("users");
    }

    @Test
    void clickItem_nestedItemHidden_throws() {
        view.adminSection.setExpanded(true);
        view.securitySection.setVisible(false);
        IllegalStateException exception = Assertions.assertThrowsExactly(
                IllegalStateException.class,
                () -> sideNav_.clickItem("Admin", "Security", "Users"));
        Assertions.assertTrue(exception.getMessage().contains("SideNavItem"));
        Assertions.assertTrue(exception.getMessage().contains("Security"));
        Assertions.assertTrue(exception.getMessage().contains("is not usable"));
    }

    @Test
    void clickItem_nestedItem_parentCollapsed_throws() {
        view.adminSection.setExpanded(false);
        view.securitySection.setExpanded(false);
        IllegalStateException exception = Assertions.assertThrowsExactly(
                IllegalStateException.class,
                () -> sideNav_.clickItem("Admin", "Security", "Users"));
        Assertions.assertTrue(exception.getMessage()
                .contains("Cannot find SideNav item with label 'Security'"));
        Assertions.assertTrue(exception.getMessage()
                .contains("on path 'Admin / Security / Users'"));
        Assertions.assertTrue(exception.getMessage()
                .contains("parent item 'Admin' is collapsed"));
    }

    @Test
    void clickItem_nestedWrongPath_throws() {
        view.adminSection.setExpanded(true);
        IllegalArgumentException exception = Assertions.assertThrowsExactly(
                IllegalArgumentException.class,
                () -> sideNav_.clickItem("Admin", "Configuration", "Users"));
        Assertions.assertTrue(exception.getMessage().contains(
                "Cannot find SideNav item 'Configuration' on path Admin / Configuration / Users"));
    }

    @Test
    void clickItem_multipleNestedMatches_throws() {
        IllegalStateException exception = Assertions.assertThrows(
                IllegalStateException.class,
                () -> sideNav_.clickItem("Nested duplicated", "Duplicated"));
        Assertions.assertTrue(exception.getMessage()
                .contains("Found 2 items with label 'Duplicated'"));
        Assertions.assertTrue(exception.getMessage()
                .contains("on path Nested duplicated / Duplicated"));
    }

    @Test
    void expandAndClickItem_nested_expandCollapsedItems() {
        sideNav_.expandAndClickItem("Admin", "Security", "Users");
        assertNavigatedToTargetViewWithParam("users");

        Assertions.assertTrue(view.adminSection.isExpanded(),
                "Expected 'Admin' item to be expanded");
        Assertions.assertTrue(view.securitySection.isExpanded(),
                "Expected 'Admin / Security' item to be expanded");
    }

    @Test
    void toggleItem_expandsAndCollapse() {
        Assertions.assertFalse(view.adminSection.isExpanded(),
                "Expected Admin SideNavItem to be collapsed by default");
        sideNav_.toggleItem("Admin");
        Assertions.assertTrue(view.adminSection.isExpanded(),
                "Expected Admin SideNavItem to be expanded after click");
        sideNav_.toggleItem("Admin");
        Assertions.assertFalse(view.adminSection.isExpanded(),
                "Expected Admin SideNavItem to be collapsed again after second click");
    }

    @Test
    void toggleItem_nestedItem_expandsAndCollapse() {
        view.adminSection.setExpanded(true);
        Assertions.assertFalse(view.securitySection.isExpanded(),
                "Expected Security SideNavItem to be collapsed by default");
        sideNav_.toggleItem("Admin", "Security");
        Assertions.assertTrue(view.securitySection.isExpanded(),
                "Expected Security SideNavItem to be expanded after click");
        sideNav_.toggleItem("Admin", "Security");
        Assertions.assertFalse(view.securitySection.isExpanded(),
                "Expected Security SideNavItem to be collapsed again after second click");
    }

    @Test
    void toggleItem_nestedItemHidden_throws() {
        view.adminSection.setExpanded(true);
        view.securitySection.setVisible(false);
        IllegalStateException exception = Assertions.assertThrowsExactly(
                IllegalStateException.class,
                () -> sideNav_.toggleItem("Admin", "Security"));
        Assertions.assertTrue(exception.getMessage().contains("SideNavItem"));
        Assertions.assertTrue(exception.getMessage().contains("Security"));
        Assertions.assertTrue(exception.getMessage().contains("is not usable"));
    }

    @Test
    void toggleItem_nestedItem_parentCollapsed_throws() {
        view.adminSection.setExpanded(false);
        view.securitySection.setExpanded(false);
        IllegalStateException exception = Assertions.assertThrowsExactly(
                IllegalStateException.class,
                () -> sideNav_.toggleItem("Admin", "Security"));
        Assertions.assertTrue(exception.getMessage()
                .contains("Cannot find SideNav item with label 'Security'"));
        Assertions.assertTrue(
                exception.getMessage().contains("on path 'Admin / Security"));
        Assertions.assertTrue(exception.getMessage()
                .contains("parent item 'Admin' is collapsed"));
    }

    @Test
    void toggleItem_nestedWrongPath_throws() {
        view.adminSection.setExpanded(true);
        IllegalArgumentException exception = Assertions.assertThrowsExactly(
                IllegalArgumentException.class,
                () -> sideNav_.toggleItem("Admin", "Configuration", "Users"));
        Assertions.assertTrue(exception.getMessage().contains(
                "Cannot find SideNav item 'Configuration' on path Admin / Configuration / Users"));
    }

    @Test
    void toggleItem_leafItem_throws() {
        IllegalStateException exception = Assertions.assertThrows(
                IllegalStateException.class,
                () -> sideNav_.toggleItem("No Link"));
        Assertions.assertTrue(exception.getMessage()
                .contains("Toggle button cannot be clicked"));

        view.adminSection.setExpanded(true);
        view.securitySection.setExpanded(true);
        exception = Assertions.assertThrows(IllegalStateException.class,
                () -> sideNav_.toggleItem("Admin", "Security", "Users"));
        Assertions.assertTrue(exception.getMessage()
                .contains("Toggle button cannot be clicked"));
    }

    @Test
    void toggleItem_multipleMatches_throws() {
        IllegalStateException exception = Assertions.assertThrows(
                IllegalStateException.class,
                () -> sideNav_.toggleItem("Nested duplicated", "Duplicated"));
        Assertions.assertTrue(exception.getMessage().contains(
                "Found 2 items with label 'Duplicated' on path Nested duplicated / Duplicated"));
    }

    private void assertNavigatedToTargetViewWithParam(
            String expectedParamValue) {
        HasElement currentView = getCurrentView();
        Assertions.assertInstanceOf(TargetView.class, currentView,
                "Expecting click on SideNav item to navigate to TargetView, but current view is "
                        + currentView.getClass());
        Assertions.assertEquals(((TargetView) currentView).parameter,
                expectedParamValue);
    }

    private void assertNoNavigation() {
        HasElement currentView = getCurrentView();
        Assertions.assertSame(view, currentView,
                "Expecting click on SideNav item without path to stay on current view");
    }

    private void navigateToSideNavView() {
        view = navigate(SideNavView.class);
        sideNav_ = test(view.sideNav);
    }
}
