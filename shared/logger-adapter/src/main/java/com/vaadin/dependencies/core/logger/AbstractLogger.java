package com.vaadin.dependencies.core.logger;

import java.util.function.Supplier;
import java.util.logging.Level;

import com.vaadin.dependencies.core.logger.tp.org.slf4j.helpers.FormattingTuple;
import com.vaadin.dependencies.core.logger.tp.org.slf4j.helpers.MessageFormatter;


/**
 * <p>Abstract AbstractLogger class.</p>
 *
 * @author svenruppert
 * @version $Id: $Id
 */
public abstract class AbstractLogger implements LoggingService {

    /** {@inheritDoc} */
    @Override
    public void finest(String message) {
        log(Level.FINEST, message);
    }

    /** {@inheritDoc} */
    @Override
    public void finest(String message, Throwable thrown) {
        log(Level.FINEST, message, thrown);
    }

    /** {@inheritDoc} */
    @Override
    public void finest(Throwable thrown) {
        log(Level.FINEST, thrown.getMessage(), thrown);
    }
    
    @Override
    public void finest(Supplier<String> message) {
      if(isFinestEnabled()) {
        finest(message.get());
      }
    }
    
    @Override
    public void finest(Supplier<String> message, Throwable thrown) {
      if(isFinestEnabled()) {
        finest(message.get(), thrown);
      }
    }
    
    @Override
    public void finest(String format, Object arg0) {
      finest(format, arg0, null);
    }
    
    @Override
    public void finest(String format, Object arg1, Object arg2) {
      if (!isFinestEnabled()) {
        return;
      }
      FormattingTuple tp = MessageFormatter.format(format, arg1, arg2);
      finest(tp.getMessage(), tp.getThrowable());
    }
      
      @Override
      public void finest(String format, Object... arguments) {
        if (!isFinestEnabled()) {
          return;
        }
      FormattingTuple tp = MessageFormatter.arrayFormat(format, arguments);
      finest(tp.getMessage(), tp.getThrowable());
      }

    /** {@inheritDoc} */
    @Override
    public void fine(String message) {
        log(Level.FINE, message);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isFinestEnabled() {
        return isLoggable(Level.FINEST);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isFineEnabled() {
        return isLoggable(Level.FINE);
    }

    /** {@inheritDoc} */
    @Override
    public void info(String message) {
        log(Level.INFO, message);
    }

    @Override
    public void info(String message, Throwable thrown) {
      log(Level.INFO, message, thrown);
    }

    @Override
    public void info(Supplier<String> message) {
      if(isInfoEnabled()) {
        info(message.get());
      }
    }

    @Override
    public boolean isInfoEnabled() {
      return isLoggable(Level.INFO);
    }
    
    @Override
    public void info(Supplier<String> message, Throwable thrown) {
      if(isInfoEnabled()) {
        info(message.get(), thrown);
      }
    }
    
    @Override
    public void info(String format, Object arg0) {
      info(format, arg0, null);
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
      if (!isInfoEnabled()) {
        return;
      }
      FormattingTuple tp = MessageFormatter.format(format, arg1, arg2);
      info(tp.getMessage(), tp.getThrowable());
    }

    @Override
    public void info(String format, Object... arguments) {
      if (!isInfoEnabled()) {
        return;
      }
      FormattingTuple tp = MessageFormatter.arrayFormat(format, arguments);
      info(tp.getMessage(), tp.getThrowable());
    }

    /** {@inheritDoc} */
    @Override
    public void severe(String message) {
        log(Level.SEVERE, message);
    }

    /** {@inheritDoc} */
    @Override
    public void severe(Throwable thrown) {
        log(Level.SEVERE, thrown.getMessage(), thrown);
    }

    /** {@inheritDoc} */
    @Override
    public void severe(String message, Throwable thrown) {
        log(Level.SEVERE, message, thrown);
    }

    @Override
    public void severe(Supplier<String> message) {
      if(isSevereEnabled()) {
        severe(message.get());
      }
    }
    
    @Override
    public void severe(Supplier<String> message, Throwable thrown) {
      if(isSevereEnabled()) {
        severe(message.get(), thrown);
      }
    }
    
    @Override
    public void severe(String format, Object arg0) {
      severe(format, arg0, null);
    }
    
    @Override
    public void severe(String format, Object arg1, Object arg2) {
      if (!isSevereEnabled()) {
        return;
      }
      FormattingTuple tp = MessageFormatter.format(format, arg1, arg2);
      severe(tp.getMessage(), tp.getThrowable());
    }
      
    @Override
    public void severe(String format, Object... arguments) {
      if (!isSevereEnabled()) {
        return;
      }
      FormattingTuple tp = MessageFormatter.arrayFormat(format, arguments);
      severe(tp.getMessage(), tp.getThrowable());
    }

    @Override
    public boolean isSevereEnabled() {
      return isLoggable(Level.SEVERE);
    }

    /** {@inheritDoc} */
    @Override
    public void warning(String message) {
        log(Level.WARNING, message);
    }

    /** {@inheritDoc} */
    @Override
    public void warning(Throwable thrown) {
        log(Level.WARNING, thrown.getMessage(), thrown);
    }

    /** {@inheritDoc} */
    @Override
    public void warning(String message, Throwable thrown) {
        log(Level.WARNING, message, thrown);
    }
    
    @Override
    public void warning(String format, Object arg0) {
      warning(format, arg0, null);
    }
    
    @Override
    public void warning(Supplier<String> message) {
      if(isWarningEnabled()) {
        warning(message.get());
      }
    }
    
    @Override
    public void warning(Supplier<String> message, Throwable thrown) {
      if(isWarningEnabled()) {
        warning(message.get(), thrown);
      }
    }

    @Override
    public boolean isWarningEnabled() {
      return isLoggable(Level.WARNING);
    }
    
    @Override
    public void warning(String format, Object arg1, Object arg2) {
      if (!isWarningEnabled()) {
        return;
      }
      FormattingTuple tp = MessageFormatter.format(format, arg1, arg2);
      warning(tp.getMessage(), tp.getThrowable());
    }
      
    @Override
    public void warning(String format, Object... arguments) {
      if (!isWarningEnabled()) {
        return;
      }
      FormattingTuple tp = MessageFormatter.arrayFormat(format, arguments);
      warning(tp.getMessage(), tp.getThrowable());
    }
}
