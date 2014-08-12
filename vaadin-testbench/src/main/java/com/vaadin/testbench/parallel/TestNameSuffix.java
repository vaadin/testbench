/**
 * Copyright (C) 2012 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Add-On License 3.0
 * (CVALv3).
 *
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 *
 * You should have received a copy of the license along with this program.
 * If not, see <http://vaadin.com/license/cval-3>.
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
