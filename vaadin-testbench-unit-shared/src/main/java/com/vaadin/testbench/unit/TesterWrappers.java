/**
 * Copyright (C) 2000-2022 Vaadin Ltd
 *
 * This program is available under Vaadin Commercial License and Service Terms.
 *
 * See <https://vaadin.com/commercial-license-and-service-terms> for the full
 * license.
 */
package com.vaadin.testbench.unit;

import java.math.BigDecimal;

import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.accordion.AccordionTester;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonTester;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.ChartTester;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupTester;
import com.vaadin.flow.component.checkbox.CheckboxTester;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.ComboBoxTester;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBoxTester;
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
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.DescriptionList;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Emphasis;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Input;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.component.html.NativeDetails;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.html.OrderedList;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Pre;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.component.html.testbench.AnchorTester;
import com.vaadin.flow.component.html.testbench.DescriptionListTester;
import com.vaadin.flow.component.html.testbench.DivTester;
import com.vaadin.flow.component.html.testbench.EmphasisTester;
import com.vaadin.flow.component.html.testbench.H1Tester;
import com.vaadin.flow.component.html.testbench.H2Tester;
import com.vaadin.flow.component.html.testbench.H3Tester;
import com.vaadin.flow.component.html.testbench.H4Tester;
import com.vaadin.flow.component.html.testbench.H5Tester;
import com.vaadin.flow.component.html.testbench.H6Tester;
import com.vaadin.flow.component.html.testbench.HrTester;
import com.vaadin.flow.component.html.testbench.ImageTester;
import com.vaadin.flow.component.html.testbench.InputTester;
import com.vaadin.flow.component.html.testbench.LabelTester;
import com.vaadin.flow.component.html.testbench.ListItemTester;
import com.vaadin.flow.component.html.testbench.NativeButtonTester;
import com.vaadin.flow.component.html.testbench.NativeDetailsTester;
import com.vaadin.flow.component.html.testbench.NativeLabelTester;
import com.vaadin.flow.component.html.testbench.OrderedListTester;
import com.vaadin.flow.component.html.testbench.ParagraphTester;
import com.vaadin.flow.component.html.testbench.PreTester;
import com.vaadin.flow.component.html.testbench.SpanTester;
import com.vaadin.flow.component.html.testbench.UnorderedListTester;
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

@SuppressWarnings("unchecked")
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

    default <V> CheckboxGroupTester<CheckboxGroup<V>, V> test(
            CheckboxGroup checkboxGroup, Class<V> valueType) {
        return BaseUIUnitTest.internalWrap(CheckboxGroupTester.class,
                checkboxGroup);
    }

    default <V> ComboBoxTester<ComboBox<V>, V> test(ComboBox<V> comboBox) {
        return BaseUIUnitTest.internalWrap(ComboBoxTester.class, comboBox);
    }

    default <V> ComboBoxTester<ComboBox<V>, V> test(ComboBox comboBox,
            Class<V> valueType) {
        return BaseUIUnitTest.internalWrap(ComboBoxTester.class, comboBox);
    }

    default <V> MultiSelectComboBoxTester<MultiSelectComboBox<V>, V> test(
            MultiSelectComboBox<V> comboBox) {
        return BaseUIUnitTest.internalWrap(MultiSelectComboBoxTester.class,
                comboBox);
    }

    default <V> MultiSelectComboBoxTester<MultiSelectComboBox<V>, V> test(
            MultiSelectComboBox comboBox, Class<V> valueType) {
        return BaseUIUnitTest.internalWrap(MultiSelectComboBoxTester.class,
                comboBox);
    }

    default ConfirmDialogTester test(ConfirmDialog confirmDialog) {
        return BaseUIUnitTest.internalWrap(ConfirmDialogTester.class,
                confirmDialog);
    }

    default ContextMenuTester<ContextMenu> test(ContextMenu contextMenu) {
        return BaseUIUnitTest.internalWrap(ContextMenuTester.class,
                contextMenu);
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

    default <V> GridTester<Grid<V>, V> test(Grid grid, Class<V> itemType) {
        return BaseUIUnitTest.internalWrap(GridTester.class, grid);
    }

    default <V> ListBoxTester<ListBox<V>, V> test(ListBox<V> listBox) {
        return BaseUIUnitTest.internalWrap(ListBoxTester.class, listBox);
    }

    default <V> MultiSelectListBoxTester<MultiSelectListBox<V>, V> test(
            MultiSelectListBox<V> multiSelectListBox) {
        return BaseUIUnitTest.internalWrap(MultiSelectListBoxTester.class,
                multiSelectListBox);
    }

    default <V> MultiSelectListBoxTester<MultiSelectListBox<V>, V> test(
            MultiSelectListBox multiSelectListBox, Class<V> valueType) {
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
        return BaseUIUnitTest.internalWrap(MessageListTester.class,
                messageList);
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

    default <V> RadioButtonGroupTester<RadioButtonGroup<V>, V> test(
            RadioButtonGroup radioButtonGroup, Class<V> valueType) {
        return BaseUIUnitTest.internalWrap(RadioButtonGroupTester.class,
                radioButtonGroup);
    }

    // RadioButton is package protected so no autowrap.

    default <V> SelectTester<Select<V>, V> test(Select<V> select) {
        return BaseUIUnitTest.internalWrap(SelectTester.class, select);
    }

    default <V> SelectTester<Select<V>, V> test(Select select,
            Class<V> valueType) {
        return BaseUIUnitTest.internalWrap(SelectTester.class, select);
    }

    default TabsTester<Tabs> test(Tabs tabs) {
        return BaseUIUnitTest.internalWrap(TabsTester.class, tabs);
    }

    default NumberFieldTester<IntegerField, Integer> test(
            IntegerField integerField) {
        return BaseUIUnitTest.internalWrap(NumberFieldTester.class,
                integerField);
    }

    default NumberFieldTester<NumberField, Double> test(
            NumberField numberField) {
        return BaseUIUnitTest.internalWrap(NumberFieldTester.class,
                numberField);
    }

    default TextAreaTester<TextArea> test(TextArea textArea) {
        return BaseUIUnitTest.internalWrap(TextAreaTester.class, textArea);
    }

    default TextFieldTester<TextField, String> test(TextField textField) {
        return BaseUIUnitTest.internalWrap(TextFieldTester.class, textField);
    }

    default TextFieldTester<PasswordField, String> test(
            PasswordField passwordField) {
        return BaseUIUnitTest.internalWrap(TextFieldTester.class,
                passwordField);
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

    /* HTML components */
    default AnchorTester test(Anchor anchor) {
        return BaseUIUnitTest.internalWrap(AnchorTester.class, anchor);
    }

    default DescriptionListTester test(DescriptionList descriptionList) {
        return BaseUIUnitTest.internalWrap(DescriptionListTester.class,
                descriptionList);
    }

    default DivTester test(Div div) {
        return BaseUIUnitTest.internalWrap(DivTester.class, div);
    }

    default EmphasisTester test(Emphasis emphasis) {
        return BaseUIUnitTest.internalWrap(EmphasisTester.class, emphasis);
    }

    default H1Tester test(H1 h1) {
        return BaseUIUnitTest.internalWrap(H1Tester.class, h1);
    }

    default H2Tester test(H2 h2) {
        return BaseUIUnitTest.internalWrap(H2Tester.class, h2);
    }

    default H3Tester test(H3 h3) {
        return BaseUIUnitTest.internalWrap(H3Tester.class, h3);
    }

    default H4Tester test(H4 h4) {
        return BaseUIUnitTest.internalWrap(H4Tester.class, h4);
    }

    default H5Tester test(H5 h5) {
        return BaseUIUnitTest.internalWrap(H5Tester.class, h5);
    }

    default H6Tester test(H6 h6) {
        return BaseUIUnitTest.internalWrap(H6Tester.class, h6);
    }

    default HrTester test(Hr hr) {
        return BaseUIUnitTest.internalWrap(HrTester.class, hr);
    }

    default ImageTester test(Image image) {
        return BaseUIUnitTest.internalWrap(ImageTester.class, image);
    }

    default InputTester test(Input input) {
        return BaseUIUnitTest.internalWrap(InputTester.class, input);
    }

    default LabelTester test(Label label) {
        return BaseUIUnitTest.internalWrap(LabelTester.class, label);
    }

    default NativeLabelTester test(NativeLabel label) {
        return BaseUIUnitTest.internalWrap(NativeLabelTester.class, label);
    }

    default ListItemTester test(ListItem listItem) {
        return BaseUIUnitTest.internalWrap(ListItemTester.class, listItem);
    }

    default NativeButtonTester test(NativeButton nativeButton) {
        return BaseUIUnitTest.internalWrap(NativeButtonTester.class,
                nativeButton);
    }

    default NativeDetailsTester test(NativeDetails nativeDetails) {
        return BaseUIUnitTest.internalWrap(NativeDetailsTester.class,
                nativeDetails);
    }

    default OrderedListTester test(OrderedList orderedList) {
        return BaseUIUnitTest.internalWrap(OrderedListTester.class,
                orderedList);
    }

    default ParagraphTester test(Paragraph paragraph) {
        return BaseUIUnitTest.internalWrap(ParagraphTester.class, paragraph);
    }

    default PreTester test(Pre pre) {
        return BaseUIUnitTest.internalWrap(PreTester.class, pre);
    }

    default SpanTester test(Span span) {
        return BaseUIUnitTest.internalWrap(SpanTester.class, span);
    }

    default UnorderedListTester test(UnorderedList unorderedList) {
        return BaseUIUnitTest.internalWrap(UnorderedListTester.class,
                unorderedList);
    }

    default ChartTester<Chart> test(Chart chart) {
        return BaseUIUnitTest.internalWrap(ChartTester.class, chart);
    }
}
