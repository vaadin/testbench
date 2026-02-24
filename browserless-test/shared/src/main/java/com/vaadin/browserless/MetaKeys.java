/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
