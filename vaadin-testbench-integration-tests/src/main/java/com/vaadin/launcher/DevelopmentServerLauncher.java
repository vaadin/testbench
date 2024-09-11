/*
 * Vaadin TestBench Addon
 *
 * Copyright (C) 2012-2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.launcher;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.log.JavaUtilLog;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * Class for running Jetty servlet container within Eclipse project.
 */
public class DevelopmentServerLauncher {

    private static final String KEYSTORE = "src/main/resources/com/vaadin/launcher/keystore";
    private static final int serverPort = 8080;

    /**
     * Main function for running Jetty.
     *
     * Command line Arguments are passed through to Jetty, see runServer method
     * for options.
     *
     * @param args
     *            command line arguments
     */
    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "true");
        System.setProperty("org.eclipse.jetty.util.log.class",
                JavaUtilLog.class.getName());

        assertAssertionsEnabled();

        //
        // Pass-through of arguments for Jetty
        final Map<String, String> serverArgs = parseArguments(args);
        if (!serverArgs.containsKey("shutdownPort")) {
            serverArgs.put("shutdownPort", "8081");
        }

        int port = Integer.parseInt(serverArgs.get("shutdownPort"));
        if (port > 0) {
            try {
                // Try to notify another instance that it's time to close
                Socket socket = new Socket((String) null, port);
                // Wait until the other instance says it has closed
                socket.getInputStream().read();
                // Then tidy up
                socket.close();
            } catch (IOException e) {
                // Ignore if port is not open
            }
        }

        // Start Jetty
        System.out.println("Starting Jetty servlet container.");
        try {
            runServer(serverArgs, "Development Server Mode");
        } catch (Exception e) {
            // NOP exception already on console by jetty
        }
    }

    private static void assertAssertionsEnabled() {
        try {
            assert false;

            System.err.println("You should run "
                    + DevelopmentServerLauncher.class.getSimpleName()
                    + " with assertions enabled. Add -ea as a VM argument.");
        } catch (AssertionError e) {
            // All is fine
        }
    }

    /**
     * Run the server with specified arguments.
     *
     * @param serverArgs
     * @return
     * @throws Exception
     * @throws Exception
     */
    protected static String runServer(Map<String, String> serverArgs,
            String mode) throws Exception {

        // Assign default values for some arguments
        assignDefault(serverArgs, "webroot", "src/main/webapp");
        assignDefault(serverArgs, "httpPort", "" + serverPort);
        assignDefault(serverArgs, "context", "");

        int port = serverPort;
        try {
            port = Integer.parseInt(serverArgs.get("httpPort"));
        } catch (NumberFormatException e) {
            // keep default value for port
        }

        // Add help for System.out
        System.out.println("-------------------------------------------------\n"
                + "Starting Vaadin in " + mode + ".\n"
                + "Running in http://localhost:" + port
                + "\n-------------------------------------------------\n");

        final Server server = new Server();

        // Enable annotation scanning for webapps
        Configuration.ClassList classlist = Configuration.ClassList
                .setServerDefault(server);
        classlist.addBefore("org.eclipse.jetty.webapp.JettyWebXmlConfiguration",
                "org.eclipse.jetty.annotations.AnnotationConfiguration");

        final ServerConnector connector = new ServerConnector(server);
        connector.setPort(port);
        if (serverArgs.containsKey("withssl")) {
            @SuppressWarnings("deprecation")
            SslContextFactory sslFact = new SslContextFactory();
            sslFact.setTrustStorePath(KEYSTORE);
            sslFact.setTrustStorePassword("password");
            sslFact.setKeyStorePath(KEYSTORE);
            sslFact.setKeyManagerPassword("password");
            sslFact.setKeyStorePassword("password");

            ServerConnector sslConnector = new ServerConnector(server, sslFact);
            sslConnector.setPort(8444);

            server.setConnectors(new Connector[] { connector, sslConnector });
        } else {
            server.setConnectors(new Connector[] { connector });
        }

        final WebAppContext webappcontext = new WebAppContext();
        webappcontext.setContextPath(serverArgs.get("context"));
        webappcontext.setWar(serverArgs.get("webroot"));

        // Make webapp use embedded classloader
        webappcontext.setParentLoaderPriority(true);
        webappcontext.setAttribute(
                "org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern",
                ".*/classes/.*");

        server.setHandler(webappcontext);

        try {
            server.start();

            if (serverArgs.containsKey("shutdownPort")) {
                int shutdownPort = Integer
                        .parseInt(serverArgs.get("shutdownPort"));
                final ServerSocket serverSocket = new ServerSocket(shutdownPort,
                        1, InetAddress.getByName("127.0.0.1"));
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            System.out.println(
                                    "Waiting for shutdown signal on port "
                                            + serverSocket.getLocalPort());
                            // Start waiting for a close signal
                            Socket accept = serverSocket.accept();
                            // First stop listening to the port
                            serverSocket.close();

                            // Start a thread that kills the JVM if
                            // server.stop() doesn't have any effect
                            Thread interruptThread = new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(5000);
                                        if (!server.isStopped()) {
                                            System.out.println(
                                                    "Jetty still running. Closing JVM.");
                                            dumpThreadStacks();
                                            System.exit(-1);
                                        }
                                    } catch (InterruptedException e) {
                                        // Interrupted if server.stop() was
                                        // successful
                                    }
                                }
                            };
                            interruptThread.setDaemon(true);
                            interruptThread.start();

                            // Then stop the jetty server
                            server.stop();

                            interruptThread.interrupt();

                            // Send a byte to tell the other process that it can
                            // start jetty
                            OutputStream outputStream = accept
                                    .getOutputStream();
                            outputStream.write(0);
                            outputStream.flush();
                            // Finally close the socket
                            accept.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }.start();

            }
            server.join();
        } catch (Exception e) {
            server.stop();
            throw e;
        }

        return "http://localhost:" + port + serverArgs.get("context");
    }

    /**
     * Assign default value for given key.
     *
     * @param map
     * @param key
     * @param value
     */
    private static void assignDefault(Map<String, String> map, String key,
            String value) {
        if (!map.containsKey(key)) {
            map.put(key, value);
        }
    }

    /**
     * Parse all command line arguments into a map.
     *
     * Arguments format "key=value" are put into map.
     *
     * @param args
     *            command line arguments
     * @return map of arguments in key value pairs.
     */
    protected static Map<String, String> parseArguments(String[] args) {
        final Map<String, String> map = new HashMap<>();
        for (String arg : args) {
            final int d = arg.indexOf("=");
            if (d > 0 && d < arg.length() && arg.startsWith("--")) {
                final String name = arg.substring(2, d);
                final String value = arg.substring(d + 1);
                map.put(name, value);
            }
        }
        return map;
    }

    private static void dumpThreadStacks() {
        for (Entry<Thread, StackTraceElement[]> entry : Thread
                .getAllStackTraces().entrySet()) {
            Thread thread = entry.getKey();
            StackTraceElement[] stackTraceElements = entry.getValue();

            System.out.println(thread.getName() + " - " + thread.getState());
            for (StackTraceElement stackTraceElement : stackTraceElements) {
                System.out.println("    at " + stackTraceElement);
            }
            System.out.println();
        }
    }
}
