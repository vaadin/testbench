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
 * Servlet used by Selenium Remote Controls to unregister themselves to the Hub.
 */
public class UnregistrationServlet extends RegistrationManagementServlet {

    private static final Log logger = LogFactory.getLog(UnregistrationServlet.class);

    @Override
    protected void process(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final List<RemoteControlProxy> newRemoteControl;
        final DynamicRemoteControlPool pool;

        logger.info("Unregistering remote control...");
        newRemoteControl = RemoteControlParser.parse(request);
        pool = registry().remoteControlPool();
        for (RemoteControlProxy rcp : newRemoteControl) {
            pool.unregister(rcp);
        }
        logger.info("Unregistered " + newRemoteControl);
        writeSuccessfulResponse(response);
    }

}
