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
import com.vaadin.flow.component.accordion.AccordionWrap;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonWrap;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupWrap;
import com.vaadin.flow.component.checkbox.CheckboxWrap;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.ComboBoxWrap;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.confirmdialog.ConfirmDialogWrap;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.contextmenu.ContextMenuWrap;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datepicker.DatePickerWrap;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePickerWrap;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.details.DetailsWrap;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.dialog.DialogWrap;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridWrap;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.listbox.ListBoxWrap;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.listbox.MultiSelectListBoxWrap;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginFormWrap;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.login.LoginOverlayWrap;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.messages.MessageInputWrap;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListWrap;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationWrap;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioButtonGroupWrap;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.select.SelectWrap;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsWrap;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.NumberFieldWrap;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextAreaWrap;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldWrap;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.component.timepicker.TimePickerWrap;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.UploadWrap;

public interface Wrappers {

    default AccordionWrap<Accordion> wrap(Accordion accordion) {
        return BaseUIUnitTest.internalWrap(AccordionWrap.class, accordion);
    }

    default ButtonWrap<Button> wrap(Button button) {
        return BaseUIUnitTest.internalWrap(ButtonWrap.class, button);
    }

    default CheckboxWrap<Checkbox> wrap(Checkbox checkbox) {
        return BaseUIUnitTest.internalWrap(CheckboxWrap.class, checkbox);
    }

    default <V> CheckboxGroupWrap<CheckboxGroup<V>, V> wrap(
            CheckboxGroup<V> checkboxGroup) {
        return BaseUIUnitTest.internalWrap(CheckboxGroupWrap.class,
                checkboxGroup);
    }

    default <V> ComboBoxWrap<ComboBox<V>, V> wrap(ComboBox<V> comboBox) {
        return BaseUIUnitTest.internalWrap(ComboBoxWrap.class, comboBox);
    }

    default ConfirmDialogWrap wrap(ConfirmDialog confirmDialog) {
        return BaseUIUnitTest.internalWrap(ConfirmDialogWrap.class,
                confirmDialog);
    }

    default ContextMenuWrap<ContextMenu> wrap(ContextMenu contextMenu) {
        return BaseUIUnitTest.internalWrap(ContextMenuWrap.class, contextMenu);
    }

    default DatePickerWrap<DatePicker> wrap(DatePicker datePicker) {
        return BaseUIUnitTest.internalWrap(DatePickerWrap.class, datePicker);
    }

    default DateTimePickerWrap<DateTimePicker> wrap(
            DateTimePicker dateTimePicker) {
        return BaseUIUnitTest.internalWrap(DateTimePickerWrap.class,
                dateTimePicker);
    }

    default DetailsWrap<Details> wrap(Details details) {
        return BaseUIUnitTest.internalWrap(DetailsWrap.class, details);
    }

    default DialogWrap wrap(Dialog dialog) {
        return BaseUIUnitTest.internalWrap(DialogWrap.class, dialog);
    }

    default <V> GridWrap<Grid<V>, V> wrap(Grid<V> grid) {
        return BaseUIUnitTest.internalWrap(GridWrap.class, grid);
    }

    default <V> ListBoxWrap<ListBox<V>, V> wrap(ListBox<V> listBox) {
        return BaseUIUnitTest.internalWrap(ListBoxWrap.class, listBox);
    }

    default <V> MultiSelectListBoxWrap<MultiSelectListBox<V>, V> wrap(
            MultiSelectListBox multiSelectListBox) {
        return BaseUIUnitTest.internalWrap(MultiSelectListBoxWrap.class,
                multiSelectListBox);
    }

    default LoginFormWrap<LoginForm> wrap(LoginForm loginForm) {
        return BaseUIUnitTest.internalWrap(LoginFormWrap.class, loginForm);
    }

    default LoginOverlayWrap<LoginOverlay> wrap(LoginOverlay loginOverlay) {
        return BaseUIUnitTest.internalWrap(LoginOverlayWrap.class,
                loginOverlay);
    }

    default MessageInputWrap<MessageInput> wrap(MessageInput messageInput) {
        return BaseUIUnitTest.internalWrap(MessageInputWrap.class,
                messageInput);
    }

    default MessageListWrap<MessageList> wrap(MessageList messageList) {
        return BaseUIUnitTest.internalWrap(MessageListWrap.class, messageList);
    }

    default NotificationWrap<Notification> wrap(Notification notification) {
        return BaseUIUnitTest.internalWrap(NotificationWrap.class,
                notification);
    }

    default <V> RadioButtonGroupWrap<RadioButtonGroup<V>, V> wrap(
            RadioButtonGroup<V> radioButtonGroup) {
        return BaseUIUnitTest.internalWrap(RadioButtonGroupWrap.class,
                radioButtonGroup);
    }
    // RadioButton is package protected so no autowrap.

    default <V> SelectWrap<Select<V>, V> wrap(Select<V> select) {
        return BaseUIUnitTest.internalWrap(SelectWrap.class, select);
    }

    default TabsWrap<Tabs> wrap(Tabs tabs) {
        return BaseUIUnitTest.internalWrap(TabsWrap.class, tabs);
    }

    default NumberFieldWrap<IntegerField, Integer> wrap(
            IntegerField integerField) {
        return BaseUIUnitTest.internalWrap(NumberFieldWrap.class, integerField);
    }

    default NumberFieldWrap<NumberField, Double> wrap(NumberField numberField) {
        return BaseUIUnitTest.internalWrap(NumberFieldWrap.class, numberField);
    }

    default TextAreaWrap<TextArea> wrap(TextArea textArea) {
        return BaseUIUnitTest.internalWrap(TextAreaWrap.class, textArea);
    }

    default TextFieldWrap<TextField, String> wrap(TextField textField) {
        return BaseUIUnitTest.internalWrap(TextFieldWrap.class, textField);
    }

    default TextFieldWrap<PasswordField, String> wrap(
            PasswordField passwordField) {
        return BaseUIUnitTest.internalWrap(TextFieldWrap.class, passwordField);
    }

    default TextFieldWrap<EmailField, String> wrap(EmailField emailField) {
        return BaseUIUnitTest.internalWrap(TextFieldWrap.class, emailField);
    }

    default TextFieldWrap<BigDecimalField, BigDecimal> wrap(
            BigDecimalField bigDecimalField) {
        return BaseUIUnitTest.internalWrap(TextFieldWrap.class,
                bigDecimalField);
    }

    default TimePickerWrap<TimePicker> wrap(TimePicker timePicker) {
        return BaseUIUnitTest.internalWrap(TimePickerWrap.class, timePicker);
    }

    default UploadWrap<Upload> wrap(Upload upload) {
        return BaseUIUnitTest.internalWrap(UploadWrap.class, upload);
    }

}
