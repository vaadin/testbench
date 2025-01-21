/**
 * Copyright (C) 2000-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testUI;

import com.vaadin.flow.component.HasLabel;
import com.vaadin.flow.component.HasPlaceholder;
import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.littemplate.LitTemplate;

@Tag(LabelPlaceholder.TAG)
@JsModule(LabelPlaceholder.JS_MODULE)
public class LabelPlaceholder extends LitTemplate
        implements HasLabel, HasPlaceholder, HasText {
    public static final String TAG = "label-placeholder";
    public static final String JS_MODULE = "./" + TAG + ".ts";

    public LabelPlaceholder() {
        //
    }
}
