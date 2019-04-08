package com.vaadin.testbench.commands;

/*-
 * #%L
 * vaadin-testbench-core
 * %%
 * Copyright (C) 2019 Vaadin Ltd
 * %%
 * Copyright (C) ${year} Vaadin Ltd
 * 
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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public interface CanCompareScreenshots {

    /**
     * Tests that a screen shot is equal to the specified reference image. The
     * comparison tolerance can be specified by setting the
     * com.testbench.testbench.block.error system property to a value between 0 and
     * 1, where 0 == no changes are accepted and 1 == big changes are accepted.
     * Note that specifying 1 doesn't mean that any reference image is accepted.
     *
     * @param referenceId the ID of the reference image
     * @return true if the screenshot is considered equal to the reference
     * image, false otherwise.
     * @throws IOException if there was a problem accessing the reference image
     */
    boolean compareScreen(String referenceId) throws IOException;

    /**
     * Tests that a screen shot is equal to the specified reference image. The
     * comparison tolerance can be specified by setting the
     * com.testbench.testbench.block.error system property to a value between 0 and
     * 1, where 0 == no changes are accepted and 1 == all changes are accepted.
     *
     * @param reference the reference image file
     * @return true if the screenshot is considered equal to the reference
     * image, false otherwise.
     * @throws IOException if there was a problem accessing the reference image
     */
    boolean compareScreen(File reference) throws IOException;

    /**
     * Tests that a screen shot is equal to the specified reference image. The
     * comparison tolerance can be specified by setting the
     * com.testbench.testbench.block.error system property to a value between 0 and
     * 1, where 0 == no changes are accepted and 1 == all changes are accepted.
     *
     * @param reference     the reference image
     * @param referenceName the filename of the reference image. Used when writing the
     *                      error files.
     * @return true if the screenshot is considered equal to the reference
     * image, false otherwise.
     * @throws IOException if there was a problem accessing the reference image
     */
    boolean compareScreen(BufferedImage reference, String referenceName)
            throws IOException;
}
