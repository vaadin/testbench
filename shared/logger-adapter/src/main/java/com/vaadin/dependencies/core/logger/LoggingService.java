package com.vaadin.dependencies.core.logger;

import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * <p>LoggingService interface.</p>
 *
 * @author svenruppert
 * @version $Id: $Id
 */
public interface LoggingService {


  /**
   * <p>finest.</p>
   *
   * @param message a {@link String} object.
   */
  void finest(String message);

  /**
   * <p>finest.</p>
   *
   * @param thrown a {@link Throwable} object.
   */
  void finest(Throwable thrown);

  /**
   * <p>finest.</p>
   *
   * @param message a {@link String} object.
   * @param thrown a {@link Throwable} object.
   */
  void finest(String message , Throwable thrown);
  
  void finest(Supplier<String> message);
  
  void finest(Supplier<String> message , Throwable thrown);

  void finest(String format , Object arg0);

  void finest(String format , Object arg1 , Object arg2);

  void finest(String format , Object... arguments);

  /**
   * <p>isFinestEnabled.</p>
   *
   * @return a boolean.
   */
  boolean isFinestEnabled();

  /**
   * <p>fine.</p>
   *
   * @param message a {@link String} object.
   */
  void fine(String message);

  /**
   * <p>isFineEnabled.</p>
   *
   * @return a boolean.
   */
  boolean isFineEnabled();

  /**
   * <p>info.</p>
   *
   * @param message a {@link String} object.
   */
  void info(String message);

  void info(Supplier<String> message);

  void info(Supplier<String> message , Throwable thrown);

  void info(String message , Throwable thrown);

  void info(String format , Object arg0);

  void info(String format , Object arg1 , Object arg2);

  void info(String format , Object... arguments);
  /**
   * <p>warning.</p>
   *
   * @param message a {@link String} object.
   */
  void warning(String message);

  /**
   * <p>warning.</p>
   *
   * @param thrown a {@link Throwable} object.
   */
  void warning(Throwable thrown);

  /**
   * <p>warning.</p>
   *
   * @param message a {@link String} object.
   * @param thrown a {@link Throwable} object.
   */
  void warning(String message , Throwable thrown);

  void warning(Supplier<String> message);

  void warning(Supplier<String> message , Throwable thrown);

  void warning(String format , Object arg0);

  void warning(String format , Object arg1 , Object arg2);

  void warning(String format , Object... arguments);

  /**
   * <p>severe.</p>
   *
   * @param message a {@link String} object.
   */
  void severe(String message);

  /**
   * <p>severe.</p>
   *
   * @param thrown a {@link Throwable} object.
   */
  void severe(Throwable thrown);

  void severe(Supplier<String> message);

  void severe(Supplier<String> message , Throwable thrown);
  /**
   * <p>severe.</p>
   *
   * @param message a {@link String} object.
   * @param thrown a {@link Throwable} object.
   */
  void severe(String message , Throwable thrown);

  void severe(String format , Object arg0);

  void severe(String format , Object arg1 , Object arg2);

  void severe(String format , Object... arguments);

  /**
   * <p>log.</p>
   *
   * @param level a {@link Level} object.
   * @param message a {@link String} object.
   */
  void log(Level level , String message);

  /**
   * <p>log.</p>
   *
   * @param level a {@link Level} object.
   * @param message a {@link String} object.
   * @param thrown a {@link Throwable} object.
   */
  void log(Level level , String message , Throwable thrown);

//  void log(LogEvent logEvent);
  /**
   * <p>log.</p>
   *
   * @param logEvent a {@link LogEvent} object.
   */
  default void log(LogEvent logEvent) {
    LogRecord logRecord = logEvent.getLogRecord();
    Level level = logEvent.getLogRecord().getLevel();
    String message = logRecord.getMessage();
    Throwable thrown = logRecord.getThrown();
    log(level, message, thrown);
  }

  /**
   * <p>getLevel.</p>
   *
   * @return a {@link Level} object.
   */
  Level getLevel();

  /**
   * <p>isLoggable.</p>
   *
   * @param level a {@link Level} object.
   * @return a boolean.
   */
  boolean isLoggable(Level level);

  boolean isWarningEnabled();

  boolean isSevereEnabled();

  boolean isInfoEnabled();

}
