package com.vaadin.testbench.addons.webdriver.conf;

/*-
 * #%L
 * vaadin-testbench-core
 * %%
 * Copyright (C) 2019 Vaadin Ltd
 * %%
 * This program is available under Commercial Vaadin Add-On License 3.0
 * (CVALv3).
 * 
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 * 
 * You should have received a copy of the license along with this program.
 * If not, see <http://vaadin.com/license/cval-3>.
 * #L%
 */

import net.vergien.beanautoutils.annotation.Bean;

import java.util.List;

@Bean
public class WebdriversConfig {

    public static final String COMPATTESTING = "compattesting";
    public static final String COMPATTESTING_GRID = COMPATTESTING + ".grid";

    private final List<GridConfig> gridConfigs;

    public WebdriversConfig(List<GridConfig> gridConfigs) {
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
        return WebdriversConfigBeanUtil.doEquals(this, obj);
    }
}
