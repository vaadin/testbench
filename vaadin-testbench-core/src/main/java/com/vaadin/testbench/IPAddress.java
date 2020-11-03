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

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Optional;
import java.util.function.Function;

public class IPAddress {
    /**
     * Tries to determine a site local (10.0.0.0, 172.16.0.0 or 192.168.0.0) IP
     * address of the machine the test is running on.
     *
     * @return An IP address of one of the network interfaces in the machine or
     *         an empty optional
     * @throws RuntimeException
     *             if no IP was found
     */
    public static String findSiteLocalAddress() {
        return findIPAddress(address -> {
            if (address.isLoopbackAddress()) {
                return false;
            }
            if (address.isSiteLocalAddress()) {
                return true;
            }
            return false;
        }).orElseThrow(() -> new RuntimeException(
                "No compatible (10.0.0.0/8, 172.16.0.0/12, 192.168.0.0/16) IP address found."));
    }

    /**
     * Enumerates the available IP addresses until one is found which is
     * accepted by the given function.
     *
     * @param acceptor
     *            the function which determines if an IP address should be
     *            returned or not
     *
     * @return An IP address of one of the network interfaces in the machine or
     *         an empty optional.
     */
    public static Optional<String> findIPAddress(
            Function<InetAddress, Boolean> acceptor) {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface nwInterface = interfaces.nextElement();
                if (!nwInterface.isUp() || nwInterface.isLoopback()
                        || nwInterface.isVirtual()) {
                    continue;
                }
                Enumeration<InetAddress> addresses = nwInterface
                        .getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if (acceptor.apply(address)) {
                        return Optional.of(address.getHostAddress());
                    }
                }
            }
        } catch (SocketException e) {
            throw new RuntimeException("Could not enumerate ");
        }

        return Optional.empty();
    }

}
