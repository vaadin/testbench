package com.vaadin.testbench.addons.junit5.extensions.unittest;

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

import com.vaadin.testbench.configuration.Target;
import com.vaadin.testbench.configuration.TargetConfiguration;

import java.util.Collections;
import java.util.List;

public class TestTargetConfiguration implements TargetConfiguration {

    @Override
    public List<Target> getBrowserTargets() {
        return Collections.singletonList(TargetConfiguration.localSafari());
    }
}
