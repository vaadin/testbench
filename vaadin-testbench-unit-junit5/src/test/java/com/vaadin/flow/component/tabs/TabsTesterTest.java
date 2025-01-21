/**
 * Copyright (C) 2000-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.tabs;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.testbench.unit.UIUnitTest;
import com.vaadin.testbench.unit.ViewPackages;

@ViewPackages
class TabsTesterTest extends UIUnitTest {

    TabsView view;
    TabsTester<Tabs> tabs_;

    @BeforeEach
    void init() {
        RouteConfiguration.forApplicationScope()
                .setAnnotatedRoute(TabsView.class);
        view = navigate(TabsView.class);
        tabs_ = test(view.tabs);
    }

    @Test
    void select_notUsable_throws() {
        view.tabs.setEnabled(false);
        Assertions.assertThrows(IllegalStateException.class,
                () -> tabs_.select("Details"));
        Assertions.assertThrows(IllegalStateException.class,
                () -> tabs_.select(0));
    }

    @Test
    void select_disableTabs_throws() {
        view.payment.setEnabled(false);
        Assertions.assertThrows(IllegalStateException.class,
                () -> tabs_.select("Payment"));
        Assertions.assertThrows(IllegalStateException.class,
                () -> tabs_.select(1));
    }

    @Test
    void select_notExistingTab_throws() {
        view.payment.setEnabled(false);
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> tabs_.select("Summary"));

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> tabs_.select(4));

    }

    @Test
    void select_byLabel_selectsTab() {
        AtomicReference<Tab> selectedTab = new AtomicReference<>();
        view.tabs.addSelectedChangeListener(
                ev -> selectedTab.set(ev.getSelectedTab()));

        tabs_.select("Payment");
        Assertions.assertEquals(view.payment, selectedTab.get());

        tabs_.select("Details");
        Assertions.assertEquals(view.details, selectedTab.get());

        tabs_.select("Shipping");
        Assertions.assertEquals(view.shipping, selectedTab.get());
    }

    @Test
    void select_byIndex_selectsTab() {
        AtomicReference<Tab> selectedTab = new AtomicReference<>();
        view.tabs.addSelectedChangeListener(
                ev -> selectedTab.set(ev.getSelectedTab()));

        tabs_.select(1);
        Assertions.assertEquals(view.payment, selectedTab.get());

        tabs_.select(0);
        Assertions.assertEquals(view.details, selectedTab.get());

        tabs_.select(2);
        Assertions.assertEquals(view.shipping, selectedTab.get());

        tabs_.select(-1);
        Assertions.assertNull(selectedTab.get());

    }

    @Test
    void select_byIndex_indexRefersToVisibleTabs() {
        view.details.setVisible(false);
        AtomicReference<Tab> selectedTab = new AtomicReference<>();
        view.tabs.addSelectedChangeListener(
                ev -> selectedTab.set(ev.getSelectedTab()));

        // Payment now is the first visible tab
        tabs_.select(0);
        Assertions.assertEquals(view.payment, selectedTab.get());

        view.payment.setVisible(false);
        // Shipping is now is the only visible tab
        tabs_.select(0);
        Assertions.assertEquals(view.shipping, selectedTab.get());
    }

    @Test
    void getTab_getsCorrectTab() {
        Assertions.assertSame(view.details, tabs_.getTab("Details"));
        Assertions.assertSame(view.payment, tabs_.getTab("Payment"));
        Assertions.assertSame(view.shipping, tabs_.getTab("Shipping"));

        Assertions.assertSame(view.details, tabs_.getTab(0));
        Assertions.assertSame(view.payment, tabs_.getTab(1));
        Assertions.assertSame(view.shipping, tabs_.getTab(2));

        view.details.setVisible(false);
        // Payment now is the first visible tab
        Assertions.assertSame(view.payment, tabs_.getTab(0));

        view.payment.setVisible(false);
        // Shipping is now is the only visible tab
        Assertions.assertSame(view.shipping, tabs_.getTab(0));

        // Now Details and Shipping
        view.details.setVisible(true);
        Assertions.assertSame(view.details, tabs_.getTab(0));
        Assertions.assertSame(view.shipping, tabs_.getTab(1));

    }

    @Test
    void getTab_notExistingTab_throws() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> tabs_.getTab("Summary"));

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> tabs_.getTab(4));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> tabs_.getTab(-1));
    }

    @Test
    void getTab_hidden_throws() {
        view.payment.setVisible(false);
        Assertions.assertThrows(IllegalStateException.class,
                () -> tabs_.getTab("Payment"));
    }

    @Test
    void isSelected_getsTabState() {
        view.tabs.setSelectedIndex(1);

        Assertions.assertFalse(tabs_.isSelected("Details"),
                "Details tab is not selected, but got true");
        Assertions.assertTrue(tabs_.isSelected("Payment"),
                "Payment tab is selected, but got false");
        Assertions.assertFalse(tabs_.isSelected("Shipping"),
                "Shipping tab is not selected, but got true");

        Assertions.assertFalse(tabs_.isSelected(0),
                "Details tab at index 0 is not selected, but got true");
        Assertions.assertTrue(tabs_.isSelected(1),
                "Payment tab at index 1 is selected, but got false");
        Assertions.assertFalse(tabs_.isSelected(2),
                "Shipping tab at index 2 is not selected, but got true");
    }

    @Test
    void isSelected_byIndex_invisibleTabsIgnored() {
        view.payment.setVisible(false);
        view.tabs.setSelectedTab(view.shipping);

        Assertions.assertFalse(tabs_.isSelected(0),
                "Details tab at index 0 should be not selected, but got true");
        Assertions.assertTrue(tabs_.isSelected(1),
                "Shipping tab at index 1 (payment hidden) should be selected, but got false");

        view.payment.setVisible(true);
        view.details.setVisible(false);
        view.tabs.setSelectedTab(view.payment);
        Assertions.assertTrue(tabs_.isSelected(0),
                "Payment tab at index 0 (details hidden) should be selected, but got false");
        Assertions.assertFalse(tabs_.isSelected(1),
                "Shipping tab at index 1 (details hidden) should be not selected, but got true");
    }

    @Test
    void isSelected_notExistingTab_throws() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> tabs_.isSelected("Summary"));

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> tabs_.isSelected(4));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> tabs_.isSelected(-1));
    }

}
