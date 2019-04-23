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

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import static com.vaadin.testbench.TestBenchLogger.logger;
import static com.vaadin.testbench.addons.junit5.extensions.container.ExtensionContextFunctions.containerInfo;

public class ContainerInfoExtension implements BeforeEachCallback {

    private ContainerInfo containerInfo;

    public int port() {
        return containerInfo.port();
    }

    public String host() {
        return containerInfo.host();
    }

    @Override
    public void beforeEach(ExtensionContext extensionContext) {
        logger().debug("ContainerInfoExtension - beforeEach ");
        containerInfo = containerInfo(extensionContext);
        logger().debug("ContainerInfoExtension - " + containerInfo);
    }
}
