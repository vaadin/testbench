/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.laboratory.views.crudexample;

import org.springframework.data.domain.PageRequest;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.function.SerializableRunnable;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import com.vaadin.laboratory.data.SamplePerson;
import com.vaadin.laboratory.services.SamplePersonService;

class PersonGrid extends Div {

    private final Grid<SamplePerson> grid = new Grid<>(SamplePerson.class,
            false);

    PersonGrid(SamplePersonService samplePersonService,
            SerializableRunnable formClean) {
        setClassName("grid-wrapper");
        add(grid);

        grid.addColumn("firstName").setAutoWidth(true);
        grid.addColumn("lastName").setAutoWidth(true);
        grid.addColumn("email").setAutoWidth(true);
        grid.addColumn("phone").setAutoWidth(true);
        grid.addColumn("dateOfBirth").setAutoWidth(true);
        grid.addColumn("occupation").setAutoWidth(true);
        grid.addColumn("role").setAutoWidth(true);
        LitRenderer<SamplePerson> importantRenderer = LitRenderer
                .<SamplePerson> of(
                        "<vaadin-icon icon='vaadin:${item.icon}' style='width: var(--lumo-icon-size-s); height: var(--lumo-icon-size-s); color: ${item.color};'></vaadin-icon>")
                .withProperty("icon",
                        important -> important.isImportant() ? "check"
                                : "minus")
                .withProperty("color",
                        important -> important.isImportant()
                                ? "var(--lumo-primary-text-color)"
                                : "var(--lumo-disabled-text-color)");

        grid.addColumn(importantRenderer).setHeader("Important")
                .setAutoWidth(true);

        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // Configure Grid
        grid.setItems(
                query -> samplePersonService
                        .list(PageRequest
                                .of(query.getPage(), query.getPageSize(),
                                        VaadinSpringDataHelpers
                                                .toSpringDataSort(query)))
                        .stream());

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(
                        CrudExampleView.SAMPLEPERSON_EDIT_ROUTE_TEMPLATE,
                        event.getValue().getId()));
            } else {
                formClean.run();
                UI.getCurrent().navigate(CrudExampleView.class);
            }
        });
    }

    void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }
}
