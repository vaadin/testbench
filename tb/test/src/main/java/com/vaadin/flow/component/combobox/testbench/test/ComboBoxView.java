package com.vaadin.flow.component.combobox.testbench.test;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.common.testbench.test.AbstractView;
import com.vaadin.flow.component.common.testbench.test.Person;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

import java.util.stream.IntStream;

@Route(ComboBoxView.NAV)
@Theme(Lumo.class)
public class ComboBoxView extends AbstractView {

    public static final String TEXT = "text";
    public static final String LAZY = "lazy";
    public static final String NOTEXT = "notext";
    public static final String BEANS = "beans";
    public static final String TEXT_WITH_PRE_SLELECTED_VALUE = "text_with_preselected_value";
    public static final String NOTEXT_WITH_PRE_SLELECTED_VALUE = "notext_with_preselected_value";
    public static final String BEANS_WITH_PRE_SLELECTED_VALUE = "beans_with_preselected_value";
    public static final String LAZY_WITH_PRE_SLELECTED_VALUE = "lazy_with_preselected_value";
    public static final String PRE_SELECTED_VALUE_FOR_COMBOBOX_WITHOUT_TEXT = "Item 1";
    public static final String PRE_SELECTED_VALUE_FOR_COMBOBOX_WITH_TEXT = "Item 18";
    public static final String PRE_SELECTED_VALUE_FOR_COMBOBOX_LAZY = "Item 400";
    public static final Person PRE_SELECTED_PERSON_FOR_COMBOBOX_WITH_BEANS = new Person("John", "Doe", 20);
    public static final String NAV = "ComboBox";


    public ComboBoxView() {

        ComboBox<String> comboBoxWithoutText = new ComboBox<>();
        comboBoxWithoutText.setId(NOTEXT);
        comboBoxWithoutText
                .setItems(IntStream.range(0, 20).mapToObj(i -> "Item " + i));
        comboBoxWithoutText.addValueChangeListener(e -> {
            log("ComboBox '" + e.getSource().getLabel() + "' value is now "
                    + e.getValue());
        });
        add(comboBoxWithoutText);

        ComboBox<String> comboBoxWithText = new ComboBox<>("Text");
        comboBoxWithText.setId(TEXT);
        comboBoxWithText
                .setItems(IntStream.range(0, 20).mapToObj(i -> "Item " + i));
        comboBoxWithText.addValueChangeListener(e -> {
            log("ComboBox '" + e.getSource().getLabel() + "' value is now "
                    + e.getValue());
        });
        add(comboBoxWithText);

        ComboBox<String> comboBoxLazy = new ComboBox<>("Lazy");
        comboBoxLazy.setId(LAZY);
        comboBoxLazy
                .setItems(IntStream.range(0, 500).mapToObj(i -> "Item " + i));
        comboBoxLazy.addValueChangeListener(e -> {
            log("ComboBox '" + e.getSource().getLabel() + "' value is now "
                    + e.getValue());
        });
        add(comboBoxLazy);

        ComboBox<Person> comboBoxWithBean = new ComboBox<>("Persons");
        comboBoxWithBean.setId(BEANS);
        comboBoxWithBean.setItemLabelGenerator(
                item -> item.getLastName() + ", " + item.getFirstName());
        comboBoxWithBean.setItems(new Person("John", "Doe", 20),
                new Person("Jeff", "Johnson", 30),
                new Person("Diana", "Meyer", 40));
        comboBoxWithBean.addValueChangeListener(e -> {
            log("ComboBox '" + e.getSource().getLabel() + "' value is now "
                    + e.getValue());
        });
        add(comboBoxWithBean);

        ComboBox<String> comboBoxWithoutTextWithPreSelectedValue = new ComboBox<>();
        comboBoxWithoutTextWithPreSelectedValue.setId(NOTEXT_WITH_PRE_SLELECTED_VALUE);
        comboBoxWithoutTextWithPreSelectedValue
                .setItems(IntStream.range(0, 20).mapToObj(i -> "Item " + i));
        comboBoxWithoutTextWithPreSelectedValue.setValue(PRE_SELECTED_VALUE_FOR_COMBOBOX_WITHOUT_TEXT);
        add(comboBoxWithoutTextWithPreSelectedValue);

        ComboBox<String> comboBoxWithTextWithPreSelectedValue = new ComboBox<>("Text");
        comboBoxWithTextWithPreSelectedValue.setId(TEXT_WITH_PRE_SLELECTED_VALUE);
        comboBoxWithTextWithPreSelectedValue
                .setItems(IntStream.range(0, 20).mapToObj(i -> "Item " + i));
        comboBoxWithTextWithPreSelectedValue.setValue(ComboBoxView.PRE_SELECTED_VALUE_FOR_COMBOBOX_WITH_TEXT);
        add(comboBoxWithTextWithPreSelectedValue);

        ComboBox<String> comboBoxLazyWithPreSelectedValue = new ComboBox<>("Lazy");
        comboBoxLazyWithPreSelectedValue.setId(LAZY_WITH_PRE_SLELECTED_VALUE);
        comboBoxLazyWithPreSelectedValue
                .setItems(IntStream.range(0, 500).mapToObj(i -> "Item " + i));
        comboBoxLazyWithPreSelectedValue.setValue(ComboBoxView.PRE_SELECTED_VALUE_FOR_COMBOBOX_LAZY);
        add(comboBoxLazyWithPreSelectedValue);

        ComboBox<Person> comboBoxWithBeanWithPreSelectedValue = new ComboBox<>("Persons");
        comboBoxWithBeanWithPreSelectedValue.setId(BEANS_WITH_PRE_SLELECTED_VALUE);
        comboBoxWithBeanWithPreSelectedValue.setItemLabelGenerator(
                item -> item.getLastName() + ", " + item.getFirstName());
        comboBoxWithBeanWithPreSelectedValue.setItems(PRE_SELECTED_PERSON_FOR_COMBOBOX_WITH_BEANS,
                new Person("Jeff", "Johnson", 30),
                new Person("Diana", "Meyer", 40));
        comboBoxWithBeanWithPreSelectedValue.setValue(PRE_SELECTED_PERSON_FOR_COMBOBOX_WITH_BEANS);
        add(comboBoxWithBeanWithPreSelectedValue);
    }

}
