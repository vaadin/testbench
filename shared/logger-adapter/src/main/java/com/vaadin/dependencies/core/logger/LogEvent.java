package com.vaadin.dependencies.core.logger;

import java.util.EventObject;
import java.util.logging.LogRecord;

/**
 * <p>LogEvent class.</p>
 *
 * @author svenruppert
 * @version $Id: $Id
 */
public class LogEvent<T> extends EventObject {
  final LogRecord logRecord;
  final T member;

  /**
   * <p>Constructor for LogEvent.</p>
   *
   * @param logRecord a {@link LogRecord} object.
   * @param member a T object.
   */
  public LogEvent(LogRecord logRecord , T member) {
    super(member);
    this.logRecord = logRecord;
    this.member = member;
  }

  /**
   * <p>Getter for the field <code>member</code>.</p>
   *
   * @return a T object.
   */
  public T getMember() {
    return member;
  }

  /**
   * <p>Getter for the field <code>logRecord</code>.</p>
   *
   * @return a {@link LogRecord} object.
   */
  public LogRecord getLogRecord() {
    return logRecord;
  }
}
