package com.thoughtworks.selenium.grid.webserver;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.ServletHttpContext;

/**
 * Self contained Selenium Grid Hub. Uses Jetty to as a standalone web application.
 */
public class WebServer {

    private final int port;
    private final Class routeResolverClass;
    private Server httpServer;


    public WebServer(int port, Class routeResolverClass) {
        this.port = port;
        this.routeResolverClass = routeResolverClass;
    }

    public int port() {
        return port;
    }

    public void launch() throws Exception {
        createHttpServer();
        startListeningForIncomingRequests();
        waitForShutdown();
    }

    protected void startListeningForIncomingRequests() throws Exception {
        httpServer().start();
    }

    protected void waitForShutdown() throws InterruptedException {
        httpServer().join();
    }

    protected void createHttpServer() throws Exception {
        final ServletHttpContext root;

        httpServer = new Server();
        httpServer.addListener(":" + port);

        root = (ServletHttpContext) httpServer.getContext("/");
        root.setInitParameter("route_resolver", routeResolverClass().getName());
        root.addServlet("/*", MainServlet.class.getName());
    }


    protected Class routeResolverClass() {
        return routeResolverClass;
    }

    protected Server httpServer() {
        return httpServer;
    }

}