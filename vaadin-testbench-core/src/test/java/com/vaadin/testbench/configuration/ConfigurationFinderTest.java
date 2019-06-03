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

import com.vaadin.testbench.addons.webdriver.BrowserType;
import io.github.classgraph.ClassGraph;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.vaadin.testbench.configuration.ConfigurationFinder.TESTBENCH_TARGET_CONFIGURATION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConfigurationFinderTest {

    private String configClassPropertyBackup;

    @BeforeEach
    void setup() {
        configClassPropertyBackup = System.getProperty(TESTBENCH_TARGET_CONFIGURATION);
        System.clearProperty(TESTBENCH_TARGET_CONFIGURATION);
    }

    @AfterEach
    void tearDown() {
        if (configClassPropertyBackup != null) {
            System.setProperty(TESTBENCH_TARGET_CONFIGURATION, configClassPropertyBackup);
        }
    }

    @Test
    void findBrowserTargets_systemProperty() {
        System.setProperty(TESTBENCH_TARGET_CONFIGURATION,
                SampleTestConfiguration.class.getCanonicalName());

        final List<Target> targets = ConfigurationFinder.findTestConfiguration().getBrowserTargets();

        assertEquals(1, targets.size());
        assertEquals(BrowserType.SAFARI.browserName(),
                targets.get(0).getDesiredCapabilities().getBrowserName());
    }

    @Test
    void findBrowserTargets_systemProperty_nonExistentClass() {
        System.setProperty(TESTBENCH_TARGET_CONFIGURATION, "NonExistentClass");

        assertThrows(IllegalArgumentException.class, ConfigurationFinder::findTestConfiguration);
    }

    @Test
    void findBrowserTargets_systemProperty_classNotImplementingTargetConfiguration() {
        System.setProperty(TESTBENCH_TARGET_CONFIGURATION,
                InvalidTestConfiguration.class.getCanonicalName());

        assertThrows(IllegalArgumentException.class, ConfigurationFinder::findTestConfiguration);
    }

    @Test
    void findBrowserTargets_pathScanning() {
        final ClassGraph limitedScopeClassGraph = new ClassGraph()
                .enableClassInfo()
                .whitelistPackagesNonRecursive(getClass().getPackage().getName());
        // There's only one implementation in the specified package tree.

        final List<Target> targets = ConfigurationFinder
                .findTestConfiguration(limitedScopeClassGraph)
                .getBrowserTargets();

        assertEquals(1, targets.size());
        assertEquals(BrowserType.SAFARI.browserName(),
                targets.get(0).getDesiredCapabilities().getBrowserName());
    }

    @Test
    void findBrowserTargets_pathScanning_none() {
        final ClassGraph tooLimitedScopedClassGraph = new ClassGraph()
                .enableClassInfo()
                .whitelistPackagesNonRecursive("com.vaadin.testbench");
        // There's no implementation in the specified package.

        assertThrows(IllegalStateException.class,
                () -> ConfigurationFinder.findTestConfiguration(tooLimitedScopedClassGraph));
    }

    @Test
    void findBrowserTargets_pathScanning_multiple() {
        // There are multiple implementations on the full classpath.
        assertThrows(IllegalStateException.class, ConfigurationFinder::findTestConfiguration);
    }
}
