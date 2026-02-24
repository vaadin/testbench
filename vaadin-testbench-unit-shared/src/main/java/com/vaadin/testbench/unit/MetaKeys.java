/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.unit;

/**
 * Class for setting any down meta keys for events supporting meta keys.
 
  * @deprecated Replace the vaadin-testbench-unit dependency with browserless-test-junit6 and use the corresponding class from the com.vaadin.browserless package instead. This class will be removed in a future version.
  */
@Deprecated(forRemoval = true, since = "10.1")
public class MetaKeys {
    private boolean ctrl = false;
    private boolean shift = false;
    private boolean alt = false;
    private boolean meta = false;

    /**
     * Construct metaKeys with all set to false.
     */
    public MetaKeys() {
    }

    /**
     * Construct MetaKeys with given values for the keys.
     *
     * @param ctrl
     *            {@code true} for ctrl pressed
     * @param shift
     *            {@code true} for shift pressed
     * @param alt{@code true} for alt pressed
     * @param meta{@code true} for meta pressed
     */
    public MetaKeys(boolean ctrl, boolean shift, boolean alt, boolean meta) {
        this.ctrl = ctrl;
        this.shift = shift;
        this.alt = alt;
        this.meta = meta;
    }

    /**
     * Set ctrl key down to {@code true}.
     *
     * @return this instance
     */
    public MetaKeys ctrl() {
        return setCtrl(true);
    }

    /**
     * Set shift key down to {@code true}.
     *
     * @return this instance
     */
    public MetaKeys shift() {
        return setShift(true);

    }

    /**
     * Set alt key down to {@code true}.
     *
     * @return this instance
     */
    public MetaKeys alt() {
        return setAlt(true);

    }

    /**
     * Set meta key down to {@code true}.
     *
     * @return this instance
     */
    public MetaKeys meta() {
        return setMeta(true);

    }

    /**
     * Set ctrl key down state to given value.
     *
     * @param ctrl
     *            key down state
     * @return this instance
     */
    public MetaKeys setCtrl(boolean ctrl) {
        this.ctrl = ctrl;
        return this;
    }

    /**
     * Set shift key down state to given value.
     *
     * @param shift
     *            key down state
     * @return this instance
     */
    public MetaKeys setShift(boolean shift) {
        this.shift = shift;
        return this;
    }

    /**
     * Set alt key down state to given value.
     *
     * @param alt
     *            key down state
     * @return this instance
     */
    public MetaKeys setAlt(boolean alt) {
        this.alt = alt;
        return this;
    }

    /**
     * Set meta key down state to given value.
     *
     * @param meta
     *            key down state
     * @return this instance
     */
    public MetaKeys setMeta(boolean meta) {
        this.meta = meta;
        return this;
    }

    /**
     * Get ctrl key isPressed state.
     *
     * @return {@code ture} is pressed
     */
    public boolean isCtrl() {
        return ctrl;
    }

    /**
     * Get shift key isPressed state.
     *
     * @return {@code ture} is pressed
     */
    public boolean isShift() {
        return shift;
    }

    /**
     * Get alt key isPressed state.
     *
     * @return {@code ture} is pressed
     */
    public boolean isAlt() {
        return alt;
    }

    /**
     * Get meta key isPressed state.
     *
     * @return {@code ture} is pressed
     */
    public boolean isMeta() {
        return meta;
    }
}
