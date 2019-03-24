package com.vaadin.dependencies.core.logger;

/**
 * <p>HasLogger interface.</p>
 *
 * @author svenruppert
 * @version $Id: $Id
 */
public interface HasLogger {
  /**
   * <p>logger.</p>
   *
   * @return a {@link LoggingService} object.
   */
  default LoggingService logger() {
    return Logger.getLogger(getClass());
  }
}
