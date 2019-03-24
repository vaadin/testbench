package com.vaadin.dependencies.core.logger.factory;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import com.vaadin.dependencies.core.logger.AbstractLogger;
import com.vaadin.dependencies.core.logger.LogEvent;
import com.vaadin.dependencies.core.logger.LoggerFactorySupport;
import com.vaadin.dependencies.core.logger.LoggingService;

/**
 * <p>StandardLoggerFactory class.</p>
 *
 * @author svenruppert
 * @version $Id: $Id
 */
public class StandardLoggerFactory extends LoggerFactorySupport implements LoggerFactory {

  /** {@inheritDoc} */
  @Override
  protected LoggingService createLogger(String name) {
    final Logger l = Logger.getLogger(name);
    return new StandardLogger(l);
  }

  static class StandardLogger extends AbstractLogger {
    private final Logger logger;

    public StandardLogger(Logger logger) {
      this.logger = logger;
    }

    @Override
    public void log(Level level , String message) {
      log(level , message , null);
    }

    @Override
    public void log(Level level , String message , Throwable thrown) {
      LogRecord logRecord = new LogRecord(level , message);
      logRecord.setLoggerName(logger.getName());
      logRecord.setThrown(thrown);
      logRecord.setSourceClassName(logger.getName());
      logger.log(logRecord);
    }

    @Override
    public void log(LogEvent logEvent) {
      logger.log(logEvent.getLogRecord());
    }

    @Override
    public Level getLevel() {
      return logger.getLevel();
    }

    @Override
    public boolean isLoggable(Level level) {
      return logger.isLoggable(level);
    }
  }
}
