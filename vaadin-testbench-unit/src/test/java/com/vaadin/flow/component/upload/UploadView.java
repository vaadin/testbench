/**
 * Copyright (C) 2000-${year} Vaadin Ltd
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
public class UploadView extends Component implements HasComponents {

    MemoryBuffer receiver = new MemoryBuffer();
    MultiFileMemoryBuffer multiReceiver = new MultiFileMemoryBuffer();
    MemoryBuffer subclassReceiver = new MemoryBuffer();
    Upload uploadSingle;
    Upload uploadMulti;
    // an application's own subclass of Upload
    Upload uploadSubclass;

    public UploadView() {
        uploadSingle = new Upload(receiver);
        uploadMulti = new Upload(multiReceiver);
        uploadSubclass = new AttachmentUpload(subclassReceiver);

        add(uploadSingle, uploadMulti, uploadSubclass);
    }

    public static class AttachmentUpload extends Upload {
        public AttachmentUpload(MemoryBuffer receiver) {
            super(receiver);
        }
    }
}
