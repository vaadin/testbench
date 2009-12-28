package com.thoughtworks.selenium.grid.hub.management;

import com.thoughtworks.selenium.grid.hub.remotecontrol.DynamicRemoteControlPool;
import com.thoughtworks.selenium.grid.hub.remotecontrol.RemoteControlProxy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;

/**
 * Servlet used by Selenium Remote Control to register themselves to the grid.
 *
 * @author Philippe Hanrigou
 */
public class RegistrationConfirmationServlet extends RegistrationManagementServlet {

    private static final Log LOGGER = LogFactory.getLog(RegistrationServlet.class);

    protected void process(HttpServletRequest request, HttpServletResponse response) throws IOException {
        
        final List<RemoteControlProxy> newRemoteControl;
        final DynamicRemoteControlPool pool;

        newRemoteControl = RemoteControlParser.parse(request);
        pool = registry().remoteControlPool();
        pool.status(newRemoteControl);
        writeSuccessfulResponse(response);
    }


}
