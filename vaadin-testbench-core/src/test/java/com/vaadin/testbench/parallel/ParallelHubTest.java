/**
 * Copyright (C) 2012 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 *
 * You should have received a copy of the license along with this program.
 * If not, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.testbench.parallel;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.annotations.RunOnHub;

@RunOnHub("hub-in-annotation")
public class ParallelHubTest extends ParallelTest {

    private static final String HUB_HOSTNAME_PROPERTY = "com.vaadin.testbench.Parameters.hubHostname";
    private static final String SAUCE_USER_PROPERTY = "sauce.user";
    private static final String SAUCE_ACCESS_KEY_PROPERTY = "sauce.sauceAccessKey";

    @Override
    public void setup() throws Exception {
        // Do not actually start a session, just test the class methods
    }

    @Test
    public void hubFromAnnotationOrSystemProperty() {
        String oldProperty = System.getProperty(HUB_HOSTNAME_PROPERTY);
        try {
            System.clearProperty(HUB_HOSTNAME_PROPERTY);

            Assert.assertEquals("hub-in-annotation", getHubHostname());
            System.setProperty(HUB_HOSTNAME_PROPERTY, "hub-system-property");
            Assert.assertEquals("hub-system-property", getHubHostname());
            System.clearProperty(HUB_HOSTNAME_PROPERTY);
            Assert.assertEquals("hub-in-annotation", getHubHostname());
        } finally {
            if (oldProperty != null) {
                System.setProperty(HUB_HOSTNAME_PROPERTY, oldProperty);
            }
        }
    }

    @Test
    public void sauceURLFromSystemProperty() {
        String oldUser = System.getProperty(SAUCE_USER_PROPERTY);
        String oldAccess = System.getProperty(SAUCE_ACCESS_KEY_PROPERTY);
        try {
            System.setProperty(SAUCE_USER_PROPERTY, "user1234");
            System.setProperty(SAUCE_ACCESS_KEY_PROPERTY, "access1234");

            Assert.assertEquals(
                    "http://user1234:access1234@localhost:4445/wd/hub",
                    getHubURL());
        } finally {
            if (oldUser != null) {
                System.setProperty(SAUCE_USER_PROPERTY, oldUser);
            }
            if (oldAccess != null) {
                System.setProperty(SAUCE_ACCESS_KEY_PROPERTY, oldAccess);
            }
        }
    }
}
