package com.thoughtworks.selenium.grid.hub;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.ServletHttpContext;

import com.thoughtworks.selenium.grid.configuration.HubConfiguration;
import com.thoughtworks.selenium.grid.hub.management.LifecycleManagerServlet;
import com.thoughtworks.selenium.grid.hub.management.RegistrationConfirmationServlet;
import com.thoughtworks.selenium.grid.hub.management.RegistrationServlet;
import com.thoughtworks.selenium.grid.hub.management.UnregistrationServlet;
import com.thoughtworks.selenium.grid.hub.management.console.ConsoleServlet;

/**
 * Self contained Selenium Grid Hub. Uses Jetty to as a standalone web application.
 */
public class HubServer {

    public static void main(String[] args) throws Exception {
        final HubConfiguration configuration;
        final Server server;
        final ServletHttpContext root;

        configuration = HubRegistry.registry().gridConfiguration().getHub();
        server = new Server();
        server.addListener(":" + configuration.getPort());

        root = (ServletHttpContext) server.getContext("/");
//        root.setResourceBase("./");
//        root.addHandler(new ResourceHandler());
        root
                .addServlet("/selenium-server/driver/*", HubServlet.class
                        .getName());
        root.addServlet("/console", ConsoleServlet.class.getName());
        root.addServlet("/registration-manager/register",
                RegistrationServlet.class.getName());
        root.addServlet("/registration-manager/remotecontrolstatus",
                RegistrationConfirmationServlet.class.getName());
        root.addServlet("/registration-manager/unregister",
                UnregistrationServlet.class.getName());
        root.addServlet("/lifecycle-manager", LifecycleManagerServlet.class
                .getName());

        server.start();
        server.join();
    }

}