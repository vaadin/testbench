/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.tests;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.flow.component.Component;
import com.vaadin.testUI.PageObjectView;

public class WaitForVaadinIT extends AbstractTB6Test {

    private static final long NON_BLOCKING_EXECUTION_TIMEOUT = 5000;
    private static final long BLOCKING_EXECUTION_TIMEOUT = 20000;

    @Override
    protected Class<? extends Component> getTestView() {
        return PageObjectView.class;
    }

    @Test
    public void waitForVaadin_connectorsInitialised_returnsImmediately() {
        openTestURL();
        assertExecutionNoLonger(() -> getCommandExecutor().waitForVaadin());
    }

    @Test
    public void waitForVaadin_activeConnector_waits() {
        openTestURL();
        getCommandExecutor().executeScript(
                "window.Vaadin.Flow.clients[\"blocker\"] = {isActive: () => true};");
        assertExecutionBlocked(() -> getCommandExecutor().waitForVaadin());
    }

    @Test
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

    @Test
    public void waitForVaadin_noConnectors_returnsImmediately() {
        openTestURL();
        getCommandExecutor()
                .executeScript("window.Vaadin.Flow.clients = undefined;");
        assertExecutionNoLonger(() -> getCommandExecutor().waitForVaadin());
    }

    @Test
    public void waitForVaadin_devModeNotReady_waits() {
        openTestURL();
        getCommandExecutor().executeScript(
                "window.Vaadin.Flow.whenReady = false;");
        assertExecutionBlocked(() -> getCommandExecutor().waitForVaadin());
    }

    @Test
    public void waitForVaadin_devModeNotReady_waitsUntilReady() {
        openTestURL();
        getCommandExecutor().executeScript(
                "window._savedWhenReady = window.Vaadin.Flow.whenReady;"
                        + "window.Vaadin.Flow.whenReady = false;"
                        + "setTimeout(function() {"
                        + "  window.Vaadin.Flow.whenReady = window._savedWhenReady;"
                        + "}, 500);");
        getCommandExecutor().waitForVaadin();
    }

    private void assertClientIsActive() {
        Object active = executeScript(
                "return window.Vaadin.Flow.clients[\"blocker\"].isActive();");
        Assert.assertSame("clients[\"blocker\"].isActive() should return false",
                Boolean.FALSE, active);
    }

    private void assertExecutionNoLonger(Runnable command) {
        long before = System.currentTimeMillis();
        command.run();
        long after = System.currentTimeMillis();
        long timeout = after - before;
        Assert.assertTrue(
                "Unexpected execution time, waiting time = " + timeout,
                timeout < NON_BLOCKING_EXECUTION_TIMEOUT);
    }

    private void assertExecutionBlocked(Runnable command) {
        long before = System.currentTimeMillis();
        command.run();
        long after = System.currentTimeMillis();
        long timeout = after - before;
        Assert.assertTrue(
                "Unexpected blocked execution time, waiting time = " + timeout,
                timeout >= BLOCKING_EXECUTION_TIMEOUT);
    }

}
