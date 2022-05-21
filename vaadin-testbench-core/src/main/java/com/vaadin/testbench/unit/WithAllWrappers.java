/*
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */

package com.vaadin.testbench.unit;

import com.vaadin.flow.component.button.ButtonWrap;
import com.vaadin.flow.component.notification.NotificationWrap;
import com.vaadin.flow.component.textfield.TextFieldWrap;

/**
 * A mixin interface that brings in all known specific wrapper creation
 * functions.
 *
 * To be used with {@link UIUnitTest} to reduce the need for explicit casts when
 * getting test wrapper for component instances.
 */
public interface WithAllWrappers
        extends TextFieldWrap.Mixin, ButtonWrap.Mixin, NotificationWrap.Mixin {
}
