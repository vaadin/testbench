package com.vaadin.testbench.commands;

import java.util.Vector;

import org.apache.commons.logging.Log;
import org.openqa.jetty.log.LogFactory;
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
        BrowserVersion browser = new BrowserVersion(userAgent);

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

            // Chrome is unstable when resizing the window
            if (browser.isChrome()) {
                CommandUtil.pause(2000);
                outerDim = CommandUtil
                        .eval(String.format(
                                "vaadin_testbench_calculateAndSetCanvasSize(%d, %d);",
                                wantedWidth, wantedHeight), sessionId).split(
                                ",");
            }

            int offs = 0;
            if (outerDim.length == 3) {
                offs = 1;
            }
            int width = Integer.valueOf(outerDim[0 + offs]);
            int height = Integer.valueOf(outerDim[1 + offs]);
            BrowserInfo.setOuterDimensions(userAgent, wantedWidth,
                    wantedHeight, width, height);
        }

        try {
            BrowserDimensions dim = CanvasSizeUtil.getBrowserDimensions(
                    userAgent, sessionId);
            return "OK," + dim.getDimensionsString();
        } catch (Exception e) {
            LOGGER.error("Unable to detect browser dimensions", e);
            return "ERROR: Unable to detect browser dimensions. See TestBench RC console for more details.";
        }
    }
}
