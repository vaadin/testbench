package com.vaadin.testbench.util;

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

    public class BlockRepresentation implements Serializable {
        private int[] blocks;

        public BlockRepresentation(int[] blocks) {
            setBlocks(blocks);
        }

        public void setBlocks(int[] blocks) {
            this.blocks = blocks;
        }

        public int[] getBlocks() {
            return blocks;
        }
    }

    private List<BlockRepresentation> representations = new ArrayList<BlockRepresentation>();

    public ReferenceImageRepresentation() {

    }

    public void addRepresentation(int[] blocks) {
        representations.add(new BlockRepresentation(blocks));
    }

    public void addRepresentation(BlockRepresentation rep) {
        representations.add(rep);
    }

    public Iterable<BlockRepresentation> getRepresentations() {
        return representations;
    }
}
