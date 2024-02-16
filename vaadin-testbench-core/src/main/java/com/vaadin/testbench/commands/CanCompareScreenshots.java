/*
 * Copyright (C) 2000-2024 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.commands;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public interface CanCompareScreenshots {

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

}
