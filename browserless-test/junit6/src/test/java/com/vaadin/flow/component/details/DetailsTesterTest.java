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
package com.vaadin.flow.component.details;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vaadin.browserless.BrowserlessTest;
import com.vaadin.browserless.ViewPackages;
import com.vaadin.flow.router.RouteConfiguration;

@ViewPackages
class DetailsTesterTest extends BrowserlessTest {

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
