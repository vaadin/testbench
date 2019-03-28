package com.vaadin.testbench.addons.junit5.extensions.container;

import static com.vaadin.frp.StringFunctions.notEmpty;
import static com.vaadin.frp.StringFunctions.notStartsWith;
import static com.vaadin.frp.Transformations.not;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.util.Collections;
import java.util.Enumeration;
import java.util.function.Supplier;

import com.vaadin.frp.Transformations;
import com.vaadin.frp.functions.CheckedPredicate;
import com.vaadin.frp.functions.CheckedSupplier;

public interface NetworkFunctions {

  String DEFAULT_PROTOCOL       = "http";
  String DEFAULT_IP             = "127.0.0.1";
  String DEFAULT_SERVLET_PORT   = "80";
  String DEFAULT_SERVLET_WEBAPP = "/";
  String SERVER_PROTOCOL        = "server.protocol";
  String SERVER_IP              = "server.ip";
  String SERVER_PORT            = "server.port";
  String SERVER_WEBAPP          = "server.webapp";

  static CheckedSupplier<Integer> freePort() {
    return () -> {
      try (final ServerSocket socket = new ServerSocket(0)) {
        return socket.getLocalPort();
      }
    };
  }

  static Supplier<String> localeIP() {
    return () -> {
      final CheckedSupplier<Enumeration<NetworkInterface>> checkedSupplier =
          NetworkInterface::getNetworkInterfaces;

      return Transformations.<NetworkInterface>enumToStream()
          .apply(checkedSupplier.getOrElse(Collections::emptyEnumeration))
          .filter((CheckedPredicate<NetworkInterface>) NetworkInterface::isUp)
          .map(NetworkInterface::getInetAddresses)
          .flatMap(iaEnum -> Transformations.<InetAddress>enumToStream().apply(iaEnum))
          .filter(inetAddress -> inetAddress instanceof Inet4Address)
          .filter(not(InetAddress::isMulticastAddress)).filter(not(InetAddress::isLoopbackAddress))
          .map(InetAddress::getHostAddress).filter(notEmpty())
          .filter(adr -> notStartsWith().apply(adr, "127"))
          .filter(adr -> notStartsWith().apply(adr, "169.254"))
          .filter(adr -> notStartsWith().apply(adr, "255.255.255.255"))
          .filter(adr -> notStartsWith().apply(adr, "255.255.255.255"))
          .filter(adr -> notStartsWith().apply(adr, "0.0.0.0"))
          // .filter(adr -> range(224, 240).noneMatch(nr -> adr.startsWith(valueOf(nr))))
          .findFirst().orElse("localhost");
    };
  }

}
