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
package org.rapidpm.vaadin.addons.webdriver.conf;

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
