package com.vaadin.flow.component.upload.testbench.test;

import com.vaadin.flow.component.common.testbench.test.AbstractView;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

import java.io.IOException;
import java.io.OutputStream;

@Route(UploadView.NAV)
@Theme(Lumo.class)
public class UploadView extends AbstractView {

    public static final String NAV = "Upload";
    public static final String UPLOAD = "upload";

    private final Upload upload = new Upload();

    public UploadView() {

        upload.setId(UPLOAD);

        upload.setReceiver((filename, mimetype) -> new OutputStream() {
            @Override
            public void write(int b) throws IOException {
            }
        });

        upload.addStartedListener(e -> log("Upload of " + e.getFileName()
                + " of size " + e.getContentLength()
                + " started"));

        upload.addSucceededListener(e -> log("File " + e.getFileName()
                + " of size " + e.getContentLength()
                + " received"));
        upload.addFailedListener(e -> log("File " + e.getFileName()
                + " of size " + e.getContentLength()
                + " failed"));

        add(upload);
    }

}
