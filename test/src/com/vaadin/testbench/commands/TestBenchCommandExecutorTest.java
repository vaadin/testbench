package com.vaadin.testbench.commands;

import static org.easymock.EasyMock.createMockBuilder;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.google.common.collect.ImmutableMap;

/**
 * @author jonatan
 * 
 */
public class TestBenchCommandExecutorTest {

    @Test
    public void testTestBenchCommandExecutorIsATestBenchCommands() {
        TestBenchCommandExecutor tbce = new TestBenchCommandExecutor(
                createNiceMock(WebDriver.class));
        assertTrue(tbce instanceof TestBenchCommands);
    }

    @Test
    public void setTestName_executesOnRemote() {
        TestBenchCommandExecutor tbce = createMockBuilder(
                TestBenchCommandExecutor.class).addMockedMethod("execute")
                .withConstructor(createNiceMock(RemoteWebDriver.class))
                .createMock();
        expect(
                tbce.execute(TestBenchCommands.SET_TEST_NAME,
                        ImmutableMap.of("name", "foo"))).andReturn(null).once();
        replay(tbce);

        tbce.setTestName("foo");

        verify(tbce);
    }
}
