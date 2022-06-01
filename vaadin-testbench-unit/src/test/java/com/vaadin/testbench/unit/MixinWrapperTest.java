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

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;

class MixinWrapperTest extends UIUnitTest implements WithAllWrappers {

    @Test
    void kind_getsCorrectWrapper() {

        Grid<Address> grid = new Grid<>(Address.class);
        grid.setItems(new Address("Udine"), new Address("Vicenza"));
        getCurrentView().getElement().appendChild(new Button().getElement(),
                new TextField().getElement(), new IntegerField().getElement(),
                new BigDecimalField().getElement(), grid.getElement());

        $(BUTTON).first().click();
        $(TEXTFIELD).first().setValue("xx");
        $(TEXTFIELD.as(IntegerField.class)).first().setValue(12);
        $(TEXTFIELD.as(BigDecimalField.class)).first()
                .setValue(new BigDecimal(10));
        $(GRID).first().select(1);
        $(GRID, Address.class).first().getSelected().iterator().next()
                .getCity();
    }

    static class Address {
        private String city;

        public Address(String city) {
            this.city = city;
        }

        String getCity() {
            return city;
        }
    }

}
