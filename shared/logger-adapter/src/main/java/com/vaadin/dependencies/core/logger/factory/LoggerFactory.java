package com.vaadin.dependencies.core.logger.factory;

import com.vaadin.dependencies.core.logger.LoggingService;

/**
 * <p>LoggerFactory interface.</p>
 *
 * @author svenruppert
 * @version $Id: $Id
 */
public interface LoggerFactory {

    /**
     * <p>getLogger.</p>
     *
     * @param name a {@link String} object.
     * @return a {@link LoggingService} object.
     */
    LoggingService getLogger(String name);
}
