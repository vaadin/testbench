package com.thoughtworks.selenium.grid.hub.remotecontrol.commands;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.thoughtworks.selenium.grid.HttpParameters;
import com.thoughtworks.selenium.grid.Response;
import com.thoughtworks.selenium.grid.hub.CouldNotGetSessionException;
import com.thoughtworks.selenium.grid.hub.Environment;
import com.thoughtworks.selenium.grid.hub.remotecontrol.GlobalRemoteControlPool;
import com.thoughtworks.selenium.grid.hub.remotecontrol.RemoteControlPool;
import com.thoughtworks.selenium.grid.hub.remotecontrol.RemoteControlProxy;

/**
 * Selenese command requesting a new session for a specific browser/environment.
 * Marks the start of a new Selenese session.
 */
public class NewBrowserSessionCommand extends SeleneseCommand {

    private static final Pattern SESSION_ID_PATTERN = Pattern
            .compile("OK,([^,]*)");
    private static final Log logger = LogFactory
            .getLog(NewBrowserSessionCommand.class);
    private final Environment environment;

    public NewBrowserSessionCommand(Environment environment,
            HttpParameters parameters) {
        super(null, parameters);
        this.environment = environment;
    }

    @Override
    public Response execute(RemoteControlPool pool) throws IOException {
        RemoteControlProxy remoteControl;
        final String sessionId;
        final Response response;

        remoteControl = pool.reserve(environment);
        logger.debug("got rc " + remoteControl.toString());
        while (remoteControl != null && remoteControl.unreliable() != 200) {
            logger.warn("RC " + remoteControl.toString()
                    + " seems unresponsive.");
            pool.release(remoteControl);
            ((GlobalRemoteControlPool) pool)
                    .unregisterAllUnresponsiveRemoteControls();
            remoteControl = pool.reserve(environment);
        }
        if (null == remoteControl) {
            final String message = "No available remote control for environment '"
                    + environment.name() + "'";
            logger.warn(message);
            return new Response(message);
        }
        try {
            response = remoteControl.forward(parameters());
            sessionId = parseSessionId(response.body());
            if (null == sessionId) {
                throw new CouldNotGetSessionException(
                        "Could not retrieve a new session from remote control running on "
                                + remoteControl.hostName() + ": "
                                + response.body());
            }
            pool.associateWithSession(remoteControl, sessionId);
            pool.updateSessionLastActiveAt(sessionId);

            return response;
        } catch (CouldNotGetSessionException e) {
            // rethrow exception to caller for the retry mechanism
            logger.error("Problem while requesting new browser session", e);
            pool.release(remoteControl);
            throw e;
        } catch (Exception e) {
            logger.error("Problem while requesting new browser session", e);
            pool.release(remoteControl);
            return new Response(e.getMessage());
        }
    }

    protected String parseSessionId(String responseBody) {
        final Matcher matcher = SESSION_ID_PATTERN.matcher(responseBody);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        return null;
    }

    public Environment environment() {
        return environment;
    }

}