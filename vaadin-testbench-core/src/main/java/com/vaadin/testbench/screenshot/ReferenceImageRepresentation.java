/**
 * Copyright (C) 2020 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.testbench.screenshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * ReferenceImageRepresentation contains a block representation of a reference
 * image. This is used for comparing the image to a screen shot and detecting
 * changes in the appearance.
 */
public class ReferenceImageRepresentation implements Serializable {
    private static final long serialVersionUID = 1L;

    public class HashRepresentation implements Serializable {
        private static final long serialVersionUID = 1L;
        private String hash;

        public HashRepresentation(String hash) {
            setHash(hash);
        }

        public String getHash() {
            return hash;
        }

        public void setHash(String hash) {
            this.hash = hash;
        }

    }

    private List<HashRepresentation> representations = new ArrayList<>();

    public ReferenceImageRepresentation() {

    }

    public void addRepresentation(String hash) {
        representations.add(new HashRepresentation(hash));
    }

    public void addRepresentation(HashRepresentation rep) {
        representations.add(rep);
    }

    public Iterable<HashRepresentation> getRepresentations() {
        return representations;
    }
}
