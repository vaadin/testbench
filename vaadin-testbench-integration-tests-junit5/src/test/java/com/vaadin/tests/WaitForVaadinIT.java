/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.tests;

import org.junit.jupiter.api.Assertions;

import com.vaadin.flow.component.Component;
import com.vaadin.testUI.PageObjectView;
import com.vaadin.testbench.BrowserTest;

public class WaitForVaadinIT extends AbstractBrowserTB9Test {

    private static final long NON_BLOCKING_EXECUTION_TIMEOUT = 10000;
    private static final long BLOCKING_EXECUTION_TIMEOUT = 40000;

    @Override
    protected Class<? extends Component> getTestView() {
        return PageObjectView.class;
    }

    @BrowserTest
    public void waitForVaadin_connectorsInitialised_returnsImmediately() {
        openTestURL();
        assertExecutionNoLonger(() -> getCommandExecutor().waitForVaadin());
    }

    @BrowserTest
    public void waitForVaadin_activeConnector_waits() {
        openTestURL();
        getCommandExecutor().executeScript(
                "window.Vaadin.Flow.clients[\"blocker\"] = {isActive: () => true};");
        assertExecutionBlocked(() -> getCommandExecutor().waitForVaadin());
    }

    @BrowserTest
    public void waitForVaadin_activeConnector_waitsUtilReady() {
        openTestURL();
        getCommandExecutor().executeScript(
                "window.Vaadin.Flow.clients[\"blocker\"] = {isActive: () => true};"
                        + "setTimeout(function() {"
                        + "  window.Vaadin.Flow.clients[\"blocker\"] = {isActive: () => false};"
                        + "}, 500);");
        getCommandExecutor().waitForVaadin();
        assertClientIsActive();
    }

    @BrowserTest
    public void waitForVaadin_noConnectors_returnsImmediately() {
        openTestURL();
        getCommandExecutor()
                .executeScript("window.Vaadin.Flow.clients = undefined;");
        assertExecutionNoLonger(() -> getCommandExecutor().waitForVaadin());
    }

    @BrowserTest
    public void waitForVaadin_devModeNotReady_waits() {
        openTestURL();
        getCommandExecutor().executeScript("window.Vaadin.Flow.ready = false;");
        assertExecutionBlocked(() -> getCommandExecutor().waitForVaadin());
    }

    @BrowserTest
    public void waitForVaadin_devModeNotReady_waitsUntilReady() {
        openTestURL();
        getCommandExecutor()
                .executeScript("window._savedReady = window.Vaadin.Flow.ready;"
                        + "window.Vaadin.Flow.ready = false;"
                        + "setTimeout(function() {"
                        + "  window.Vaadin.Flow.ready = window._savedReady;"
                        + "}, 500);");
        getCommandExecutor().waitForVaadin();
    }

    private void assertClientIsActive() {
        Object active = executeScript(
                "return window.Vaadin.Flow.clients[\"blocker\"].isActive();");
        Assertions.assertSame(active, Boolean.FALSE,
                "clients[\"blocker\"].isActive() should return false");
    }

    private void assertExecutionNoLonger(Runnable command) {
        long before = System.currentTimeMillis();
        command.run();
        long after = System.currentTimeMillis();
        long timeout = after - before;
        Assertions.assertTrue(timeout < NON_BLOCKING_EXECUTION_TIMEOUT,
                "Unexpected execution time, waiting time = " + timeout);
    }

    private void assertExecutionBlocked(Runnable command) {
        long before = System.currentTimeMillis();
        command.run();
        long after = System.currentTimeMillis();
        long timeout = after - before;
        Assertions.assertTrue(timeout >= BLOCKING_EXECUTION_TIMEOUT,
                "Unexpected execution time, waiting time = " + timeout);
    }

}
