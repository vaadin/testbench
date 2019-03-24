package com.vaadin.dependencies.core.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Random;

/**
 * <p>PortUtils class.</p>
 *
 * @author svenruppert
 * @version $Id: $Id
 */
public class PortUtils {

  /**
   * <p>defaultRestPort.</p>
   *
   * @return a int.
   */
  public int defaultRestPort() {
    return 7081;
  }

  /**
   * <p>defaultServletPort.</p>
   *
   * @return a int.
   */
  public int defaultServletPort() {
    return 7080;
  }

  //TODO static ??
  /**
   * <p>nextFreePortForTest.</p>
   *
   * @return a int.
   */
  public int nextFreePortForTest() {
    int counter = 0;
    final Random random = new Random();
    while (counter < 1_00) {
      try {
        final int port = 1024 + (random.nextInt(65535 - 2048));
        new ServerSocket(port).close();
        return port;
      } catch (IOException ex) {
        counter = counter + 1;
      }
    }
    // if the program gets here, no port in the range was found
    throw new RuntimeException("no free port found");
  }

  /**
   * <p>isPortAvailable.</p>
   *
   * @param port a int.
   * @return a boolean.
   */
  public boolean isPortAvailable(int port) {
    try {
      new ServerSocket(port).close();
    } catch (IOException e) {
      // port is used
      return false;
    }
    // port is unused
    return true;
  }

}
