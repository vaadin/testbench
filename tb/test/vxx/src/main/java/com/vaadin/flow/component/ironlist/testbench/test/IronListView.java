package com.vaadin.flow.component.ironlist.testbench.test;

import com.vaadin.flow.component.common.testbench.test.AbstractView;
import com.vaadin.flow.component.common.testbench.test.DataGenerator;
import com.vaadin.flow.component.common.testbench.test.Person;
import com.vaadin.flow.component.ironlist.IronList;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

@Route(IronListView.NAV)
@Theme(Lumo.class)
public class IronListView extends AbstractView {

  public static final String HUNDRED_THOUSAND = "default";
  public static final String NAV = "IronList";

  public IronListView() {
    IronList<Person> ironList = new IronList<>();
    ironList.setRenderer(person -> person.getFirstName() + " "
                                   + person.getLastName() + " (" + person.getAge() + ")");
    ironList.setWidth("500px");
    ironList.setHeight("400px");
    ironList.setId(HUNDRED_THOUSAND);
    ironList.setDataProvider(new DataGenerator(100000));
    add(ironList);
  }

}
