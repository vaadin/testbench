/**
 * Copyright (C) 2020 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.testbench;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class IPAddressTest {

    @Test
    public void siteLocalAddress() {
        // Can't know if the machine running the test actually has a site local
        // address...
        try {
            String address = IPAddress.findSiteLocalAddress();
            Assertions.assertTrue(
                    address.startsWith("10.") || address.startsWith("172.16.")
                            || address.startsWith("192.168."));
        } catch (RuntimeException e) {
            Assertions.assertEquals(
                    "No compatible (10.0.0.0/8, 172.16.0.0/12, 192.168.0.0/16) IP address found.",
                    e.getMessage());
        }
    }

}
