/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.laboratory.views.crudexample;

import java.util.Optional;

import org.vaadin.lineawesome.LineAwesomeIconUrl;

import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.laboratory.data.SamplePerson;
import com.vaadin.laboratory.services.SamplePersonService;

@PageTitle("Crud Example")
@Route(value = "crud-example/:samplePersonID?/:action?(edit)")
@Menu(order = 1, icon = LineAwesomeIconUrl.COLUMNS_SOLID)
@Uses(Icon.class)
public class CrudExampleView extends Div implements BeforeEnterObserver {

    static final String SAMPLEPERSON_ID = "samplePersonID";
    static final String SAMPLEPERSON_EDIT_ROUTE_TEMPLATE = "crud-example/%s/edit";

    private final PersonGrid grid;
    private final SamplePersonService samplePersonService;
    private final PersonForm form;

    public CrudExampleView(CrudExampleFactory crudExampleFactory) {
        addClassNames("crud-example-view");

        samplePersonService = crudExampleFactory.createService();
        grid = crudExampleFactory.createGrid(this::clearForm);
        form = crudExampleFactory.createForm(this::refreshGrid);

        // Create UI
        SplitLayout splitLayout = new SplitLayout();
        splitLayout.addToPrimary(grid);
        splitLayout.addToSecondary(form);

        add(splitLayout);
    }

    private void refreshGrid() {
        grid.refreshGrid();
    }

    private void clearForm() {
        form.clearForm();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> samplePersonId = event.getRouteParameters()
                .get(SAMPLEPERSON_ID).map(Long::parseLong);
        if (samplePersonId.isPresent()) {
            Optional<SamplePerson> samplePersonFromBackend = samplePersonService
                    .get(samplePersonId.get());
            if (samplePersonFromBackend.isPresent()) {
                form.populateForm(samplePersonFromBackend.get());
            } else {
                Notification.show(String.format(
                        "The requested samplePerson was not found, ID = %s",
                        samplePersonId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                this.refreshGrid();
                event.forwardTo(CrudExampleView.class);
            }
        }
    }
}
