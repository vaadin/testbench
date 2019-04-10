package com.vaadin.testbench.commands;

/*-
 * #%L
 * vaadin-testbench-core
 * %%
 * Copyright (C) 2019 Vaadin Ltd
 * %%
 * This program is available under Commercial Vaadin Add-On License 3.0
 * (CVALv3).
 * 
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 * 
 * You should have received a copy of the license along with this program.
 * If not, see <http://vaadin.com/license/cval-3>.
 * #L%
 */

public interface TestBenchCommands extends CanCompareScreenshots {

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
     * This method provides performance information of the client-side rendering
     * for the last operation performed. You can use this information to make
     * sure that some operation is executed in a timely fashion.
     * <p>
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
     * <p>
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
     * <p>
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
     * <p>
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
     * <p>
     * Implicit waiting is enabled by default.
     */
    void disableWaitForVaadin();

    /**
     * Enables implicit waiting for Vaadin to finish processing requests.
     * <p>
     * Implicit waiting is enabled by default.
     */
    void enableWaitForVaadin();

    /**
     * Whether or not the elements should be scrolled into the visible area of
     * the browser window before interacting with them
     *
     * @return true if elements should be scrolled, otherwise false
     */
    boolean isAutoScrollIntoView();

    /**
     * Sets if the elements should be scrolled into the visible area of the
     * browser window before interacting with them
     *
     * @param autoScrollIntoView
     */
    void setAutoScrollIntoView(boolean autoScrollIntoView);

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
     * @param width  the desired width of the viewport
     * @param height the desired height of the viewport
     */
    void resizeViewPortTo(int width, int height)
            throws UnsupportedOperationException;
}
