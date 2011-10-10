/* 
@ITMillApache2LicenseForJavaFiles@
 */
package com.vaadin.testbench.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.UnknownHostException;

import org.easymock.EasyMock;
import org.junit.Test;

import com.vaadin.testbench.commands.GetRemoteControlNameCommand.HostResolver;

public class GetRemoteControlNameCommandTest {

    private HostResolver mockResolver() throws UnknownHostException {
        HostResolver resolver = EasyMock.createMock(HostResolver.class);
        resolver.getHostName();
        EasyMock.expectLastCall().andReturn("localhost");
        resolver.getHostIP();
        EasyMock.expectLastCall().andReturn("127.0.0.1");
        EasyMock.replay(resolver);
        return resolver;
    }

    @Test
    public void testResultContainsHostname() throws UnknownHostException {
        HostResolver resolver = mockResolver();

        GetRemoteControlNameCommand cmd = new GetRemoteControlNameCommand();
        cmd.setHostResolver(resolver);
        String result = cmd.execute();
        assertTrue(result.startsWith("OK,localhost"));

        EasyMock.verify(resolver);
    }

    @Test
    public void testResultContainsIPAddress() throws UnknownHostException {
        GetRemoteControlNameCommand cmd = new GetRemoteControlNameCommand();
        // The result is cached.
        String result = cmd.execute();
        assertTrue(result.endsWith("(127.0.0.1)"));
    }

    @Test
    public void testResultIsCorrectlyFormatted() throws UnknownHostException {
        GetRemoteControlNameCommand cmd = new GetRemoteControlNameCommand();
        // The result is cached.
        String result = cmd.execute();
        assertEquals("OK,localhost (127.0.0.1)", result);
    }
}
