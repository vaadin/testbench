package com.thoughtworks.selenium.grid.hub.management;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.thoughtworks.selenium.grid.hub.remotecontrol.DynamicRemoteControlPool;
import com.thoughtworks.selenium.grid.hub.remotecontrol.RemoteControlProxy;

/**
 * Servlet used by Selenium Remote Control to register themselves to the grid.
 *
 * @author Philippe Hanrigou
 */
public class RegistrationServlet extends RegistrationManagementServlet {

    private static final Log LOGGER = LogFactory.getLog(RegistrationServlet.class);

    @Override
    protected void process(HttpServletRequest request, HttpServletResponse response) throws IOException {

        final List<RemoteControlProxy> newRemoteControl;
        final DynamicRemoteControlPool pool;

        LOGGER.info("Registering new remote control...");
        newRemoteControl = RemoteControlParser.parse(request);
        pool = registry().remoteControlPool();
        for (RemoteControlProxy rcProxy : newRemoteControl) {
            pool.register(rcProxy);
        }
        LOGGER.info("Registered " + newRemoteControl.get(0));
        writeSuccessfulResponse(response);
    }


}
