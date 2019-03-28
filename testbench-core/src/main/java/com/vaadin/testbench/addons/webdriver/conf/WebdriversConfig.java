package com.vaadin.testbench.addons.webdriver.conf;

import java.util.List;

import net.vergien.beanautoutils.annotation.Bean;

@Bean
public class WebdriversConfig {

  public static final String COMPATTESTING = "compattesting";
  public static final String COMPATTESTING_GRID = COMPATTESTING + ".grid";

  private final List<GridConfig> gridConfigs;

  public WebdriversConfig( List<GridConfig> gridConfigs) {
    this.gridConfigs = gridConfigs;
//    logger().info("WebdriversConfig was created .. - " + toString());
  }

  public List<GridConfig> getGridConfigs() {
    return gridConfigs;
  }

  @Override
  public String toString() {
    return WebdriversConfigBeanUtil.doToString(this);
  }

  @Override
  public int hashCode() {
    return WebdriversConfigBeanUtil.doToHashCode(this);
  }

  @Override
  public boolean equals(Object obj) {
    return WebdriversConfigBeanUtil.doEquals(this , obj);
  }
}
