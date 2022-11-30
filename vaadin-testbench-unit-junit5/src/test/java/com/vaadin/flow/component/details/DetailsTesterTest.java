/**
 * Copyright (C) 2000-2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.details;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.testbench.unit.UIUnitTest;
import com.vaadin.testbench.unit.ViewPackages;

@ViewPackages
class DetailsWrapTest extends UIUnitTest {

    DetailsView view;

    @BeforeEach
    public void registerView() {
        RouteConfiguration.forApplicationScope()
                .setAnnotatedRoute(DetailsView.class);
        view = navigate(DetailsView.class);
    }

    @Test
    void openDetails_contentVisible() {
        AtomicBoolean listenerInvoked = new AtomicBoolean();
        view.details.addOpenedChangeListener(ev -> listenerInvoked.set(true));

        test(view.details).openDetails();
        Assertions.assertTrue(listenerInvoked.get());
        Assertions.assertTrue(view.details.isOpened(),
                "Contents should be visible after opening details");
    }

    @Test
    void openDetails_notUsable_throws() {
        view.details.setEnabled(false);
        Assertions.assertThrows(IllegalStateException.class,
                test(view.details)::openDetails);
    }

    @Test
    void openDetails_alreadyOpen_throws() {
        view.details.setOpened(true);
        AtomicBoolean listenerInvoked = new AtomicBoolean();
        view.details.addOpenedChangeListener(ev -> listenerInvoked.set(true));

        Assertions.assertThrows(IllegalStateException.class,
                test(view.details)::openDetails);
        Assertions.assertFalse(listenerInvoked.get());
    }

    @Test
    void closeDetails_contentHidden() {
        view.details.setOpened(true);

        AtomicBoolean listenerInvoked = new AtomicBoolean();
        view.details.addOpenedChangeListener(ev -> listenerInvoked.set(true));

        test(view.details).closeDetails();
        Assertions.assertTrue(listenerInvoked.get());
        Assertions.assertFalse(view.details.isOpened(),
                "Contents should not be visible after closing details");
    }

    @Test
    void closeDetails_notUsable_throws() {
        view.details.setEnabled(false);
        Assertions.assertThrows(IllegalStateException.class,
                test(view.details)::closeDetails);
    }

    @Test
    void closeDetails_alreadyClosed_throws() {
        AtomicBoolean listenerInvoked = new AtomicBoolean();
        view.details.addOpenedChangeListener(ev -> listenerInvoked.set(true));

        Assertions.assertThrows(IllegalStateException.class,
                test(view.details)::closeDetails);
        Assertions.assertFalse(listenerInvoked.get());
    }

    @Test
    void toggleDetails_notUsable_throws() {
        view.details.setEnabled(false);
        Assertions.assertThrows(IllegalStateException.class,
                test(view.details)::toggleDetails);
    }

    @Test
    void toggleDetails_detailsVisibilityChanges() {
        List<Boolean> stateChanges = new ArrayList<>();
        view.details
                .addOpenedChangeListener(ev -> stateChanges.add(ev.isOpened()));

        test(view.details).toggleDetails();
        Assertions.assertIterableEquals(List.of(true), stateChanges);

        test(view.details).toggleDetails();
        Assertions.assertIterableEquals(List.of(true, false), stateChanges);

        test(view.details).toggleDetails();
        Assertions.assertIterableEquals(List.of(true, false, true),
                stateChanges);
    }

    @Test
    void isOpen_getsDetailsOpenInfo() {
        Assertions.assertFalse(test(view.details).isOpen());

        view.details.setOpened(true);
        Assertions.assertTrue(test(view.details).isOpen());

        view.details.setOpened(false);
        Assertions.assertFalse(test(view.details).isOpen());
    }

    @Test
    void isOpen_notUsable_throws() {
        view.details.setEnabled(false);
        Assertions.assertThrows(IllegalStateException.class,
                test(view.details)::isOpen);
    }

}
