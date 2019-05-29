package com.vaadin.testbench.addons.junit5.extensions.unittest.additionaltargetconfiguration;

import com.vaadin.testbench.configuration.Target;
import com.vaadin.testbench.configuration.TargetConfiguration;

import java.util.Collections;
import java.util.List;

public class AdditionalTestTargetConfiguration implements TargetConfiguration {

    @Override
    public List<Target> getBrowserTargets() {
        return Collections.singletonList(TargetConfiguration.localSafari());
    }
}
