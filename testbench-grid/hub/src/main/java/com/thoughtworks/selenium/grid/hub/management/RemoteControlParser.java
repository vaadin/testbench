package com.thoughtworks.selenium.grid.hub.management;

import com.thoughtworks.selenium.grid.HttpClient;
import com.thoughtworks.selenium.grid.hub.remotecontrol.RemoteControlProxy;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.LinkedList;

public class RemoteControlParser {

    public static List<RemoteControlProxy> parse(HttpServletRequest request) {
        final List<RemoteControlProxy> remoteControl = new LinkedList<RemoteControlProxy>();
        final String portParameter;
        final String environment;
        String host;
        final int port;

        if(request.getParameter("host") != null && request.getParameter("host").length() > 0){
            host = request.getParameter("host");
            if (null == host || "".equals(host.trim())) {
                throw new IllegalStateException("You must specify a 'host' parameter");
            }
        }else{
            host = request.getRemoteAddr();
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
            remoteControl.add(new RemoteControlProxy(host, port, env, 1, new HttpClient()));
        }
        
        return remoteControl;
    }

}
