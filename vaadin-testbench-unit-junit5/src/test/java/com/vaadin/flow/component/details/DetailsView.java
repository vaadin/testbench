/**
 * Copyright (C) 2000-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.details;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.router.Route;

@Tag("div")
@Route(value = "details", registerAtStartup = false)
public class DetailsView extends Component implements HasComponents {

    Details details;
    DetailsContents contents;

    public DetailsView() {
        contents = new DetailsContents();
        details = new Details("Members", contents);
        add(details);
    }

    @Tag("div")
    public static class DetailsContents extends Component {

    }
}
