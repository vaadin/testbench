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
package com.vaadin.testbench;

import org.junit.Assert;
import org.junit.Test;

public class IPAddressTest {

    @Test
    public void siteLocalAddress() {
        // Can't know if the machine running the test actually has a site local
        // address...
        try {
            String address = IPAddress.findSiteLocalAddress();
            Assert.assertTrue(
                    address.startsWith("10.") || address.startsWith("172.16.")
                            || address.startsWith("192.168."));
        } catch (RuntimeException e) {
            Assert.assertEquals(
                    "No compatible (10.0.0.0/8, 172.16.0.0/12, 192.168.0.0/16) IP address found.",
                    e.getMessage());
        }
    }

}
