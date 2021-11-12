/**
 * Copyright (C) 2021 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
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
    public void waitForVaadin_noConnectors_returnsImmediately() {
        openTestURL();
        getCommandExecutor().executeScript(
                "window.Vaadin.Flow.clients = undefined;");
        assertExecutionNoLonger(() -> getCommandExecutor().waitForVaadin());
    }

    @Test
    public void waitForVaadin_noFlow_returnsImmediately() {
        openTestURL();

        getCommandExecutor().executeScript(
                "window.Vaadin.Flow = undefined;");
        assertExecutionNoLonger(() -> getCommandExecutor().waitForVaadin());
    }

    @Test
    public void waitForVaadin_devModeNotReady_waits() {
        openTestURL();

        getCommandExecutor().executeScript(
                "window.Vaadin = {Flow: {devServerIsNotLoaded: true}};");
        assertExecutionBlocked(() -> getCommandExecutor().waitForVaadin());
    }

    private void assertExecutionNoLonger(Runnable command) {
        long before = System.currentTimeMillis();
        command.run();
        long after = System.currentTimeMillis();
        long timeout = after - before;
        Assert.assertTrue(
                "Unexpected execution time, waiting time = " +
                timeout, timeout < NON_BLOCKING_EXECUTION_TIMEOUT);
    }

    private void assertExecutionBlocked(Runnable command) {
        long before = System.currentTimeMillis();
        command.run();
        long after = System.currentTimeMillis();
        long timeout = after - before;
        Assert.assertTrue(
                "Unexpected blocked execution time, waiting time = " +
                timeout, timeout >= BLOCKING_EXECUTION_TIMEOUT);
    }
}
