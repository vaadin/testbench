/**
 * Copyright (C) 2012 Vaadin Ltd
 * <p>
 * This program is available under Commercial Vaadin Add-On License 3.0
 * (CVALv3).
 * <p>
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 * <p>
 * You should have received a copy of the license along with this program.
 * If not, see <http://vaadin.com/license/cval-3>.
 */
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
