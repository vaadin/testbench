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
package com.vaadin.flow.component.confirmdialog;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vaadin.browserless.BrowserlessTest;
import com.vaadin.browserless.ViewPackages;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.router.RouteConfiguration;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ViewPackages
class ConfirmDialogTesterTest extends BrowserlessTest {

    ConfirmDialogView view;
    ConfirmDialogTester wrap;

    @BeforeEach
    void init() {
        RouteConfiguration.forApplicationScope()
                .setAnnotatedRoute(ConfirmDialogView.class);
        view = navigate(ConfirmDialogView.class);
        wrap = test(view.dialog);
    }

    @Test
    void cancelNotAvailable_throwsException() {
        wrap.open();
        assertThrows(IllegalStateException.class, wrap::cancel);
    }

    @Test
    void rejectNotAvailable_throwsException() {
        wrap.open();
        assertThrows(IllegalStateException.class, wrap::reject);
    }

    @Test
    void cancelAvailable_cancelEventClosesDialog() {
        AtomicInteger close = new AtomicInteger(0);
        view.dialog.setCancelButton("cancel", event -> close.incrementAndGet());
        wrap.open();

        wrap.cancel();

        Assertions.assertEquals(1, close.get());
        Assertions.assertFalse(view.dialog.isOpened());
        Assertions.assertFalse(view.dialog.isAttached(),
                "Confirm dialog should be detached");
    }

    @Test
    void rejectAvailable_cancelEventClosesDialog() {
        AtomicInteger rejects = new AtomicInteger(0);
        view.dialog.setRejectButton("reject",
                event -> rejects.incrementAndGet());
        wrap.open();

        wrap.reject();

        Assertions.assertEquals(1, rejects.get());
        Assertions.assertFalse(view.dialog.isOpened());
        Assertions.assertFalse(view.dialog.isAttached(),
                "Confirm dialog should be detached");
    }

    @Test
    void confirmButton_ClosesDialog() {
        AtomicInteger confirm = new AtomicInteger(0);
        view.dialog.addConfirmListener(event -> confirm.incrementAndGet());
        wrap.open();

        wrap.confirm();

        Assertions.assertEquals(1, confirm.get());
        Assertions.assertFalse(view.dialog.isOpened());
        Assertions.assertFalse(view.dialog.isAttached(),
                "Confirm dialog should be detached");
    }

    @Test
    void programmaticallyClose_dialogIsDetached() {
        wrap.open();

        view.dialog.close();

        Assertions.assertFalse(view.dialog.isAttached(),
                "Confirm dialog should be detached");
    }

    @Test
    void headerText_getHeaderReturnsCorrect() {
        String header = "Test String";
        view.dialog.setHeader(header);

        wrap.open();

        Assertions.assertEquals(header, wrap.getHeader());
        Assertions.assertThrows(IllegalStateException.class,
                wrap::getHeaderElement);
    }

    @Test
    void headerElement_correctElementIsReturned() {
        Head header = new Head();
        view.dialog.setHeader(header);
        wrap.open();
        Assertions.assertEquals(header.getElement(), wrap.getHeaderElement());
        Assertions.assertNull(wrap.getHeader(),
                "No string header should be available");
    }

    @Tag("div")
    private static class Head extends Component {

    }
}
