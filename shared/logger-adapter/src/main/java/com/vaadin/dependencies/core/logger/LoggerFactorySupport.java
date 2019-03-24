package com.vaadin.dependencies.core.logger;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.vaadin.dependencies.core.logger.factory.LoggerFactory;

/**
 * <p>Abstract LoggerFactorySupport class.</p>
 *
 * @author svenruppert
 * @version $Id: $Id
 */
public abstract class LoggerFactorySupport implements LoggerFactory {

    final ConcurrentMap<String, LoggingService> mapLoggers = new ConcurrentHashMap<>(100);

    final ConstructorFunction<String, LoggingService> loggerConstructor = this::createLogger;

    /** {@inheritDoc} */
    @Override
    public final LoggingService getLogger(String name) {
        return ConcurrencyUtil.getOrPutIfAbsent(mapLoggers, name, loggerConstructor);
    }

    /**
     * <p>createLogger.</p>
     *
     * @param name a {@link String} object.
     * @return a {@link LoggingService} object.
     */
    protected abstract LoggingService createLogger(String name);


}
