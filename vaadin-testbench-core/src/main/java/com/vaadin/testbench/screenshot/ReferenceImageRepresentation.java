package com.vaadin.testbench.screenshot;

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

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

/**
 * ReferenceImageRepresentation contains a block representation of a reference
 * image. This is used for comparing the image to a screen shot and detecting
 * changes in the appearance.
 */
@Deprecated
public class ReferenceImageRepresentation implements Serializable {
    private static final long serialVersionUID = 1L;

    private String hash;

    private Set<String> hashValues = new HashSet<>();

    public void addRepresentation(String hash) {
        hashValues.add(hash);
    }

    public Stream<String> getRepresentations() {
        return hashValues.stream();
    }
}
