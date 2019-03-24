package com.vaadin.testbench.addons.webdriver.conf;

import java.util.Collections;
import java.util.List;
import org.openqa.selenium.remote.DesiredCapabilities;
import net.vergien.beanautoutils.annotation.Bean;

@Bean
public class GridConfig {

  public enum Type {
    GENERIC, SELENOID, BROWSERSTACK, SAUCELABS
  }

  private final Type type;
  private final String name;
  private final String target;
  private final List<DesiredCapabilities> desiredCapabilities;

  public GridConfig(Type type, String name, String target,
      List<DesiredCapabilities> desiredCapabilities) {
    super();
    this.type = type;
    this.name = name;
    this.target = target;
    this.desiredCapabilities = Collections.unmodifiableList(desiredCapabilities);
  }

  public Type getType() {
    return type;
  }

  public String getName() {
    return name;
  }

  public String getTarget() {
    return target;
  }

  public List<DesiredCapabilities> getDesiredCapabilities() {
    return desiredCapabilities;
  }

  @Override
  public String toString() {
    return GridConfigBeanUtil.doToString(this);
  }

  @Override
  public int hashCode() {
    return GridConfigBeanUtil.doToHashCode(this);
  }

  @Override
  public boolean equals(Object obj) {
    return GridConfigBeanUtil.doEquals(this, obj);
  }
}
