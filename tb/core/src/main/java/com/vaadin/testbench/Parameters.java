/**
 * Copyright (C) 2012 Vaadin Ltd
 * <p>
 * This program is available under Commercial Vaadin Add-On License 3.0
 * (CVALv3).
 * <p>
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 * <p>
 * You should have received a copy of the license along with this program.
 * If not, see <http://vaadin.com/license/cval-3>.
 */
package com.vaadin.testbench;

import static com.vaadin.frp.SystemProperties.systemPropertyInt;

import java.util.function.Function;

import com.vaadin.dependencies.core.logger.HasLogger;
import com.vaadin.frp.model.Result;

public final class Parameters implements HasLogger {

  private static final Function<String, Result<Integer>> propertyInt = systemPropertyInt(Parameters.class);

  private static int maxAttempts = propertyInt.apply("maxAttempts")
                                              .getOrElse(() -> 1);

  /**
   * Gets the maximum number of times to run the test in case of a random
   * failure.
   *
   * @return maximum attempts the test can be run.
   */
  public static int getMaxAttempts() {
    return maxAttempts;
  }

  /**
   * Sets the maximum number of times to run the test in case of a random
   * failure
   *
   * @param maxAttempts maximum attempts the test can be run.
   */
  public static void setMaxAttempts(int maxAttempts) {
    Parameters.maxAttempts = maxAttempts;
  }

}
