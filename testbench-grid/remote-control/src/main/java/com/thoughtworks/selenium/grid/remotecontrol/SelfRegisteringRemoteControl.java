package com.thoughtworks.selenium.grid.remotecontrol;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.server.RemoteControlConfiguration;
import org.openqa.selenium.server.SeleniumServer;
import org.openqa.selenium.server.cli.RemoteControlLauncher;

/*
 * Selenium Remote Control that registers/unregisters itself to a central Hub when it starts/stops.
 */
public class SelfRegisteringRemoteControl {


    private static final Log logger = LogFactory.getLog(SelfRegisteringRemoteControlLauncher.class);
    private final RegistrationInfo registrationInfo;
    private final HubPoller hubPoller;

    public SelfRegisteringRemoteControl(RegistrationInfo registrationInfo, int hubPollerIntervalInSeconds) {
        this.registrationInfo = registrationInfo;
        hubPoller = new HubPoller(this, hubPollerIntervalInSeconds);
    }

    public RegistrationInfo registrationInfo() {
        return registrationInfo;
    }

    public void register() throws IOException {
        new RegistrationRequest(registrationInfo).execute();
    }

    public void unregister() throws IOException {
        new UnregistrationRequest(registrationInfo).execute();
    }

    public HeartbeatRequest.Status canReachHub() {
        return new HeartbeatRequest(registrationInfo).execute();
    }

    public void launch(String[] args) throws Exception {
        logStartingMessages(args);

        // extracted from SeleniumServer.main(args) to access SeleniumServer
        // instance
        final RemoteControlConfiguration configuration;
        final SeleniumServer seleniumProxy;

        configuration = RemoteControlLauncher.parseLauncherOptions(args);

        System.setProperty("org.mortbay.http.HttpRequest.maxFormContentSize",
                "0"); // default max is 200k; zero is infinite
        seleniumProxy = new SeleniumServer("true".equals(System
                .getProperty("slowResources")), configuration);
        seleniumProxy.boot();

        hubPoller.setSeleniumServer(seleniumProxy);
        startHubPoller();
    }

    protected HubPoller hubPoller() {
        return hubPoller;
    }

    protected void startHubPoller() {
        new Thread(hubPoller).start();
    }


    protected void logStartingMessages(String[] args) {
        logger.info("Starting selenium server with options:" + registrationInfo);
        logger.info("hubPollerInterval: " + hubPoller.pollingIntervalInMilliseconds() + " ms");
        for (String arg : args) {
            logger.info(arg);
        }
    }

    protected void ensureUnregisterOnShutdown() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    hubPoller.stop();
                    unregister();
                } catch (IOException e) {
                    logger.error("Could not unregister " + this, e);
                }
            }
        });
    }
}
