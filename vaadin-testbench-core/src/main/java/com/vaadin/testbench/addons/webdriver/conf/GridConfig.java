package com.vaadin.testbench.addons.webdriver.conf;

/*-
 * #%L
 * vaadin-testbench-core
 * %%
 * Copyright (C) 2019 Vaadin Ltd
 * %%
 * Copyright (C) ${year} Vaadin Ltd
 * 
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
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.Collections;
import java.util.List;

@Bean
public class GridConfig {

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

    public enum Type {
        GENERIC, SELENOID, BROWSERSTACK, SAUCELABS
    }
}
