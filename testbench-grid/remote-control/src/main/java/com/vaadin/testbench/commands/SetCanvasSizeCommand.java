package com.vaadin.testbench.commands;

import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.apache.commons.logging.Log;
import org.mortbay.log.LogFactory;
import org.openqa.selenium.server.commands.Command;

import com.vaadin.testbench.util.BrowserDimensions;
import com.vaadin.testbench.util.BrowserInfo;
import com.vaadin.testbench.util.BrowserVersion;

/**
 * Initializes the browser to a certain canvas size and returns the coordinates
 * and the size.
 * 
 * Parameters:<br>
 * - canvas width<br>
 * - canvas height
 * 
 * Returns: OK,screenWidth,screenHeight,canvasWidth,canvasHeight,canvasX,canvasY
 */
public class SetCanvasSizeCommand extends Command {

    private static final Log LOGGER = LogFactory
            .getLog(SetCanvasSizeCommand.class);

    private final int wantedWidth;
    private final int wantedHeight;
    private final String sessionId;

    public SetCanvasSizeCommand(Vector<String> params, String sessionId) {
        wantedWidth = Integer.valueOf(params.get(0));
        wantedHeight = Integer.valueOf(params.get(1));
        this.sessionId = sessionId;
    }

    @Override
    public String execute() {
        String userAgent = CommandUtil.eval("navigator.userAgent;", sessionId);
        if (BrowserInfo.getOuterWidth(userAgent, wantedWidth, wantedHeight) > -1) {
            CommandUtil.eval(String.format(
                    "vaadin_testbench_setWindowSize(%d, %d);",
                    BrowserInfo.getOuterWidth(userAgent, wantedWidth,
                            wantedHeight), BrowserInfo.getOuterHeight(
                            userAgent, wantedWidth, wantedHeight)), sessionId);
        } else {
            String[] outerDim = CommandUtil
                    .eval(String.format(
                            "vaadin_testbench_calculateAndSetCanvasSize(%d, %d);",
                            wantedWidth, wantedHeight), sessionId).split(",");
            int offs = 0;
            if (outerDim.length == 3) {
                offs = 1;
            }
            int width = Integer.valueOf(outerDim[0 + offs]);
            int height = Integer.valueOf(outerDim[1 + offs]);
            BrowserInfo.setOuterDimensions(userAgent, wantedWidth,
                    wantedHeight, width, height);
        }

        BrowserVersion browser = new BrowserVersion(userAgent);
        // Firefox on OSX has a problem with moveTo(0,0)
        if (browser.isMac() && browser.isFirefox()) {
            CommandUtil.eval("window.moveTo(0,1);", sessionId);
        }

        BrowserDimensions dim = BrowserInfo.getBrowserDimensions(userAgent);
        if (dim == null) {
            dim = new BrowserDimensions(CommandUtil.eval(
                    "vaadin_testbench_getDimensions();", sessionId));

            if (!browser.isIE()) {
                // Only IE provides canvas position. For the other browsers we
                // locate it based on a screenshot.
                CommandUtil.pause(200);
                try {
                    CommandUtil.findCanvasPositionByScreenshot(dim);
                } catch (InterruptedException e) {
                    LOGGER.error("Problem grabbing screen shot", e);
                    return "ERROR: Problem grabbing screen shot";
                } catch (ExecutionException e) {
                    LOGGER.error("Problem grabbing screen shot", e);
                    return "ERROR: Problem grabbing screen shot";
                } catch (TimeoutException e) {
                    LOGGER.error("Problem grabbing screen shot", e);
                    return "ERROR: Problem grabbing screen shot";
                }
            }

            BrowserInfo.setBrowserDimensions(userAgent, dim);
        }

        return "OK," + dim.getDimensionsString();
    }
}
