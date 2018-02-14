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
            if (address.startsWith("10.") || address.startsWith("172.16.")
                    || address.startsWith("192.168.")) {
                // This is ok
            } else {
                throw new RuntimeException(
                        "Unexpected IP address returned: " + address);
            }
        } catch (RuntimeException e) {
            Assert.assertEquals(
                    "No compatible (10.0.0.0/8, 172.16.0.0/12, 192.168.0.0/16) IP address found.",
                    e.getMessage());
        }
    }

}
