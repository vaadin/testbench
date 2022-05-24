/*
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
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
