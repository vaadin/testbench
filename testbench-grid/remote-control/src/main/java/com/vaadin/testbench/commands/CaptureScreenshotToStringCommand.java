/* 
@ITMillApache2LicenseForJavaFiles@
 */
package com.vaadin.testbench.commands;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.mortbay.log.LogFactory;

import com.vaadin.testbench.util.BrowserDimensions;
import com.vaadin.testbench.util.BrowserInfo;

/**
 * @author jonatan
 * 
 */
public class CaptureScreenshotToStringCommand {

    private static final Log LOGGER = LogFactory
            .getLog(CaptureScreenshotToStringCommand.class);
    private BrowserDimensions dim;

    /**
     * @param sessionId
     */
    public CaptureScreenshotToStringCommand(String sessionId) {
        String userAgent = CommandUtil.eval("navigator.userAgent;", sessionId);
        dim = BrowserInfo.getBrowserDimensions(userAgent);
    }

    public String execute() {
        try {
            return "OK," + captureAndEncodeSystemScreenshot();
        } catch (Exception e) {
            LOGGER.error("Problem capturing a screenshot to string", e);
            return "ERROR: Problem capturing a screenshot to string: "
                    + e.getMessage();
        }
    }

    public String captureAndEncodeSystemScreenshot()
            throws InterruptedException, ExecutionException, TimeoutException,
            IOException {
        final ByteArrayOutputStream outStream = new ByteArrayOutputStream();

        ImageIO.write(ScreenShot.capture(dim.getDisplayIndex()), "png",
                outStream);

        return new String(Base64.encodeBase64(outStream.toByteArray()));
    }

}
