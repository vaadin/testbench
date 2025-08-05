/**
 * Copyright (C) 2000-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.confirmdialog;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.testbench.unit.UIUnitTest;
import com.vaadin.testbench.unit.ViewPackages;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ViewPackages
class ConfirmDialogTesterTest extends UIUnitTest {

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
