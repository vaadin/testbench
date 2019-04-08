package com.vaadin.testbench.addons.junit5.extensions.container;

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

import org.junit.jupiter.api.extension.ExtensionContext;

import static com.vaadin.testbench.addons.junit5.extensions.ExtensionFunctions.valueAsIntPlain;
import static com.vaadin.testbench.addons.junit5.extensions.ExtensionFunctions.valueAsStringPlain;
import static com.vaadin.testbench.addons.junit5.extensions.container.NetworkFunctions.SERVER_IP;
import static com.vaadin.testbench.addons.junit5.extensions.container.NetworkFunctions.SERVER_PORT;

public interface ExtensionContextFunctions {

    static Integer serverPort(ExtensionContext ctx) {
        return valueAsIntPlain(SERVER_PORT, ctx);
    }

    static String serverIp(ExtensionContext ctx) {
        return valueAsStringPlain(SERVER_IP, ctx);
    }

    static ContainerInfo containerInfo(ExtensionContext ctx) {
        return new ContainerInfo(serverPort(ctx), serverIp(ctx));
    }
}
