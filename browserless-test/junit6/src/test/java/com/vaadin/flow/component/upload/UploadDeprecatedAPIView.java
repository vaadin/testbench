/**
 * Copyright (C) 2000-2026 Vaadin Ltd
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
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.router.Route;

@Tag("div")
@Route(value = "upload", registerAtStartup = false)
@Deprecated
public class UploadDeprecatedAPIView extends Component
        implements HasComponents {

    MemoryBuffer receiver = new MemoryBuffer();
    MultiFileMemoryBuffer multiReceiver = new MultiFileMemoryBuffer();
    Upload uploadSingle;
    Upload uploadMulti;

    public UploadDeprecatedAPIView() {
        uploadSingle = new Upload(receiver);
        uploadMulti = new Upload(multiReceiver);

        add(uploadSingle, uploadMulti);
    }
}
