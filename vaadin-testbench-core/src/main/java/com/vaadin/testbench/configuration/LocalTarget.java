package com.vaadin.testbench.configuration;

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

import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.remote.DesiredCapabilities;

public class LocalTarget extends Target {

    private final DesiredCapabilities desiredCapabilities;
    private MutableCapabilities browserOptions;
    private String driverPath;

    public LocalTarget(DesiredCapabilities desiredCapabilities,
                       MutableCapabilities browserOptions,
                       String driverPath) {

        this.desiredCapabilities = desiredCapabilities;
        this.browserOptions = browserOptions;
        this.driverPath = driverPath;
    }

    @Override
    public boolean isLocal() {
        return true;
    }

    @Override
    public DesiredCapabilities getDesiredCapabilities() {
        return desiredCapabilities;
    }

    public MutableCapabilities getBrowserOptions() {
        return browserOptions;
    }

    public String getDriverPath() {
        return driverPath;
    }
}
