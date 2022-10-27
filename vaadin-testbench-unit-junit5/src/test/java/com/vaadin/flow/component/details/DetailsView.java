/**
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
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
