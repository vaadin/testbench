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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.rapidpm.dependencies.core.logger.factory.LoggerFactory;
import org.rapidpm.dependencies.core.logger.factory.NoLogFactory;
import org.rapidpm.dependencies.core.logger.factory.StandardLoggerFactory;

/**
 * <p>Logger class.</p>
 *
 * @author svenruppert
 * @version $Id: $Id
 */
public class Logger {

  /** Constant <code>RAPIDPM_LOGGING_TYPE="rapidpm.logging.type"</code> */
  public static final String RAPIDPM_LOGGING_TYPE = "rapidpm.logging.type";
  /** Constant <code>RAPIDPM_LOGGING_CLASS="rapidpm.logging.class"</code> */
  public static final String RAPIDPM_LOGGING_CLASS = "rapidpm.logging.class";

  private static volatile LoggerFactory loggerFactory;
  private static final Object FACTORY_LOCK = new Object();

  private Logger() {
  }

  /**
   * <p>getLogger.</p>
   *
   * @param clazz a {@link Class} object.
   * @return a {@link org.rapidpm.dependencies.core.logger.LoggingService} object.
   */
  public static LoggingService getLogger(Class clazz) {
    return getLogger(clazz.getName());
  }

  /**
   * <p>getLogger.</p>
   *
   * @param name a {@link String} object.
   * @return a {@link org.rapidpm.dependencies.core.logger.LoggingService} object.
   */
  public static LoggingService getLogger(String name) {
    //noinspection DoubleCheckedLocking
    if (loggerFactory == null) {
      //noinspection SynchronizationOnStaticField
      synchronized (FACTORY_LOCK) {
        if (loggerFactory == null) {
          String loggerType = System.getProperty(RAPIDPM_LOGGING_TYPE);
          loggerFactory = newLoggerFactory(loggerType);
        }
      }
    }
    return loggerFactory.getLogger(name);
  }

  /**
   * <p>newLoggerFactory.</p>
   *
   * @param loggerType a {@link String} object.
   * @return a {@link org.rapidpm.dependencies.core.logger.factory.LoggerFactory} object.
   */
  public static LoggerFactory newLoggerFactory(String loggerType) {
    LoggerFactory loggerFactory = null;
    String loggerClass = System.getProperty(RAPIDPM_LOGGING_CLASS);
    if (loggerClass != null) {
      loggerFactory = loadLoggerFactory(loggerClass);
    }

    if (loggerFactory == null) {
      if (loggerType != null) {
        if ("log4j".equals(loggerType)) {
          loggerFactory = loadLoggerFactory("org.rapidpm.dependencies.core.logger.factory.Log4jFactory");
        } else if ("log4j2".equals(loggerType)) {
          loggerFactory = loadLoggerFactory("org.rapidpm.dependencies.core.logger.factory.Log4j2Factory");
        } else if ("slf4j".equals(loggerType)) {
          loggerFactory = loadLoggerFactory("org.rapidpm.dependencies.core.logger.factory.Slf4jFactory");
        } else if ("jdk".equals(loggerType)) {
          loggerFactory = new StandardLoggerFactory();
        } else if ("none".equals(loggerType)) {
          loggerFactory = new NoLogFactory();
        }
      }
    }

    if (loggerFactory == null) {
      loggerFactory = new StandardLoggerFactory();
    }
    return loggerFactory;
  }

  private static LoggerFactory loadLoggerFactory(String className) {
    try {
      final Class<?> forName = Class.forName(className);
      final Constructor<?> declaredConstructor = forName.getDeclaredConstructor();
      return (LoggerFactory) declaredConstructor.newInstance();
    } catch (ClassNotFoundException
        | InstantiationException
        | IllegalAccessException
        | InvocationTargetException
        | NoSuchMethodException e) {
      e.printStackTrace();
      return null;
    }
  }
}
