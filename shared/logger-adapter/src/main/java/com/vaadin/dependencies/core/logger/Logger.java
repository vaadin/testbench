package com.vaadin.dependencies.core.logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.vaadin.dependencies.core.logger.factory.LoggerFactory;
import com.vaadin.dependencies.core.logger.factory.NoLogFactory;
import com.vaadin.dependencies.core.logger.factory.StandardLoggerFactory;

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
   * @return a {@link LoggingService} object.
   */
  public static LoggingService getLogger(Class clazz) {
    return getLogger(clazz.getName());
  }

  /**
   * <p>getLogger.</p>
   *
   * @param name a {@link String} object.
   * @return a {@link LoggingService} object.
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
   * @return a {@link LoggerFactory} object.
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
          loggerFactory = loadLoggerFactory("Log4jFactory");
        } else if ("log4j2".equals(loggerType)) {
          loggerFactory = loadLoggerFactory("Log4j2Factory");
        } else if ("slf4j".equals(loggerType)) {
          loggerFactory = loadLoggerFactory("Slf4jFactory");
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
