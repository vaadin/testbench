/*
 * Copyright (C) 2022 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Developer License
 * 4.0 (CVDLv4).
 *
 *
 * For the full License, see <https://vaadin.com/license/cvdl-4.0>.
 */
package com.vaadin.testbench.unit;

/**
 * Class for setting any down meta keys for events supporting meta keys.
 */
public class MetaKeys {
    private boolean ctrl = false;
    private boolean shift = false;
    private boolean alt = false;
    private boolean meta = false;

    public MetaKeys() {
    }

    public MetaKeys(boolean ctrl, boolean shift, boolean alt, boolean meta) {
        this.ctrl = ctrl;
        this.shift = shift;
        this.alt = alt;
        this.meta = meta;
    }

    public void setCtrl(boolean ctrl) {
        this.ctrl = ctrl;
    }

    public void setShift(boolean shift) {
        this.shift = shift;
    }

    public void setAlt(boolean alt) {
        this.alt = alt;
    }

    public void setMeta(boolean meta) {
        this.meta = meta;
    }

    public boolean isCtrl() {
        return ctrl;
    }

    public boolean isShift() {
        return shift;
    }

    public boolean isAlt() {
        return alt;
    }

    public boolean isMeta() {
        return meta;
    }
}
