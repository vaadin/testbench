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
package com.vaadin.testbench.addons.webdriver.conf;

import java.util.List;

import com.vaadin.dependencies.core.logger.HasLogger;
import net.vergien.beanautoutils.annotation.Bean;

@Bean
public class WebdriversConfig implements HasLogger {

  public static final String COMPATTESTING = "compattesting";
  public static final String COMPATTESTING_GRID = COMPATTESTING + ".grid";

  private final List<GridConfig> gridConfigs;

  public WebdriversConfig( List<GridConfig> gridConfigs) {
    this.gridConfigs = gridConfigs;
    logger().info("WebdriversConfig was created .. - " + toString());
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
