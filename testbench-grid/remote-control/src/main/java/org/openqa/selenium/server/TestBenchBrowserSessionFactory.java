package org.openqa.selenium.server;

import org.openqa.selenium.server.browserlaunchers.BrowserLauncherFactory;

/**
 * Additions for the BrowserSessionFactory class.
 * 
 * @author Vaadin Ltd
 */
public class TestBenchBrowserSessionFactory extends BrowserSessionFactory {

    public TestBenchBrowserSessionFactory(BrowserLauncherFactory blf) {
        super(blf);
    }

    public boolean hasActiveSessions() {
        return !activeSessions.isEmpty();
    }
}
