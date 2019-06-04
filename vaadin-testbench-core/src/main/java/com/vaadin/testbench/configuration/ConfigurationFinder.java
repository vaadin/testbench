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

/**
 * An internal helper class for locating the {@link TestConfiguration} class in look,
 * using system properties, environment variables or classpath scanning.
 */
public class ConfigurationFinder {

    /**
     * System property for specifying the fully qualified name of the test configuration class.
     * This takes precedence over the {@link #CONFIG_CLASS_ENVIRONMENT_VARIABLE} environment variable.
     *
     * @see #CONFIG_CLASS_ENVIRONMENT_VARIABLE
     * @see TestConfiguration
     */
    public static final String CONFIG_CLASS_SYSTEM_PROPERTY = "testbench.configuration.class";

    /**
     * Environment variable for specifying the fully qualified name of the test configuration class.
     * This is only considered when {@link #CONFIG_CLASS_SYSTEM_PROPERTY} system property is not set.
     *
     * This is probably only useful in a build environment as it might not make sense
     * on a developer machine with multiple projects.
     *
     * @see #CONFIG_CLASS_SYSTEM_PROPERTY
     * @see TestConfiguration
     */
    public static final String CONFIG_CLASS_ENVIRONMENT_VARIABLE = "TESTBENCH_CONFIGURATION_CLASS";

    private static final String TEST_CONFIGURATION_CLASSNAME = TestConfiguration.class.getSimpleName();

    private ConfigurationFinder() {
    }

    /**
     * Locates the {@link TestConfiguration} implementation for the test run in this order:
     * <ol>
     *     <li>System property</li>
     *     <li>Environment variable</li>
     *     <li>Module / classpath scanning</li>
     * </ol>
     *
     * Classpath scanning is a best-effort attempt to find the {@link TestConfiguration}
     * without requiring any settings.
     *
     * @return the discovered test configuration class.
     * @throws IllegalArgumentException if the class set by the system property
     *                                  is not found or cannot be instantiated.
     * @throws IllegalStateException    if during classpath scanning zero or multiple
     *                                  implementations of {@link TestConfiguration} were found.
     */
    public static TestConfiguration findTestConfiguration()
            throws IllegalArgumentException, IllegalStateException {

        return findTestConfiguration(new ClassGraph().enableClassInfo());
    }

    @VisibleForTesting
    static TestConfiguration findTestConfiguration(ClassGraph classGraph) {
        final String targetConfigurationSystemProperty
                = System.getProperty(CONFIG_CLASS_SYSTEM_PROPERTY);
        if (targetConfigurationSystemProperty != null) {
            logger().debug(TEST_CONFIGURATION_CLASSNAME
                    + " implementation found via system property: "
                    + targetConfigurationSystemProperty);

            return instantiate(targetConfigurationSystemProperty);
        }

        final String targetConfigurationEnvironmentVariable
                = System.getenv(CONFIG_CLASS_ENVIRONMENT_VARIABLE);
        if (targetConfigurationEnvironmentVariable != null) {
            logger().debug(TEST_CONFIGURATION_CLASSNAME
                    + " implementation found via environment variable: "
                    + targetConfigurationEnvironmentVariable);

            return instantiate(targetConfigurationEnvironmentVariable);
        }

        try (ScanResult scanResult = classGraph.scan()) {
            final ClassInfoList targetConfiguration = scanResult
                    .getClassesImplementing(TestConfiguration.class.getCanonicalName());

            if (targetConfiguration.size() == 0) {
                throw new IllegalStateException("No implementation of "
                        + TEST_CONFIGURATION_CLASSNAME + " found");
            }

            if (targetConfiguration.size() > 1) {
                throw new IllegalStateException("Multiple implementations of "
                        + TEST_CONFIGURATION_CLASSNAME
                        + " found. Either ensure that only one implementation exist "
                        + "or specify the desired implementation by setting the system property '"
                        + CONFIG_CLASS_SYSTEM_PROPERTY + "'");
            }

            final String targetConfigurationClassName = targetConfiguration.get(0).getName();
            logger().debug(TEST_CONFIGURATION_CLASSNAME
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
                        + ConfigurationFinder.TEST_CONFIGURATION_CLASSNAME);
            }

            return ((TestConfiguration) config);
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new IllegalArgumentException(fullyQualifiedTargetConfigurationClassName
                    + " is not instantiatable", e);
        }
    }
}
