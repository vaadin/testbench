package com.vaadin.flow.component.grid.testbench.test;

import java.util.stream.Collectors;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.common.testbench.test.AbstractView;
import com.vaadin.flow.component.common.testbench.test.DataGenerator;
import com.vaadin.flow.component.common.testbench.test.Person;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.selection.SelectionEvent;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

@Route(GridView.NAV)
@Theme(Lumo.class)
public class GridView extends AbstractView {

  public static final String NO_HEADER = "noheader";
  public static final String HEADER_MULTISELECT = "header";
  public static final String TEN_MILLION = "tenmillion";
  public static final String COMPONENTS = "components";
  public static final String DETAILS = "details";
  public static final String NAV = "Grid";

  public GridView() {
    Grid<Person> gridNoHeader = new Grid<>();
    gridNoHeader.setId(NO_HEADER);
    gridNoHeader.addColumn(person -> person.getFirstName());
    gridNoHeader.addColumn(person -> person.getLastName());
    gridNoHeader.addSelectionListener(e -> {
      log("Grid 'noheader' selection changed to '" + getSelection(e)
          + "'");
    });
    gridNoHeader.setDataProvider(new DataGenerator(1000));

    Grid<Person> gridHeader = new Grid<>();
    gridHeader.setSelectionMode(SelectionMode.MULTI);
    gridHeader.setId(HEADER_MULTISELECT);
    Column<Person> firstName = gridHeader
        .addColumn(person -> person.getFirstName())
        .setHeader("First name");
    Column<Person> lastName = gridHeader
        .addColumn(person -> person.getLastName())
        .setHeader("Last name");
    gridHeader.addColumn(person -> person.getAge()).setHeader("Age");

    HeaderRow headerRow = gridHeader.prependHeaderRow();
    headerRow.join(headerRow.getCell(firstName) , headerRow.getCell(lastName))
             .setText("Name");

    gridHeader.addSelectionListener(e -> {
      log("Grid 'header' selection changed to '" + getSelection(e) + "'");
    });
    gridHeader.setDataProvider(new DataGenerator(100));

    Grid<Person> tenMillionGrid = new Grid<>();
    tenMillionGrid.setId(TEN_MILLION);
    firstName = tenMillionGrid.addColumn(person -> person.getFirstName())
                              .setHeader("First name").setFooter("First Footer");
    lastName = tenMillionGrid.addColumn(person -> person.getLastName())
                             .setHeader("Last name").setFooter("Last Footer");

    HeaderRow tenMillionHeaderRow = tenMillionGrid.prependHeaderRow();
    tenMillionHeaderRow.join(tenMillionHeaderRow.getCell(firstName) ,
                             tenMillionHeaderRow.getCell(lastName)).setText("Name");

    tenMillionGrid.addSelectionListener(e -> {
      log("Grid 'tenmillion' selection changed to '" + getSelection(e)
          + "'");
    });
    tenMillionGrid.setDataProvider(new DataGenerator(10000000));

    Grid<Person> componentGrid = new Grid<>();
    componentGrid.setId(COMPONENTS);
    componentGrid
        .addColumn(new ComponentRenderer<Button, Person>(person -> {
          return new Button(person.getFirstName() , e -> {
            log("Click on button '" + e.getSource().getText()
                + "'");
          });
        }));
    componentGrid.addSelectionListener(e -> {
      log("Grid 'componentGrid' selection changed to '" + getSelection(e)
          + "'");
    });
    componentGrid.setDataProvider(new DataGenerator(10000));

    Grid<Person> detailsGrid = new Grid<>();
    detailsGrid.setId(DETAILS);
    detailsGrid.addColumn(person -> person.getFirstName());
    detailsGrid.addColumn(person -> person.getLastName());
    detailsGrid.setItemDetailsRenderer(
        new ComponentRenderer<Component, Person>(
            person -> createDetails(person)));
    detailsGrid.setDataProvider(new DataGenerator(10000));

    add(new HorizontalLayout(gridHeader , gridNoHeader , tenMillionGrid ,
                             componentGrid , detailsGrid));
  }

  private Component createDetails(Person p) {
    Button button = new Button("Say hello" , e -> log("Hello " + p));
    return new VerticalLayout(new Span(p.getFirstName()) ,
                              new Span(p.getLastName()) , button);
  }

  private String getSelection(SelectionEvent<Grid<Person>, Person> e) {
    return e.getAllSelectedItems().stream().map(Person::toString)
            .collect(Collectors.joining(", "));
  }

}
