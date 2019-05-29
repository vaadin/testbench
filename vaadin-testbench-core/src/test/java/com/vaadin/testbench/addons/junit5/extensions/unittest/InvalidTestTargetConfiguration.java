package com.vaadin.testbench.addons.junit5.extensions.unittest;

import com.vaadin.testbench.configuration.Target;
import com.vaadin.testbench.configuration.TargetConfiguration;

import java.util.Collections;
import java.util.List;

public class InvalidTestTargetConfiguration {

    public List<Target> getBrowserTargets() {
        return Collections.singletonList(TargetConfiguration.localSafari());
    }
}
