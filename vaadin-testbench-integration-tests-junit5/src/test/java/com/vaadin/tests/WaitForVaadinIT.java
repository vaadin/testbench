/**
 * Copyright (C) 2000-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.tests;

import java.lang.reflect.Field;

import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.JavascriptExecutor;

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
        assertDevServerIsNotLoaded();
        getCommandExecutor().executeScript(
                "window.Vaadin.Flow.clients[\"blocker\"] = {isActive: () => true};");
        setWaitForVaadinLoopHook(500,
                "window.Vaadin.Flow.clients[\"blocker\"] = {isActive: () => false};");
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
    public void waitForVaadin_noFlow_returnsImmediately() {
        openTestURL();

        getCommandExecutor().executeScript("window.Vaadin.Flow = undefined;");
        assertExecutionNoLonger(() -> getCommandExecutor().waitForVaadin());
    }

    @BrowserTest
    public void waitForVaadin_devModeNotReady_waits() {
        openTestURL();
        getCommandExecutor().executeScript(
                "window.Vaadin = {Flow: {devServerIsNotLoaded: true}};");
        assertExecutionBlocked(() -> getCommandExecutor().waitForVaadin());
    }

    @BrowserTest
    public void waitForVaadin_devModeNotReady_waitsUntilReady() {
        openTestURL();
        assertDevServerIsNotLoaded();
        getCommandExecutor().executeScript(
                "window.Vaadin = {Flow: {devServerIsNotLoaded: true}};");
        setWaitForVaadinLoopHook(500,
                "window.Vaadin.Flow.devServerIsNotLoaded = false;");
        getCommandExecutor().waitForVaadin();
        assertDevServerIsNotLoaded();
    }

    private void assertDevServerIsNotLoaded() {
        Object devServerIsNotLoaded = executeScript(
                "return window.Vaadin.Flow.devServerIsNotLoaded;");
        Assertions.assertTrue(
                devServerIsNotLoaded == null
                        || devServerIsNotLoaded == Boolean.FALSE,
                "devServerIsNotLoaded should be null or false");
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

    private void setWaitForVaadinLoopHook(long timeout,
            String scriptToRunAfterTimeout) {
        long systemCurrentTimeMillis = System.currentTimeMillis();
        setWaitForVaadinLoopHook(() -> {
            if (System.currentTimeMillis()
                    - systemCurrentTimeMillis > timeout) {
                ((JavascriptExecutor) getCommandExecutor().getDriver()
                        .getWrappedDriver())
                                .executeScript(scriptToRunAfterTimeout);
                setWaitForVaadinLoopHook(null);
            }
        });
    }

    private void setWaitForVaadinLoopHook(Runnable action) {
        try {
            Field field = getCommandExecutor().getClass()
                    .getDeclaredField("waitForVaadinLoopHook");
            field.setAccessible(true);
            field.set(getCommandExecutor(), action);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
