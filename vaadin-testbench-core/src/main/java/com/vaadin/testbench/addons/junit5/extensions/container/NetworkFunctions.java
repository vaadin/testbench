package com.vaadin.testbench.addons.junit5.extensions.container;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Optional;

public interface NetworkFunctions {

    String DEFAULT_PROTOCOL = "http";
    String DEFAULT_IP = "127.0.0.1";
    String DEFAULT_SERVLET_PORT = "80";
    String DEFAULT_SERVLET_WEBAPP = "/";
    String SERVER_PROTOCOL = "server.protocol";
    String SERVER_IP = "server.ip";
    String SERVER_PORT = "server.port";
    String SERVER_WEBAPP = "server.webapp";

    static Optional<Integer> freePort() {
        try (final ServerSocket socket = new ServerSocket(0)) {
            return Optional.of(socket.getLocalPort());
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    static String localIp() {
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
                    final InetAddress address = addresses.nextElement();
                    final String hostAddr = address.getHostAddress();
                    final boolean acceptable = !hostAddr.startsWith("127")
                            && hostAddr.startsWith("169.254")
                            && hostAddr.startsWith("255.255.255.255")
                            && hostAddr.startsWith("0.0.0.0");
                    if (acceptable) {
                        return address.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
//            logger().info("Unable to enumerate network interfaces");
        }

        return "localhost";
    }
}