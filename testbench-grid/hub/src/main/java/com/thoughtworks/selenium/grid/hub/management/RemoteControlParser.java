package com.thoughtworks.selenium.grid.hub.management;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.thoughtworks.selenium.grid.HttpClient;
import com.thoughtworks.selenium.grid.hub.remotecontrol.RemoteControlProxy;

public class RemoteControlParser {

    public static List<RemoteControlProxy> parse(HttpServletRequest request) {
        final List<RemoteControlProxy> remoteControl = new LinkedList<RemoteControlProxy>();
        final String portParameter;
        final String environment;
        String host;
        final int port;

        host = request.getParameter("host");
        if (null == host || "".equals(host.trim())) {
            host = request.getRemoteAddr();
        }
        if (null == host || "".equals(host.trim())) {
            throw new IllegalStateException("You must specify a 'host' parameter");
        }
        portParameter = request.getParameter("port");
        if (null == portParameter || "".equals(portParameter.trim())) {
            throw new IllegalStateException("You must specify a 'port' parameter");
        }
        port = Integer.parseInt(portParameter);
        environment = request.getParameter("environment");
        if (null == environment || "".equals(environment.trim())) {
            throw new IllegalStateException("You must specify an 'environment' parameter");
        }
        String[] environments = environment.split(",");
        for(String env: environments){
            remoteControl.add(new RemoteControlProxy(host, port, env,
                    new HttpClient()));
        }
        
        return remoteControl;
    }

}
