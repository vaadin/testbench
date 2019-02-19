/**
 * Copyright © 2017 Sven Ruppert (sven.ruppert@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.rapidpm.dependencies.core.logger;

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
