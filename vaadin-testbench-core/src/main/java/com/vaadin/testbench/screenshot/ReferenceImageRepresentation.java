/*
 * Vaadin TestBench Addon
 *
 * Copyright (C) 2012-2025 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
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

    private List<HashRepresentation> representations = new ArrayList<HashRepresentation>();

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
