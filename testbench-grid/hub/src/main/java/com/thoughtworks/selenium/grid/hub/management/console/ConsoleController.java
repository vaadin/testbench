package com.thoughtworks.selenium.grid.hub.management.console;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.thoughtworks.selenium.grid.hub.Environment;
import com.thoughtworks.selenium.grid.hub.HubRegistry;
import com.thoughtworks.selenium.grid.hub.management.console.mvc.Controller;
import com.thoughtworks.selenium.grid.hub.management.console.mvc.Page;
import com.thoughtworks.selenium.grid.hub.remotecontrol.RemoteControlProxy;

public class ConsoleController extends Controller {

    public ConsoleController(HubRegistry registry) {
        super(registry);
    }

    public void process(HttpServletResponse response) throws IOException {
        final Page page = list();
        render(page, response);
    }

    public Page list() {
        final Page page;

        List<Environment> environments = registry().environmentManager()
                .environments();
        Collections.sort(environments, new Comparator<Environment>() {

            public int compare(Environment arg0, Environment arg1) {
                return arg0.name().compareTo(arg1.name());
            }
        });

        List<RemoteControlProxy> allRemoteControls = registry()
                .remoteControlPool().availableRemoteControls();
        List<RemoteControlProxy> remoteControls = new ArrayList<RemoteControlProxy>();

        RemoteControlProxy rc = null;// String host, int port, String
        // environment,HttpClient httpClient
        StringBuilder environment = new StringBuilder();
        for (RemoteControlProxy rcp : allRemoteControls) {
            if (rc == null) {
                rc = rcp;
                environment.append(rcp.environment());
            } else if (rc.host().equals(rcp.host())) {
                environment.append("</br>" + rcp.environment());
            } else {
                remoteControls.add(new RemoteControlProxy(rc.host(), rc.port(),
                        environment.toString(), null));
                rc = rcp;
                environment = new StringBuilder();
                environment.append(rcp.environment());
            }
        }
        if (rc != null) {
            remoteControls.add(new RemoteControlProxy(rc.host(), rc.port(),
                    environment.toString(), null));
        }

        page = new Page("index.html");
        page.set("environments", environments);
        page.set("availableRemoteControls", remoteControls);
        page.set("reservedRemoteControls", registry().remoteControlPool()
                .reservedRemoteControls());

        return page;
    }
}