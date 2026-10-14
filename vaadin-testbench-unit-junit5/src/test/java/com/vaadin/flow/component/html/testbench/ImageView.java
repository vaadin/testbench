/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.html.testbench;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.router.Route;

@Tag("div")
@Route(value = "image", registerAtStartup = false)
public class ImageView extends Component implements HasComponents {

    Image image = new Image("logo.png", "Logo");

    int clicks = 0;

    public ImageView() {
        image.setTitle("Vaadin logo");
        image.addClickListener(event -> clicks++);
        add(image);
    }

}
