package com.itmill.testingtools.runner;

import com.thoughtworks.selenium.SeleneseTestCase;

public abstract class ITMillToolkitTestCase extends SeleneseTestCase {

    public enum Browser {
        CHROME("*chrome");

        private String browserId;

        public String getBrowserId() {
            return browserId;
        }

        private Browser(String browserId) {
            this.browserId = browserId;
        }
    }

    protected TestBase testBase = new TestBase();

    public void waitForITMillToolkit() {
        testBase.waitForITMillToolkit();
    }

    @Override
    public void setUp(String url, String browserString) throws Exception {
        testBase.setUp(url, browserString);
        selenium = testBase.getSelenium();
    }

    @Override
    public void setUp(String url) throws Exception {
        testBase.setUp(url);
        selenium = testBase.getSelenium();
    }

    @Override
    public void setUp() throws Exception {
        testBase.setUp();
        selenium = testBase.getSelenium();
    }

    protected void setUp(String url, Browser browser) throws Exception {
        testBase.setUp(url, browser.getBrowserId());
        selenium = testBase.getSelenium();
    }

    protected void setTestHosts(String... testHosts) {
        testBase.setTestHosts(testHosts);
    }

}
