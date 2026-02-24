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
package com.vaadin.browserless;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to use to scan given packages for component wrappers outside the
 * default {@code com.vaadin.flow.component}.
 * <p/>
 * This makes adding custom component wrappers simpler as they can then use
 * package protected fields and methods.
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ComponentTesterPackages {

    /**
     * Array of packages to scan for {@link ComponentTester} implementations.
     * <p/>
     * Implementation should use the {@link Tests} annotation to be used
     * automatically in the {@code wraps(Component)} method.
     *
     * @return String array of packages to scan
     */
    String[] value();
}
