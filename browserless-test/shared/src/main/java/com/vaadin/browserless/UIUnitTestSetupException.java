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

/**
 * Exception thrown by {@link BaseUIUnitTest} methods when the mock environment
 * has not been set up correctly.
 */
public class UIUnitTestSetupException extends RuntimeException {
    public UIUnitTestSetupException(String message) {
        super(message);
    }

    public UIUnitTestSetupException(String message, Throwable cause) {
        super(message, cause);
    }
}
