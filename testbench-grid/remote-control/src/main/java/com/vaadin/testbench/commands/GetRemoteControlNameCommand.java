package com.vaadin.testbench.commands;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.commons.logging.Log;
import org.mortbay.log.LogFactory;
import org.openqa.selenium.server.commands.Command;

/**
 * Fetches the host name of the machine running the remote control. Falls back
 * to using IP if the name cannot be determined.
 * 
 * Returns: OK,host name or ip
 * 
 * @author Vaadin Ltd
 */
public class GetRemoteControlNameCommand extends Command {
    private static final Log LOGGER = LogFactory
            .getLog(GetRemoteControlNameCommand.class);

    private static String hostName = null;

    public GetRemoteControlNameCommand() {
    }

    @Override
    public String execute() {
        if (hostName == null) {

            try {
                InetAddress localHost = InetAddress.getLocalHost();
                hostName = localHost.getHostName();
            } catch (UnknownHostException e) {
                hostName = "???";
                LOGGER.error("Could not determine host name or IP address", e);
            }
        }

        return "OK," + hostName;
    }
}
