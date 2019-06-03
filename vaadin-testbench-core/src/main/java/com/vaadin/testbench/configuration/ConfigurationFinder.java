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

import com.google.common.annotations.VisibleForTesting;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;

import static com.vaadin.testbench.TestBenchLogger.logger;

public class ConfigurationFinder {

    @VisibleForTesting
    static final String TESTBENCH_CONFIG_CLASS_SYSTEM_PROPERTY = "testbench.configuration.class";

    /**
     * This should probably only be used in a CI environment as it might not make sense
     * on a developer machine with multiple projects.
     */
    private static final String TESTBENCH_CONFIG_CLASS_ENVIRONMENT_VARIABLE = "TESTBENCH_CONFIGURATION_CLASS";

    private static final String TARGET_CONFIGURATION_CLASSNAME = TestConfiguration.class.getSimpleName();

    private ConfigurationFinder() {
    }

    public static TestConfiguration findTestConfiguration() {
        return findTestConfiguration(new ClassGraph().enableClassInfo());
    }

    @VisibleForTesting
    static TestConfiguration findTestConfiguration(ClassGraph classGraph) {
        final String targetConfigurationSystemProperty
                = System.getProperty(TESTBENCH_CONFIG_CLASS_SYSTEM_PROPERTY);
        if (targetConfigurationSystemProperty != null) {
            logger().debug(TARGET_CONFIGURATION_CLASSNAME
                    + " implementation found via system property: "
                    + targetConfigurationSystemProperty);

            return instantiate(targetConfigurationSystemProperty);
        }

        final String targetConfigurationEnvironmentVariable
                = System.getenv(TESTBENCH_CONFIG_CLASS_ENVIRONMENT_VARIABLE);
        if (targetConfigurationEnvironmentVariable != null) {
            logger().debug(TARGET_CONFIGURATION_CLASSNAME
                    + " implementation found via environment variable: "
                    + targetConfigurationEnvironmentVariable);

            return instantiate(targetConfigurationEnvironmentVariable);
        }

        try (ScanResult scanResult = classGraph.scan()) {
            final ClassInfoList targetConfiguration = scanResult
                    .getClassesImplementing(TestConfiguration.class.getCanonicalName());

            if (targetConfiguration.size() == 0) {
                throw new IllegalStateException("No implementation of "
                        + TARGET_CONFIGURATION_CLASSNAME + " found");
            }

            if (targetConfiguration.size() > 1) {
                throw new IllegalStateException("Multiple implementations of "
                        + TARGET_CONFIGURATION_CLASSNAME
                        + " found. Either ensure that only one implementation exist "
                        + "or specify the desired implementation by setting the system property '"
                        + TESTBENCH_CONFIG_CLASS_SYSTEM_PROPERTY + "'");
            }

            final String targetConfigurationClassName = targetConfiguration.get(0).getName();
            logger().debug(TARGET_CONFIGURATION_CLASSNAME
                    + " implementation found by class scanning: "
                    + targetConfigurationClassName);

            return instantiate(targetConfigurationClassName);
        }
    }

    private static TestConfiguration instantiate(String fullyQualifiedTargetConfigurationClassName) {
        try {
            final Object config = Class.forName(fullyQualifiedTargetConfigurationClassName).newInstance();
            if (!(config instanceof TestConfiguration)) {
                throw new IllegalArgumentException(fullyQualifiedTargetConfigurationClassName
                        + " does not implement "
                        + ConfigurationFinder.TARGET_CONFIGURATION_CLASSNAME);
            }

            return ((TestConfiguration) config);
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new IllegalArgumentException(fullyQualifiedTargetConfigurationClassName
                    + " is not instantiatable", e);
        }
    }
}
