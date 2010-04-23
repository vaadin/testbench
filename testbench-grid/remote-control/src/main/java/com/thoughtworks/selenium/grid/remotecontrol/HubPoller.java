package com.thoughtworks.selenium.grid.remotecontrol;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.server.SeleniumServer;

import com.thoughtworks.selenium.grid.remotecontrol.HeartbeatRequest.Status;

/**
 * Periodicaly Poll Hub to check it is still up and re-register automatically
 * when the Hub disappears and come back up.
 */
public class HubPoller implements Runnable {
    private static final Log LOGGER = LogFactory.getLog(HubPoller.class);
    private final SelfRegisteringRemoteControl rc;
    private final int pollingIntervalInSeconds;
    private boolean active;
    private SeleniumServer seleniumServer;

    protected HubPoller(SelfRegisteringRemoteControl rc, int pollingIntervalInSeconds) {
        this.rc = rc;
        this.pollingIntervalInSeconds = pollingIntervalInSeconds;
        active = true;
    }

    public SelfRegisteringRemoteControl remoteControl() {
        return rc;
    }

    public long pollingIntervalInMilliseconds() {
        return pollingIntervalInSeconds * 1000;
    }
    
    public void checkConnectionToHub() {
        final Status status;

        LOGGER.debug("Checking connection to hub...");
        status = rc.canReachHub();
        if (status.equals(Status.UNREGISTERED)) {
            if (seleniumServer != null) {
                seleniumServer.stopAllBrowsers();
            }
            try {
                rc.register();
            } catch (IOException e) {
                LOGGER.error("Internal error while checking hub connection", e);
            }
        } else if (status.equals(Status.DOWN)) {
            if (seleniumServer != null) {
                seleniumServer.stopAllBrowsers();
            }
            LOGGER.warn("Lost connection to hub!");
        }
    }

    public boolean active() {
        return active;
    }

    public void stop() {
        active = false;
    }

    public void run() {
        while (active) {
            pollHub();
        }
    }

    public void pollHub() {
        sleepForALittleWhile();
        checkConnectionToHub();
    }

    protected void sleepForALittleWhile() {
        try {
            Thread.sleep(pollingIntervalInMilliseconds());
        } catch (InterruptedException e) {
            LOGGER.warn("Interrupted!");
        }
    }

    public void setSeleniumServer(SeleniumServer seleniumServer) {
        this.seleniumServer = seleniumServer;
    }

}
