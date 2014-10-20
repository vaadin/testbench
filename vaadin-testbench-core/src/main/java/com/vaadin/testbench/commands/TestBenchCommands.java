/**
 * Copyright (C) 2012 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Add-On License 3.0
 * (CVALv3).
 *
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 *
 * You should have received a copy of the license along with this program.
 * If not, see <http://vaadin.com/license/cval-3>.
 */
package com.vaadin.testbench.commands;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public interface TestBenchCommands extends CanWaitForVaadin {

    /**
     * Finds the canonical host name of the remotely executing node where the
     * test is being run. This is useful for failure reporting when running on
     * large grids where one machine may start having problems and you need to
     * find out which of the machines it is.
     *
     * @return the canonical host name along with it's IP as a string.
     */
    String getRemoteControlName();

    /**
     * Tests that a screen shot is equal to the specified reference image. The
     * comparison tolerance can be specified by setting the
     * com.vaadin.testbench.block.error system property to a value between 0 and
     * 1, where 0 == no changes are accepted and 1 == big changes are accepted.
     * Note that specifying 1 doesn't mean that any reference image is accepted.
     *
     * @param referenceId
     *            the ID of the reference image
     * @return true if the screenshot is considered equal to the reference
     *         image, false otherwise.
     * @throws IOException
     *             if there was a problem accessing the reference image
     */
    boolean compareScreen(String referenceId) throws IOException;

    /**
     * Tests that a screen shot is equal to the specified reference image. The
     * comparison tolerance can be specified by setting the
     * com.vaadin.testbench.block.error system property to a value between 0 and
     * 1, where 0 == no changes are accepted and 1 == all changes are accepted.
     *
     * @param reference
     *            the reference image file
     * @return true if the screenshot is considered equal to the reference
     *         image, false otherwise.
     * @throws IOException
     *             if there was a problem accessing the reference image
     */
    boolean compareScreen(File reference) throws IOException;

    /**
     * Tests that a screen shot is equal to the specified reference image. The
     * comparison tolerance can be specified by setting the
     * com.vaadin.testbench.block.error system property to a value between 0 and
     * 1, where 0 == no changes are accepted and 1 == all changes are accepted.
     *
     * @param reference
     *            the reference image
     * @param referenceName
     *            the filename of the reference image. Used when writing the
     *            error files.
     * @return true if the screenshot is considered equal to the reference
     *         image, false otherwise.
     * @throws IOException
     *             if there was a problem accessing the reference image
     */
    boolean compareScreen(BufferedImage reference, String referenceName)
            throws IOException;

    /**
     * This method provides performance information of the client-side rendering
     * for the last operation performed. You can use this information to make
     * sure that some operation is executed in a timely fashion.
     *
     * If more than one application is running at the same URL, e.g. a portal
     * environment with many portlets on the same page, the value returned will
     * be the sum for all applications.
     *
     * <em>Note!</em> This method needs to be called before
     * {@link #timeSpentRenderingLastRequest()} or
     * {@link #totalTimeSpentServicingRequests()}, since they will perform an
     * extra request, causing the value returned from this method to be that for
     * an empty request/response.
     *
     * @return the time spent rendering the last request.
     */
    long timeSpentRenderingLastRequest();

    /**
     * This method provides performance information of the client-side rendering
     * for the entire session. The session starts when you navigate to an
     * application and this method returns the amount of time spent rendering up
     * to the point of the call.
     *
     * If more than one application is running at the same URL, e.g. a portal
     * environment with many portlets on the same page, the value returned will
     * be the sum for all applications.
     *
     * @return the total time spent rendering in this session.
     */
    long totalTimeSpentRendering();

    /**
     * This method provides performance information of the server-side
     * processing for the last request. You can use this information to ensure
     * that an operation is processed in a timely fashion.
     *
     * If more than one application is running at the same URL, e.g. a portal
     * environment with many portlets on the same page, the value returned will
     * be the sum for all applications.
     *
     * <em>Note!</em> If you are interested in the client-side performance for
     * the last request, you must call {@link #timeSpentRenderingLastRequest()}
     * before calling this method. This is due to the fact that this method
     * causes an extra server round-trip, which will cause an empty response to
     * be rendered.
     *
     * @return the time spent servicing the last request on the server.
     */
    long timeSpentServicingLastRequest();

    /**
     * This method provides performance information of the server-side
     * processing for the entire session. The session starts when you navigate
     * to an application and this method returns the amount of time spent
     * processing requests up to the point of the call.
     *
     * If more than one application is running at the same URL, e.g. a portal
     * environment with many portlets on the same page, the value returned will
     * be the sum for all applications.
     *
     * <em>Note!</em> If you are interested in the client-side performance for
     * the last request, you must call {@link #timeSpentRenderingLastRequest()}
     * before calling this method. This is due to the fact that this method
     * causes an extra server round-trip, which will cause an empty response to
     * be rendered.
     *
     * @return the total time spent servicing requests in this session.
     */
    long totalTimeSpentServicingRequests();

    /**
     * Disables implicit waiting for Vaadin to finish processing requests. This
     * is useful if you need to test bombarding an application with events.
     *
     * Implicit waiting is enabled by default.
     */
    void disableWaitForVaadin();

    /**
     * Enables implicit waiting for Vaadin to finish processing requests.
     *
     * Implicit waiting is enabled by default.
     */
    void enableWaitForVaadin();

    /**
     * Tries to resize the browsers window so that the space available for
     * actual web content (aka viewport) is of given size.
     * <p>
     * Note, that the result cannot be guaranteed on all platforms. For example
     * browsers in mobile devices are most often always fullscreen and their
     * viewport can be "simultated". Also browsers might not allow resizing the
     * window or limit size of window to minimum or maximum (often limited by
     * screen size). Currently most common desktop browsers support this.
     * <p>
     *
     * @param width
     *            the desired width of the viewport
     * @param height
     *            the desired height of the viewport
     */
    void resizeViewPortTo(int width, int height)
            throws UnsupportedOperationException;
}
