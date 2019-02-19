/**
 * Copyright © 2017 Sven Ruppert (sven.ruppert@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.rapidpm.vaadin.addons.testbench.junit5.extensions.unittest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.rapidpm.vaadin.addons.testbench.junit5.extension.unitest.PageObjectInvocationContextProvider;
import org.rapidpm.vaadin.addons.testbench.junit5.extension.unitest.PageObjectWebDriverCleanerExtension;
import org.rapidpm.vaadin.addons.testbench.junit5.extensions.ConvertWebdriverTestExtension;
import org.rapidpm.vaadin.addons.testbench.junit5.extensions.container.ServletContainerExtension;

/**
 *
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(ServletContainerExtension.class)
@ExtendWith(PageObjectInvocationContextProvider.class)

//convert Driver
@ExtendWith(ConvertWebdriverTestExtension.class)

@ExtendWith(PageObjectWebDriverCleanerExtension.class)

@TestTemplate
public @interface VaadinWebUnitTest {
}
