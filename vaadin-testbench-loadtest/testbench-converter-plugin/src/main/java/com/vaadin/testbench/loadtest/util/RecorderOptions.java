/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.loadtest.util;

/**
 * Bundle of configuration passed to the HAR-to-k6 conversion step. Keeping
 * these options in a single object avoids method-signature churn as new knobs
 * are added.
 *
 * @param thresholdConfig
 *            threshold configuration for the generated script
 * @param responseCheckConfig
 *            custom response validation checks to inject
 */
public record RecorderOptions(ThresholdConfig thresholdConfig,
        ResponseCheckConfig responseCheckConfig) {

    public static final RecorderOptions DEFAULT = new RecorderOptions(
            ThresholdConfig.DEFAULT, ResponseCheckConfig.EMPTY);

    public RecorderOptions {
        if (thresholdConfig == null) {
            thresholdConfig = ThresholdConfig.DEFAULT;
        }
        if (responseCheckConfig == null) {
            responseCheckConfig = ResponseCheckConfig.EMPTY;
        }
    }
}
