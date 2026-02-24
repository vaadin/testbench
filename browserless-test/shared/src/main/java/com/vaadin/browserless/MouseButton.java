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

/**
 * Enums for mouse button values in click events.
 * <p/>
 * Default button values are as follows:
 * <dl>
 * <dt>-1: No button</dt>
 * <dt>0: The primary button, typically the left mouse button</dt>
 * <dt>1: The middle button,</dt>
 * <dt>2: The secondary button, typically the right mouse button</dt>
 * <dt>3: The first additional button, typically the back button</dt>
 * <dt>4: The second additional button, typically the forward button</dt>
 * <dt>5+: More additional buttons without any typical meanings</dt>
 * </dl>
 */
public enum MouseButton {

    NO_BUTTON(-1), LEFT(0), MIDDLE(1), RIGHT(2), BACK(3), FORWARD(4);

    private int button;

    MouseButton(int button) {
        this.button = button;
    }

    /**
     * Get value associated with the button by default.
     *
     * @return button value
     */
    public int getButton() {
        return button;
    }
}
