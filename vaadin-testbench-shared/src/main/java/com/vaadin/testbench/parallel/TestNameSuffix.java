/**
 * Copyright (C) 2000-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.parallel;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Defines a system property to be used as part of the test name.<br>
 * It may be useful to add information related to the system or the machine
 * itself, such as the OS.<br>
 * This will affect, for instance, the names of screenshots taken in case of
 * test failure.<br>
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface TestNameSuffix {

    String property();

}
