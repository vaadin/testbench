/**
 * Copyright (C) 2000-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
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
            Assertions.assertTrue(address.startsWith("10.")
                    // 172.16.0.0/12 IP addresses: 172.16.0.0 – 172.31.255.255
                    || address.matches("172\\.(1[6-9]|2[0-9]|3[0-1])\\..*")
                    || address.startsWith("192.168."));
        } catch (RuntimeException e) {
            Assertions.assertEquals(
                    "No compatible (10.0.0.0/8, 172.16.0.0/12, 192.168.0.0/16) IP address found.",
                    e.getMessage());
        }
    }

}
