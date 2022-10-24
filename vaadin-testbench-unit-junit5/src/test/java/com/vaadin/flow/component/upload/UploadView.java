/**
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.flow.component.upload;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.router.Route;

@Tag("div")
@Route(value = "upload", registerAtStartup = false)
public class UploadView extends Component implements HasComponents {

    MemoryBuffer receiver = new MemoryBuffer();
    MultiFileMemoryBuffer multiReceiver = new MultiFileMemoryBuffer();
    Upload uploadSingle;
    Upload uploadMulti;

    public UploadView() {
        uploadSingle = new Upload(receiver);
        uploadMulti = new Upload(multiReceiver);

        add(uploadSingle, uploadMulti);
    }
}
