package com.vaadin.testbench.configuration.additionaltargetconfiguration;

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

import com.vaadin.testbench.addons.junit5.extensions.container.ContainerInfo;
import com.vaadin.testbench.configuration.Target;
import com.vaadin.testbench.configuration.TestConfiguration;

import java.util.Collections;
import java.util.List;

public class AdditionalTestConfiguration implements TestConfiguration {

    @Override
    public List<Target> getBrowserTargets() {
        return Collections.singletonList(TestConfiguration.localSafari());
    }

    @Override
    public ContainerInfo getContainerInfo() {
        return TestConfiguration.defaultContainerInfo();
    }
}
