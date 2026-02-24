/**
 * Copyright (C) 2000-2026 Vaadin Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vaadin.browserless;

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
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.component.html.NativeDetails;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.html.OrderedList;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Pre;
import com.vaadin.flow.component.html.RangeInput;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.component.html.tester.AnchorTester;
import com.vaadin.flow.component.html.tester.DescriptionListTester;
import com.vaadin.flow.component.html.tester.DivTester;
import com.vaadin.flow.component.html.tester.EmphasisTester;
import com.vaadin.flow.component.html.tester.H1Tester;
import com.vaadin.flow.component.html.tester.H2Tester;
import com.vaadin.flow.component.html.tester.H3Tester;
import com.vaadin.flow.component.html.tester.H4Tester;
import com.vaadin.flow.component.html.tester.H5Tester;
import com.vaadin.flow.component.html.tester.H6Tester;
import com.vaadin.flow.component.html.tester.HrTester;
import com.vaadin.flow.component.html.tester.ImageTester;
import com.vaadin.flow.component.html.tester.InputTester;
import com.vaadin.flow.component.html.tester.ListItemTester;
import com.vaadin.flow.component.html.tester.NativeButtonTester;
import com.vaadin.flow.component.html.tester.NativeDetailsTester;
import com.vaadin.flow.component.html.tester.NativeLabelTester;
import com.vaadin.flow.component.html.tester.OrderedListTester;
import com.vaadin.flow.component.html.tester.ParagraphTester;
import com.vaadin.flow.component.html.tester.PreTester;
import com.vaadin.flow.component.html.tester.RangeInputTester;
import com.vaadin.flow.component.html.tester.SpanTester;
import com.vaadin.flow.component.html.tester.UnorderedListTester;
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
import com.vaadin.flow.component.routerlink.RouterLinkTester;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.select.SelectTester;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavTester;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.tabs.TabSheetTester;
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
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.component.virtuallist.VirtualListTester;
import com.vaadin.flow.router.RouterLink;

@SuppressWarnings("unchecked")
public interface TesterWrappers {

    default AccordionTester<Accordion> test(Accordion accordion) {
        return BaseBrowserlessTest.internalWrap(AccordionTester.class,
                accordion);
    }

    default ButtonTester<Button> test(Button button) {
        return BaseBrowserlessTest.internalWrap(ButtonTester.class, button);
    }

    default CheckboxTester<Checkbox> test(Checkbox checkbox) {
        return BaseBrowserlessTest.internalWrap(CheckboxTester.class, checkbox);
    }

    default <V> CheckboxGroupTester<CheckboxGroup<V>, V> test(
            CheckboxGroup<V> checkboxGroup) {
        return BaseBrowserlessTest.internalWrap(CheckboxGroupTester.class,
                checkboxGroup);
    }

    default <V> CheckboxGroupTester<CheckboxGroup<V>, V> test(
            CheckboxGroup checkboxGroup, Class<V> valueType) {
        return BaseBrowserlessTest.internalWrap(CheckboxGroupTester.class,
                checkboxGroup);
    }

    default <V> ComboBoxTester<ComboBox<V>, V> test(ComboBox<V> comboBox) {
        return BaseBrowserlessTest.internalWrap(ComboBoxTester.class, comboBox);
    }

    default <V> ComboBoxTester<ComboBox<V>, V> test(ComboBox comboBox,
            Class<V> valueType) {
        return BaseBrowserlessTest.internalWrap(ComboBoxTester.class, comboBox);
    }

    default <V> MultiSelectComboBoxTester<MultiSelectComboBox<V>, V> test(
            MultiSelectComboBox<V> comboBox) {
        return BaseBrowserlessTest.internalWrap(MultiSelectComboBoxTester.class,
                comboBox);
    }

    default <V> MultiSelectComboBoxTester<MultiSelectComboBox<V>, V> test(
            MultiSelectComboBox comboBox, Class<V> valueType) {
        return BaseBrowserlessTest.internalWrap(MultiSelectComboBoxTester.class,
                comboBox);
    }

    default ConfirmDialogTester test(ConfirmDialog confirmDialog) {
        return BaseBrowserlessTest.internalWrap(ConfirmDialogTester.class,
                confirmDialog);
    }

    default ContextMenuTester<ContextMenu> test(ContextMenu contextMenu) {
        return BaseBrowserlessTest.internalWrap(ContextMenuTester.class,
                contextMenu);
    }

    default DatePickerTester<DatePicker> test(DatePicker datePicker) {
        return BaseBrowserlessTest.internalWrap(DatePickerTester.class,
                datePicker);
    }

    default DateTimePickerTester<DateTimePicker> test(
            DateTimePicker dateTimePicker) {
        return BaseBrowserlessTest.internalWrap(DateTimePickerTester.class,
                dateTimePicker);
    }

    default DetailsTester<Details> test(Details details) {
        return BaseBrowserlessTest.internalWrap(DetailsTester.class, details);
    }

    default DialogTester test(Dialog dialog) {
        return BaseBrowserlessTest.internalWrap(DialogTester.class, dialog);
    }

    default <V> GridTester<Grid<V>, V> test(Grid<V> grid) {
        return BaseBrowserlessTest.internalWrap(GridTester.class, grid);
    }

    default <V> GridTester<Grid<V>, V> test(Grid grid, Class<V> itemType) {
        return BaseBrowserlessTest.internalWrap(GridTester.class, grid);
    }

    default <V> ListBoxTester<ListBox<V>, V> test(ListBox<V> listBox) {
        return BaseBrowserlessTest.internalWrap(ListBoxTester.class, listBox);
    }

    default <V> MultiSelectListBoxTester<MultiSelectListBox<V>, V> test(
            MultiSelectListBox<V> multiSelectListBox) {
        return BaseBrowserlessTest.internalWrap(MultiSelectListBoxTester.class,
                multiSelectListBox);
    }

    default <V> MultiSelectListBoxTester<MultiSelectListBox<V>, V> test(
            MultiSelectListBox multiSelectListBox, Class<V> valueType) {
        return BaseBrowserlessTest.internalWrap(MultiSelectListBoxTester.class,
                multiSelectListBox);
    }

    default LoginFormTester<LoginForm> test(LoginForm loginForm) {
        return BaseBrowserlessTest.internalWrap(LoginFormTester.class,
                loginForm);
    }

    default LoginOverlayTester<LoginOverlay> test(LoginOverlay loginOverlay) {
        return BaseBrowserlessTest.internalWrap(LoginOverlayTester.class,
                loginOverlay);
    }

    default MessageInputTester<MessageInput> test(MessageInput messageInput) {
        return BaseBrowserlessTest.internalWrap(MessageInputTester.class,
                messageInput);
    }

    default MessageListTester<MessageList> test(MessageList messageList) {
        return BaseBrowserlessTest.internalWrap(MessageListTester.class,
                messageList);
    }

    default NotificationTester<Notification> test(Notification notification) {
        return BaseBrowserlessTest.internalWrap(NotificationTester.class,
                notification);
    }

    default <V> RadioButtonGroupTester<RadioButtonGroup<V>, V> test(
            RadioButtonGroup<V> radioButtonGroup) {
        return BaseBrowserlessTest.internalWrap(RadioButtonGroupTester.class,
                radioButtonGroup);
    }

    default <V> RadioButtonGroupTester<RadioButtonGroup<V>, V> test(
            RadioButtonGroup radioButtonGroup, Class<V> valueType) {
        return BaseBrowserlessTest.internalWrap(RadioButtonGroupTester.class,
                radioButtonGroup);
    }

    // RadioButton is package protected so no autowrap.

    default RouterLinkTester<RouterLink> test(RouterLink routerLink) {
        return BaseBrowserlessTest.internalWrap(RouterLinkTester.class,
                routerLink);
    }

    default <V> SelectTester<Select<V>, V> test(Select<V> select) {
        return BaseBrowserlessTest.internalWrap(SelectTester.class, select);
    }

    default <V> SelectTester<Select<V>, V> test(Select select,
            Class<V> valueType) {
        return BaseBrowserlessTest.internalWrap(SelectTester.class, select);
    }

    default SideNavTester<SideNav> test(SideNav sideNav) {
        return BaseBrowserlessTest.internalWrap(SideNavTester.class, sideNav);
    }

    default TabsTester<Tabs> test(Tabs tabs) {
        return BaseBrowserlessTest.internalWrap(TabsTester.class, tabs);
    }

    default TabSheetTester<TabSheet> test(TabSheet tabSheet) {
        return BaseBrowserlessTest.internalWrap(TabSheetTester.class, tabSheet);
    }

    default NumberFieldTester<IntegerField, Integer> test(
            IntegerField integerField) {
        return BaseBrowserlessTest.internalWrap(NumberFieldTester.class,
                integerField);
    }

    default NumberFieldTester<NumberField, Double> test(
            NumberField numberField) {
        return BaseBrowserlessTest.internalWrap(NumberFieldTester.class,
                numberField);
    }

    default TextAreaTester<TextArea> test(TextArea textArea) {
        return BaseBrowserlessTest.internalWrap(TextAreaTester.class, textArea);
    }

    default TextFieldTester<TextField, String> test(TextField textField) {
        return BaseBrowserlessTest.internalWrap(TextFieldTester.class,
                textField);
    }

    default TextFieldTester<PasswordField, String> test(
            PasswordField passwordField) {
        return BaseBrowserlessTest.internalWrap(TextFieldTester.class,
                passwordField);
    }

    default TextFieldTester<EmailField, String> test(EmailField emailField) {
        return BaseBrowserlessTest.internalWrap(TextFieldTester.class,
                emailField);
    }

    default TextFieldTester<BigDecimalField, BigDecimal> test(
            BigDecimalField bigDecimalField) {
        return BaseBrowserlessTest.internalWrap(TextFieldTester.class,
                bigDecimalField);
    }

    default TimePickerTester<TimePicker> test(TimePicker timePicker) {
        return BaseBrowserlessTest.internalWrap(TimePickerTester.class,
                timePicker);
    }

    default UploadTester<Upload> test(Upload upload) {
        return BaseBrowserlessTest.internalWrap(UploadTester.class, upload);
    }

    default <V> VirtualListTester<VirtualList<V>, V> test(
            VirtualList<V> virtualList) {
        return BaseBrowserlessTest.internalWrap(VirtualListTester.class,
                virtualList);
    }

    default <V> VirtualListTester<VirtualList<V>, V> test(
            VirtualList virtualList, Class<V> itemType) {
        return BaseBrowserlessTest.internalWrap(VirtualListTester.class,
                virtualList);
    }

    /* HTML components */

    default AnchorTester test(Anchor anchor) {
        return BaseBrowserlessTest.internalWrap(AnchorTester.class, anchor);
    }

    default DescriptionListTester test(DescriptionList descriptionList) {
        return BaseBrowserlessTest.internalWrap(DescriptionListTester.class,
                descriptionList);
    }

    default DivTester test(Div div) {
        return BaseBrowserlessTest.internalWrap(DivTester.class, div);
    }

    default EmphasisTester test(Emphasis emphasis) {
        return BaseBrowserlessTest.internalWrap(EmphasisTester.class, emphasis);
    }

    default H1Tester test(H1 h1) {
        return BaseBrowserlessTest.internalWrap(H1Tester.class, h1);
    }

    default H2Tester test(H2 h2) {
        return BaseBrowserlessTest.internalWrap(H2Tester.class, h2);
    }

    default H3Tester test(H3 h3) {
        return BaseBrowserlessTest.internalWrap(H3Tester.class, h3);
    }

    default H4Tester test(H4 h4) {
        return BaseBrowserlessTest.internalWrap(H4Tester.class, h4);
    }

    default H5Tester test(H5 h5) {
        return BaseBrowserlessTest.internalWrap(H5Tester.class, h5);
    }

    default H6Tester test(H6 h6) {
        return BaseBrowserlessTest.internalWrap(H6Tester.class, h6);
    }

    default HrTester test(Hr hr) {
        return BaseBrowserlessTest.internalWrap(HrTester.class, hr);
    }

    default ImageTester test(Image image) {
        return BaseBrowserlessTest.internalWrap(ImageTester.class, image);
    }

    default InputTester test(Input input) {
        return BaseBrowserlessTest.internalWrap(InputTester.class, input);
    }

    default RangeInputTester test(RangeInput input) {
        return BaseBrowserlessTest.internalWrap(RangeInputTester.class, input);
    }

    default NativeLabelTester test(NativeLabel label) {
        return BaseBrowserlessTest.internalWrap(NativeLabelTester.class, label);
    }

    default ListItemTester test(ListItem listItem) {
        return BaseBrowserlessTest.internalWrap(ListItemTester.class, listItem);
    }

    default NativeButtonTester test(NativeButton nativeButton) {
        return BaseBrowserlessTest.internalWrap(NativeButtonTester.class,
                nativeButton);
    }

    default NativeDetailsTester test(NativeDetails nativeDetails) {
        return BaseBrowserlessTest.internalWrap(NativeDetailsTester.class,
                nativeDetails);
    }

    default OrderedListTester test(OrderedList orderedList) {
        return BaseBrowserlessTest.internalWrap(OrderedListTester.class,
                orderedList);
    }

    default ParagraphTester test(Paragraph paragraph) {
        return BaseBrowserlessTest.internalWrap(ParagraphTester.class,
                paragraph);
    }

    default PreTester test(Pre pre) {
        return BaseBrowserlessTest.internalWrap(PreTester.class, pre);
    }

    default SpanTester test(Span span) {
        return BaseBrowserlessTest.internalWrap(SpanTester.class, span);
    }

    default UnorderedListTester test(UnorderedList unorderedList) {
        return BaseBrowserlessTest.internalWrap(UnorderedListTester.class,
                unorderedList);
    }
}
