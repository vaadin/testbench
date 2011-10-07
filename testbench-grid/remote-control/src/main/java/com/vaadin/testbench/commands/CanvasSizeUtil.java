package com.vaadin.testbench.commands;

import org.apache.commons.logging.Log;
import org.mortbay.log.LogFactory;

import com.vaadin.testbench.util.BrowserDimensions;
import com.vaadin.testbench.util.BrowserInfo;
import com.vaadin.testbench.util.BrowserVersion;

public class CanvasSizeUtil {
    private static final Log LOGGER = LogFactory.getLog(CanvasSizeUtil.class);

    public static BrowserDimensions getBrowserDimensions(String userAgent,
            String sessionId) throws Exception {
        BrowserVersion browser = new BrowserVersion(userAgent);
        // Firefox on OSX has a problem with moveTo(0,0)
        if (browser.isMac() && browser.isFirefox()) {
            CommandUtil.eval("window.moveTo(0,1);", sessionId);
        }

        BrowserDimensions dim = BrowserInfo.getBrowserDimensions(userAgent);
        int retries = 5;
        while (dim == null && retries-- >= 0) {

            dim = new BrowserDimensions(CommandUtil.eval(
                    "vaadin_testbench_getDimensions();", sessionId));

            dim.setDisplayIndex(CommandUtil.findPhysicalDisplay(sessionId));

            if (!browser.isIE()) {
                // Only IE provides canvas position. For the other browsers we
                // locate it based on a screenshot.
                CommandUtil.pause(200);
                CommandUtil.findCanvasPositionByScreenshot(dim);
            }

            if (windowOutsideScreen(dim)) {
                // Browser window position is either incorrectly detected or the
                // window is partly outside the view. Happens sometimes in
                // Firefox for unknown reasons.
                LOGGER.warn("Window position for "
                        + userAgent
                        + " detected as x: "
                        + dim.getCanvasXPosition()
                        + ", y: "
                        + dim.getCanvasYPosition()
                        + ", width: "
                        + dim.getCanvasWidth()
                        + ", height: "
                        + dim.getCanvasHeight()
                        + " but the screen size is "
                        + dim.getScreenWidth()
                        + "x"
                        + dim.getScreenHeight()
                        + ", which places the window outside the screen. Will retry "
                        + retries + " more times.");

                dim = null;
            } else {
                BrowserInfo.setBrowserDimensions(userAgent, dim);
            }
        }

        if (dim == null) {
            throw new CanvasNotFoundException(
                    "Could not find canvas position after five tries");
        }

        return dim;
    }

    private static boolean windowOutsideScreen(BrowserDimensions dim) {
        if (dim == null) {
            return true;
        }

        if (dim.getCanvasXPosition() + dim.getCanvasWidth() > dim
                .getScreenWidth()) {
            return true;
        }
        if (dim.getCanvasYPosition() + dim.getCanvasHeight() > dim
                .getScreenHeight()) {
            return true;
        }

        return false;
    }
}
