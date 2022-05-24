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

import com.vaadin.flow.di.DefaultInstantiator;
import com.vaadin.flow.di.Instantiator;
import com.vaadin.flow.di.InstantiatorFactory;
import com.vaadin.flow.server.VaadinService;

/**
 * A custom {@link InstantiatorFactory} to test
 * {@link com.vaadin.flow.di.Lookup} initialization.
 */
public class TestCustomInstantiatorFactory implements InstantiatorFactory {

    @Override
    public Instantiator createInstantitor(VaadinService vaadinService) {
        return new DefaultInstantiator(vaadinService);
    }
}
