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

import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.accordion.AccordionTester;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonTester;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupTester;
import com.vaadin.flow.component.checkbox.CheckboxTester;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.ComboBoxTester;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.confirmdialog.ConfirmDialogTester;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.contextmenu.ContextMenuTester;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datepicker.DatePickerTester;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePickerTester;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.details.DetailsTester;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.dialog.DialogTester;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridTester;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.listbox.ListBoxTester;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.listbox.MultiSelectListBoxTester;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginFormTester;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.login.LoginOverlayTester;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.messages.MessageInputTester;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListTester;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationTester;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioButtonGroupTester;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.select.SelectTester;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsTester;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.NumberFieldTester;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextAreaTester;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldTester;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.component.timepicker.TimePickerTester;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.UploadTester;

public interface TesterWrappers {

    default AccordionTester<Accordion> test(Accordion accordion) {
        return BaseUIUnitTest.internalWrap(AccordionTester.class, accordion);
    }

    default ButtonTester<Button> test(Button button) {
        return BaseUIUnitTest.internalWrap(ButtonTester.class, button);
    }

    default CheckboxTester<Checkbox> test(Checkbox checkbox) {
        return BaseUIUnitTest.internalWrap(CheckboxTester.class, checkbox);
    }

    default <V> CheckboxGroupTester<CheckboxGroup<V>, V> test(
            CheckboxGroup<V> checkboxGroup) {
        return BaseUIUnitTest.internalWrap(CheckboxGroupTester.class,
                checkboxGroup);
    }

    default <V> ComboBoxTester<ComboBox<V>, V> test(ComboBox<V> comboBox) {
        return BaseUIUnitTest.internalWrap(ComboBoxTester.class, comboBox);
    }

    default ConfirmDialogTester test(ConfirmDialog confirmDialog) {
        return BaseUIUnitTest.internalWrap(ConfirmDialogTester.class,
                confirmDialog);
    }

    default ContextMenuTester<ContextMenu> test(ContextMenu contextMenu) {
        return BaseUIUnitTest.internalWrap(ContextMenuTester.class, contextMenu);
    }

    default DatePickerTester<DatePicker> test(DatePicker datePicker) {
        return BaseUIUnitTest.internalWrap(DatePickerTester.class, datePicker);
    }

    default DateTimePickerTester<DateTimePicker> test(
            DateTimePicker dateTimePicker) {
        return BaseUIUnitTest.internalWrap(DateTimePickerTester.class,
                dateTimePicker);
    }

    default DetailsTester<Details> test(Details details) {
        return BaseUIUnitTest.internalWrap(DetailsTester.class, details);
    }

    default DialogTester test(Dialog dialog) {
        return BaseUIUnitTest.internalWrap(DialogTester.class, dialog);
    }

    default <V> GridTester<Grid<V>, V> test(Grid<V> grid) {
        return BaseUIUnitTest.internalWrap(GridTester.class, grid);
    }

    default <V> ListBoxTester<ListBox<V>, V> test(ListBox<V> listBox) {
        return BaseUIUnitTest.internalWrap(ListBoxTester.class, listBox);
    }

    default <V> MultiSelectListBoxTester<MultiSelectListBox<V>, V> test(
            MultiSelectListBox multiSelectListBox) {
        return BaseUIUnitTest.internalWrap(MultiSelectListBoxTester.class,
                multiSelectListBox);
    }

    default LoginFormTester<LoginForm> test(LoginForm loginForm) {
        return BaseUIUnitTest.internalWrap(LoginFormTester.class, loginForm);
    }

    default LoginOverlayTester<LoginOverlay> test(LoginOverlay loginOverlay) {
        return BaseUIUnitTest.internalWrap(LoginOverlayTester.class,
                loginOverlay);
    }

    default MessageInputTester<MessageInput> test(MessageInput messageInput) {
        return BaseUIUnitTest.internalWrap(MessageInputTester.class,
                messageInput);
    }

    default MessageListTester<MessageList> test(MessageList messageList) {
        return BaseUIUnitTest.internalWrap(MessageListTester.class, messageList);
    }

    default NotificationTester<Notification> test(Notification notification) {
        return BaseUIUnitTest.internalWrap(NotificationTester.class,
                notification);
    }

    default <V> RadioButtonGroupTester<RadioButtonGroup<V>, V> test(
            RadioButtonGroup<V> radioButtonGroup) {
        return BaseUIUnitTest.internalWrap(RadioButtonGroupTester.class,
                radioButtonGroup);
    }
    // RadioButton is package protected so no autowrap.

    default <V> SelectTester<Select<V>, V> test(Select<V> select) {
        return BaseUIUnitTest.internalWrap(SelectTester.class, select);
    }

    default TabsTester<Tabs> test(Tabs tabs) {
        return BaseUIUnitTest.internalWrap(TabsTester.class, tabs);
    }

    default NumberFieldTester<IntegerField, Integer> test(
            IntegerField integerField) {
        return BaseUIUnitTest.internalWrap(NumberFieldTester.class, integerField);
    }

    default NumberFieldTester<NumberField, Double> test(NumberField numberField) {
        return BaseUIUnitTest.internalWrap(NumberFieldTester.class, numberField);
    }

    default TextAreaTester<TextArea> test(TextArea textArea) {
        return BaseUIUnitTest.internalWrap(TextAreaTester.class, textArea);
    }

    default TextFieldTester<TextField, String> test(TextField textField) {
        return BaseUIUnitTest.internalWrap(TextFieldTester.class, textField);
    }

    default TextFieldTester<PasswordField, String> test(
            PasswordField passwordField) {
        return BaseUIUnitTest.internalWrap(TextFieldTester.class, passwordField);
    }

    default TextFieldTester<EmailField, String> test(EmailField emailField) {
        return BaseUIUnitTest.internalWrap(TextFieldTester.class, emailField);
    }

    default TextFieldTester<BigDecimalField, BigDecimal> test(
            BigDecimalField bigDecimalField) {
        return BaseUIUnitTest.internalWrap(TextFieldTester.class,
                bigDecimalField);
    }

    default TimePickerTester<TimePicker> test(TimePicker timePicker) {
        return BaseUIUnitTest.internalWrap(TimePickerTester.class, timePicker);
    }

    default UploadTester<Upload> test(Upload upload) {
        return BaseUIUnitTest.internalWrap(UploadTester.class, upload);
    }

}
