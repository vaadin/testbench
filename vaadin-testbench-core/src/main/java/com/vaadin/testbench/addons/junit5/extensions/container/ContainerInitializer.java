package com.vaadin.testbench.addons.junit5.extensions.container;

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

import com.vaadin.testbench.configuration.ConfigurationFinder;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.lang.reflect.Method;

public interface ContainerInitializer {

    ContainerInfo CONTAINER_INFO = ConfigurationFinder.findTestConfiguration().getContainerInfo();

    /**
     * The test configuration container info.
     *
     * @return the test configuration container info.
     */
    static ContainerInfo containerInfo() {
        return CONTAINER_INFO;
    }

    void beforeAll(Class<?> testClass, ExtensionContext context) throws Exception;

    void beforeEach(Method testMethod, ExtensionContext context) throws Exception;

    void afterEach(Method testMethod, ExtensionContext context) throws Exception;

    void afterAll(Class<?> testClass, ExtensionContext context) throws Exception;
}
