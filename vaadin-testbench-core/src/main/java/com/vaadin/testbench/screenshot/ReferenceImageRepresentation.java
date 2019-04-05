package com.vaadin.testbench.screenshot;

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
