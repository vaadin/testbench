package com.vaadin.testbench.tests;

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

import com.google.common.base.Joiner;
import com.vaadin.testbench.addons.junit5.extensions.container.ContainerInfo;
import com.vaadin.testbench.addons.webdriver.BrowserType;
import com.vaadin.testbench.configuration.Target;
import com.vaadin.testbench.configuration.TestConfiguration;
import org.apache.commons.lang3.SystemUtils;
import org.openqa.selenium.Platform;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.vaadin.testbench.configuration.TestConfiguration.localChrome;
import static com.vaadin.testbench.configuration.TestConfiguration.localFirefox;
import static com.vaadin.testbench.configuration.TestConfiguration.saucelabs;

public class BrowserConfiguration implements TestConfiguration {

    @Override
    public List<Target> getBrowserTargets() {
        return Collections.singletonList(
                saucelabs(BrowserType.CHROME, "74", Platform.WIN8_1)
        );
    }

    @Override
    public ContainerInfo getContainerInfo() {
        return TestConfiguration.defaultContainerInfo();
    }

    /**
     * Only here for local testing.
     */
    private static class LocalBrowsers {

        private static final String DRIVERS_DIRECTORY = "../_data/webdrivers/";
        private static final String ARCH = "64bit";
        private static final String OS = SystemUtils.IS_OS_WINDOWS ? "windows"
                : SystemUtils.IS_OS_LINUX ? "linux"
                : "mac";

        private static List<Target> getLocalBrowserTargets() {
            return Arrays.asList(
                    localChrome(pathFor("chromedriver"), true),
                    localFirefox(pathFor("geckodriver"), true));
        }

        private static String pathFor(String driverName) {
            return Joiner.on("").join(DRIVERS_DIRECTORY, driverName, '-', OS, '-', ARCH);
        }
    }
}
