package com.vaadin.testUI;

import com.vaadin.flow.component.HasLabel;
import com.vaadin.flow.component.HasPlaceholder;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.littemplate.LitTemplate;

@Tag(CaptionComponent.TAG)
@JsModule("./caption-component.ts")
public class CaptionComponent extends LitTemplate
        implements HasLabel, HasPlaceholder {
    public static final String TAG = "caption-component";

    public CaptionComponent() {
        //
    }
}
