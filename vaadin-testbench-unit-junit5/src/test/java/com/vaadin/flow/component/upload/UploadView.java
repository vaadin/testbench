/**
 * Copyright (C) 2000-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.flow.component.upload;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.router.Route;

@Tag("div")
@Route(value = "upload", registerAtStartup = false)
public class UploadView extends Component implements HasComponents {

    final Upload uploadSingle;
    final Upload uploadMulti;

    public UploadView() {
        uploadSingle = new Upload();
        uploadMulti = new Upload();

        add(uploadSingle, uploadMulti);
    }

}
