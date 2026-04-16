/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.laboratory.views.crudexample;

import org.springframework.stereotype.Component;

import com.vaadin.flow.function.SerializableRunnable;
import com.vaadin.laboratory.services.SamplePersonService;

@Component
class CrudExampleFactory {

    private final SamplePersonService samplePersonService;

    public CrudExampleFactory(SamplePersonService samplePersonService) {
        this.samplePersonService = samplePersonService;
    }

    PersonForm createForm(SerializableRunnable refreshGridRunnable) {
        return new PersonForm(samplePersonService, refreshGridRunnable);
    }

    SamplePersonService createService() {
        return samplePersonService;
    }

    public PersonGrid createGrid(SerializableRunnable clearForm) {
        return new PersonGrid(samplePersonService, clearForm);
    }
}
