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

import org.junit.jupiter.api.Assertions;

import com.vaadin.flow.component.Component;
import com.vaadin.testUI.PageObjectView;
import com.vaadin.testbench.TestBenchTest;

public class WaitForVaadinIT extends AbstractJUnit5TB6Test {

    private static final long NON_BLOCKING_EXECUTION_TIMEOUT = 10000;
    private static final long BLOCKING_EXECUTION_TIMEOUT = 40000;

    @Override
    protected Class<? extends Component> getTestView() {
        return PageObjectView.class;
    }

    @TestBenchTest
    public void waitForVaadin_connectorsInitialised_returnsImmediately() {
        openTestURL();
        assertExecutionNoLonger(
                () -> testBenchUtil.getCommandExecutor().waitForVaadin());
    }

    @TestBenchTest
    public void waitForVaadin_activeConnector_waits() {
        openTestURL();
        testBenchUtil.getCommandExecutor().executeScript(
                "window.Vaadin.Flow.clients[\"blocker\"] = {isActive: () => true};");
        assertExecutionBlocked(
                () -> testBenchUtil.getCommandExecutor().waitForVaadin());
    }

    @TestBenchTest
    public void waitForVaadin_noConnectors_returnsImmediately() {
        openTestURL();
        testBenchUtil.getCommandExecutor()
                .executeScript("window.Vaadin.Flow.clients = undefined;");
        assertExecutionNoLonger(
                () -> testBenchUtil.getCommandExecutor().waitForVaadin());
    }

    @TestBenchTest
    public void waitForVaadin_noFlow_returnsImmediately() {
        openTestURL();

        testBenchUtil.getCommandExecutor()
                .executeScript("window.Vaadin.Flow = undefined;");
        assertExecutionNoLonger(
                () -> testBenchUtil.getCommandExecutor().waitForVaadin());
    }

    @TestBenchTest
    public void waitForVaadin_devModeNotReady_waits() {
        openTestURL();

        testBenchUtil.getCommandExecutor().executeScript(
                "window.Vaadin = {Flow: {devServerIsNotLoaded: true}};");
        assertExecutionBlocked(
                () -> testBenchUtil.getCommandExecutor().waitForVaadin());
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
