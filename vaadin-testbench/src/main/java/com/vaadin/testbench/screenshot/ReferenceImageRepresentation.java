/**
 * Copyright (C) 2012 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Add-On License 2.0
 * (CVALv2) or GNU Affero General Public License (version 3 or later at
 * your option).
 *
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 *
 * You should have received a copy of the license along with this program.
 * If not, see <http://vaadin.com/license/cval-2.0> or
 * <http://www.gnu.org/licenses> respectively.
 */
package com.vaadin.testbench.screenshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * ReferenceImageRepresentation contains a block representation of a reference
 * image. This is used for comparing the image to a screen shot and detecting
 * changes in the appearance.
 * 
 * @author Jonatan Kronqvist / Vaadin
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
