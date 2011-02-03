package org.openqa.selenium.server;

/**
 * Additional TestBench commands are enumerated here.
 * 
 * @author Jonatan Kronqvist / Vaadin Ltd.
 */
public enum TestBenchCommand {
    /**
     * Defines the name of the test.
     */
    setTestName,

    /**
     * Takes a screen shot and compares it to the provided data. If shots
     * differing after N tries, the screen shot image is sent back to the caller
     * for more thorough comparison and/or error reporting.
     * 
     * Parameters (Strings): <br>
     * - A representation of reference image calculated as the average sum of
     * RGB values over 16x16 blocks <br>
     * - The tolerance for error (0..1) <br>
     * - The maximum number of retries <br>
     * - The X position of the canvas <br>
     * - The Y position of the canvas <br>
     * - The width of the canvas <br>
     * - The height of the canvas
     */
    compareScreen,

    /**
     * An empty command. Used instead of passing nulls here and there.
     */
    none;

    public static TestBenchCommand getValue(final String command) {
        try {
            return valueOf(command);
        } catch (Exception e) {
            return none;
        }
    }

}
