package com.vaadin.testbench.commands;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.apache.commons.logging.Log;
import org.mortbay.log.LogFactory;
import org.openqa.selenium.server.commands.Command;

import com.vaadin.testbench.util.BrowserDimensions;
import com.vaadin.testbench.util.BrowserInfo;
import com.vaadin.testbench.util.BrowserVersion;

/**
 * Fetches the current canvas size from cache if possible, otherwise it measures
 * the canvas size and returns the result.
 * 
 * Returns: OK,screenWidth,screenHeight,canvasWidth,canvasHeight,canvasX,canvasY
 * 
 * @author Jonatan Kronqvist / Vaadin
 */
public class GetCanvasSizeCommand extends Command {
    private static final Log LOGGER = LogFactory
            .getLog(GetCanvasSizeCommand.class);

    private final String sessionId;

    public GetCanvasSizeCommand(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public String execute() {
        String userAgent = CommandUtil.eval("navigator.userAgent;", sessionId);
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
