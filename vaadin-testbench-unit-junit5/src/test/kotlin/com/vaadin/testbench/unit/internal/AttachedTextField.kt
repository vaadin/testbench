/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */

package com.vaadin.testbench.unit.internal

import com.vaadin.flow.component.textfield.TextField

/**
 * TextField for testing which returns that it is attached.
 */
class AttachedTextField : TextField() {
    override fun isAttached(): Boolean {
        return true
    }
}
