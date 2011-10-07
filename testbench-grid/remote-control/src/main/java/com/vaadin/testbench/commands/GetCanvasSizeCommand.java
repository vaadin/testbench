package com.vaadin.testbench.commands;

import org.apache.commons.logging.Log;
import org.mortbay.log.LogFactory;
import org.openqa.selenium.server.commands.Command;

import com.vaadin.testbench.util.BrowserDimensions;

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
