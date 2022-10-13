package com.vaadin.testbench.capabilities;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.remote.DesiredCapabilities;

public class DesiredCapabilitiesExtension implements Extension, BeforeEachCallback, ExecutionCondition {

    private final DesiredCapabilities desiredCapabilities;

    public DesiredCapabilitiesExtension(DesiredCapabilities desiredCapabilities) {
        this.desiredCapabilities = desiredCapabilities;
    }

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        return DesiredCapabilitiesUtil.evaluateExecutionCondition(context);
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        ((CapabilitiesTest) context.getRequiredTestInstance())
                .setDesiredCapabilities(desiredCapabilities);
    }
}
