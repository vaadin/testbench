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

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.vaadin.flow.component.textfield.IntegerField;

public class MixinWrapperTest extends UIUnitTest implements WithAllWrappers {

    @Test
    void kind_getsCorrectWrapper() {
        $(BUTTON).first().click();
        $(TEXTFIELD).first().setValue("xx");
        $(TEXTFIELD.as(IntegerField.class)).first().setValue(12);
        $(TEXTFIELD, BigDecimal.class).first().setValue(new BigDecimal(10));
        $(GRID).first().select(1);
        $(GRID, Address.class).first().getSelected().iterator().next().city();
    }

    static class Address {
        String city() {
            return null;
        }
    }

}
