package com.vaadin.testbench.addons.junit5.extensions.container;

import javax.enterprise.context.Dependent;

import com.vaadin.dependencies.core.logger.HasLogger;

@Dependent
public class UpperCaseService implements HasLogger {
  public UpperCaseService() {
    logger().info("UpperCaseService created .. ");
  }

  public String upperCase(String txt) {
    return txt.toUpperCase();
  }
}
