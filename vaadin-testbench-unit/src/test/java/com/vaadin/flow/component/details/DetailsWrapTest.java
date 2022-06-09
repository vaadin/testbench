/*
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */

package com.vaadin.flow.component.details;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.testbench.unit.ComponentWrap;
import com.vaadin.testbench.unit.UIUnitTest;
import com.vaadin.testbench.unit.ViewPackages;

@ViewPackages
class DetailsWrapTest extends UIUnitTest {

    DetailsView view;
    DetailsWrap<Details> details_;
    ComponentWrap<?> contents_;

    @BeforeEach
    public void registerView() {
        RouteConfiguration.forApplicationScope()
                .setAnnotatedRoute(DetailsView.class);
        view = navigate(DetailsView.class);
        details_ = wrap(view.details);
        contents_ = wrap(view.contents);
    }

    @Test
    void openDetails_contentVisible() {
        AtomicBoolean listenerInvoked = new AtomicBoolean();
        view.details.addOpenedChangeListener(ev -> listenerInvoked.set(true));

        details_.openDetails();
        Assertions.assertTrue(listenerInvoked.get());
        Assertions.assertTrue(view.details.isOpened(),
                "Contents should be visible after opening details");
    }

    @Test
    void openDetails_notUsable_throws() {
        view.details.setEnabled(false);
        Assertions.assertThrows(IllegalStateException.class,
                details_::openDetails);
    }

    @Test
    void openDetails_alreadyOpen_throws() {
        view.details.setOpened(true);
        AtomicBoolean listenerInvoked = new AtomicBoolean();
        view.details.addOpenedChangeListener(ev -> listenerInvoked.set(true));

        Assertions.assertThrows(IllegalStateException.class,
                details_::openDetails);
        Assertions.assertFalse(listenerInvoked.get());
    }

    @Test
    void closeDetails_contentHidden() {
        view.details.setOpened(true);

        AtomicBoolean listenerInvoked = new AtomicBoolean();
        view.details.addOpenedChangeListener(ev -> listenerInvoked.set(true));

        details_.closeDetails();
        Assertions.assertTrue(listenerInvoked.get());
        Assertions.assertFalse(view.details.isOpened(),
                "Contents should not be visible after closing details");
    }

    @Test
    void closeDetails_notUsable_throws() {
        view.details.setEnabled(false);
        Assertions.assertThrows(IllegalStateException.class,
                details_::closeDetails);
    }

    @Test
    void closeDetails_alreadyClosed_throws() {
        AtomicBoolean listenerInvoked = new AtomicBoolean();
        view.details.addOpenedChangeListener(ev -> listenerInvoked.set(true));

        Assertions.assertThrows(IllegalStateException.class,
                details_::closeDetails);
        Assertions.assertFalse(listenerInvoked.get());
    }

    @Test
    void toggleDetails_notUsable_throws() {
        view.details.setEnabled(false);
        Assertions.assertThrows(IllegalStateException.class,
                details_::toggleDetails);
    }

    @Test
    void toggleDetails_detailsVisibilityChanges() {
        List<Boolean> stateChanges = new ArrayList<>();
        view.details
                .addOpenedChangeListener(ev -> stateChanges.add(ev.isOpened()));

        details_.toggleDetails();
        Assertions.assertIterableEquals(List.of(true), stateChanges);

        details_.toggleDetails();
        Assertions.assertIterableEquals(List.of(true, false), stateChanges);

        details_.toggleDetails();
        Assertions.assertIterableEquals(List.of(true, false, true),
                stateChanges);
    }

    @Test
    void isOpen_getsDetailsOpenInfo() {
        Assertions.assertFalse(details_.isOpen());

        view.details.setOpened(true);
        Assertions.assertTrue(details_.isOpen());

        view.details.setOpened(false);
        Assertions.assertFalse(details_.isOpen());
    }

    @Test
    void isOpen_notUsable_throws() {
        view.details.setEnabled(false);
        Assertions.assertThrows(IllegalStateException.class, details_::isOpen);
    }

}
