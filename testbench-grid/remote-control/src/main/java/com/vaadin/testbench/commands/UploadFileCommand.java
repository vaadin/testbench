package com.vaadin.testbench.commands;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.mortbay.log.LogFactory;
import org.openqa.selenium.server.FrameGroupCommandQueueSet;
import org.openqa.selenium.server.commands.Command;

/**
 * Sends a file that is to be passed to an Upload input field.
 * 
 * Parameters:<br>
 * - The fieldLocator (locator of the upload input field)<br>
 * - The name of the file<br>
 * - The base64 encoded file data
 * 
 * @author Jonatan Kronqvist / Vaadin
 */
public class UploadFileCommand extends Command {
    private static final Log LOGGER = LogFactory
            .getLog(UploadFileCommand.class);

    private String fieldLocator;
    private String filename;
    private File file;

    private String sessionId;

    public UploadFileCommand(Vector<String> parameters, String sessionId) {
        fieldLocator = parameters.get(0);
        filename = parameters.get(1);
        file = createTemporaryFile(parameters.get(2));
        this.sessionId = sessionId;
    }

    private File createTemporaryFile(String fileData) {
        try {
            File tempFile = File.createTempFile(new File(filename).getName(),
                    null);
            BufferedOutputStream out = new BufferedOutputStream(
                    new FileOutputStream(tempFile));
            out.write(Base64.decodeBase64(fileData.getBytes()));
            out.close();
            return tempFile;
        } catch (IOException e) {
            LOGGER.error("Problem creating a temporary file for uploading", e);
            return null;
        }
    }

    public File getTemporaryFile() {
        return file;
    }

    @Override
    public String execute() {
        String results;
        FrameGroupCommandQueueSet queue = FrameGroupCommandQueueSet
                .getQueueSet(sessionId);
        try {
            results = queue.doCommand("type", fieldLocator,
                    file.getAbsolutePath());
        } catch (Exception e) {
            results = e.toString();
        }
        return results;
    }

}
