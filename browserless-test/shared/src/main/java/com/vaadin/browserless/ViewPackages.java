/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vaadin.testbench.unit;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to use to scan given packages for routes and error views.
 *
 * Packages can be defined by their fully-qualified name or by providing classes
 * that are members of them.
 *
 * If both {@link #classes()} and {@link #packages()} are empty, the scan is
 * assumed to be limited to the annotated class package.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface ViewPackages {

    /**
     * Array of classes whose packages will be scanned for views
     *
     * @return Array of classes whose packages will be scanned for views
     */
    Class<?>[] classes() default {};

    /**
     * Array of packages to scan for views
     *
     * @return String array of packages to scan
     */
    String[] packages() default {};
}
