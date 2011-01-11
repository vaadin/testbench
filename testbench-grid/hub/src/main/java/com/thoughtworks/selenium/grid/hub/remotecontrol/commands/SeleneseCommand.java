package com.thoughtworks.selenium.grid.hub.remotecontrol.commands;

import java.io.IOException;

import com.thoughtworks.selenium.grid.HttpParameters;
import com.thoughtworks.selenium.grid.Response;
import com.thoughtworks.selenium.grid.hub.remotecontrol.RemoteControlPool;
import com.thoughtworks.selenium.grid.hub.remotecontrol.RemoteControlProxy;

/**
 * Generic Selenese command
 */
public class SeleneseCommand {

    private final String sessionId;
    private final HttpParameters parameters;

    public SeleneseCommand(String sessionId, HttpParameters parameters) {
        this.sessionId = sessionId;
        this.parameters = parameters;
    }

    public String sessionId() {
        return sessionId;
    }

    public HttpParameters parameters() {
        return parameters;
    }

    public Response execute(RemoteControlPool pool) throws IOException {
        final RemoteControlProxy remoteControl;
        final Response response;

        if (null == sessionId) {
            return new Response(
                    "Selenium Driver error: No sessionId provided for command '"
                            + parameters.toString() + "'");
        }
        remoteControl = pool.retrieve(sessionId());
        pool.updateSessionLastActiveAt(sessionId);

        // Set the test name if this is a setTestName command
        if (getTestName() != null) {
            remoteControl.setCurrentTestName(getTestName());
        }
        response = remoteControl.forward(parameters());
        pool.updateSessionLastActiveAt(sessionId);

        return response;
    }

    private String getTestName() {
        String cmd = parameters().get("cmd");
        if ("setTestName".equals(cmd)) {
            return parameters().get("1");
        }

        return null;
    }

}
