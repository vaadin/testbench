package com.vaadin.testbench.finder;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.SearchContext;

public class Vaadin {

    private static final Logger logger = Logger.getLogger(Vaadin.class
            .getName());

    private static final Map<String, Class<? extends ComponentFinder>> vaadinComponentToFinderMap;

    static {
        vaadinComponentToFinderMap = new HashMap<String, Class<? extends ComponentFinder>>();

        vaadinComponentToFinderMap.put("com.vaadin.ui.AbsoluteLayout",
                AbsoluteLayoutFinder.class);
        vaadinComponentToFinderMap.put("com.vaadin.ui.Accordion",
                AccordionFinder.class);
        vaadinComponentToFinderMap
                .put("com.vaadin.ui.Audio", AudioFinder.class);
        vaadinComponentToFinderMap.put("com.vaadin.ui.Button",
                ButtonFinder.class);
        vaadinComponentToFinderMap.put("com.vaadin.ui.Calendar",
                CalendarFinder.class);
        vaadinComponentToFinderMap.put("com.vaadin.ui.CheckBox",
                CheckBoxFinder.class);
        vaadinComponentToFinderMap.put("com.vaadin.ui.ColorPicker",
                ColorPickerFinder.class);
        vaadinComponentToFinderMap.put("com.vaadin.ui.ColorPickerArea",
                ColorPickerAreaFinder.class);
        vaadinComponentToFinderMap.put("com.vaadin.ui.ComboBox",
                ComboBoxFinder.class);
        vaadinComponentToFinderMap.put("com.vaadin.ui.CssLayout",
                CssLayoutFinder.class);
        vaadinComponentToFinderMap.put("com.vaadin.ui.CustomComponent",
                CustomComponentFinder.class);
        vaadinComponentToFinderMap.put("com.vaadin.ui.CustomLayout",
                CustomLayoutFinder.class);
        vaadinComponentToFinderMap.put("com.vaadin.ui.DateField",
                DateFieldFinder.class);
        vaadinComponentToFinderMap.put("com.vaadin.ui.Embedded",
                EmbeddedFinder.class);
        vaadinComponentToFinderMap
                .put("com.vaadin.ui.Flash", FlashFinder.class);
        vaadinComponentToFinderMap.put("com.vaadin.ui.Form", FormFinder.class);
        vaadinComponentToFinderMap.put("com.vaadin.ui.FormLayout",
                FormLayoutFinder.class);
        vaadinComponentToFinderMap.put("com.vaadin.ui.GridLayout",
                GridLayoutFinder.class);
        vaadinComponentToFinderMap.put("com.vaadin.ui.HorizontalLayout",
                HorizontalLayoutFinder.class);
        vaadinComponentToFinderMap
                .put("com.vaadin.ui.Image", ImageFinder.class);
        vaadinComponentToFinderMap
                .put("com.vaadin.ui.Label", LabelFinder.class);
        vaadinComponentToFinderMap.put("com.vaadin.ui.Link", LinkFinder.class);
        vaadinComponentToFinderMap.put("com.vaadin.ui.ListSelect",
                ListSelectFinder.class);
        vaadinComponentToFinderMap.put("com.vaadin.ui.MenuBar",
                MenuBarFinder.class);
        vaadinComponentToFinderMap.put("com.vaadin.ui.NativeButton",
                NativeButtonFinder.class);
        vaadinComponentToFinderMap.put("com.vaadin.ui.NativeSelect",
                NativeSelectFinder.class);
        vaadinComponentToFinderMap.put("com.vaadin.ui.Notification",
                NotificationFinder.class);
        vaadinComponentToFinderMap.put("com.vaadin.ui.OptionGroup",
                OptionGroupFinder.class);
        vaadinComponentToFinderMap
                .put("com.vaadin.ui.Panel", PanelFinder.class);
        vaadinComponentToFinderMap.put("com.vaadin.ui.PasswordField",
                PasswordFieldFinder.class);
        vaadinComponentToFinderMap.put("com.vaadin.ui.PopupView",
                PopupViewFinder.class);
        vaadinComponentToFinderMap.put("com.vaadin.ui.ProgressBar",
                ProgressBarFinder.class);
        vaadinComponentToFinderMap.put("com.vaadin.ui.ProgressIndicator",
                ProgressIndicatorFinder.class);
        vaadinComponentToFinderMap.put("com.vaadin.ui.RichTextArea",
                RichTextAreaFinder.class);
        vaadinComponentToFinderMap.put("com.vaadin.ui.Slider",
                SliderFinder.class);
        vaadinComponentToFinderMap.put("com.vaadin.ui.HorizontalSplitPanel",
                HorizontalSplitPanelFinder.class);
        vaadinComponentToFinderMap.put("com.vaadin.ui.VerticalSplitPanel",
                VerticalSplitPanel.class);
        vaadinComponentToFinderMap.put("com.vaadin.ui.TabSheet",
                TabSheetFinder.class);
        vaadinComponentToFinderMap
                .put("com.vaadin.ui.Table", TableFinder.class);
        vaadinComponentToFinderMap.put("com.vaadin.ui.TextArea",
                TextAreaFinder.class);
        vaadinComponentToFinderMap.put("com.vaadin.ui.TextField",
                TextFieldFinder.class);
        vaadinComponentToFinderMap.put("com.vaadin.ui.TwinColSelect",
                TwinColSelectFinder.class);
        vaadinComponentToFinderMap.put("com.vaadin.ui.Tree", TreeFinder.class);
        vaadinComponentToFinderMap.put("com.vaadin.ui.TreeTable",
                TreeTableFinder.class);
        vaadinComponentToFinderMap.put("com.vaadin.ui.UI", UIFinder.class);
        vaadinComponentToFinderMap.put("com.vaadin.ui.Upload",
                UploadFinder.class);
        vaadinComponentToFinderMap.put("com.vaadin.ui.VerticalLayout",
                VerticalLayoutFinder.class);
        vaadinComponentToFinderMap
                .put("com.vaadin.ui.Video", VideoFinder.class);
        vaadinComponentToFinderMap.put("com.vaadin.ui.Window",
                WindowFinder.class);

    }

    @SuppressWarnings("unchecked")
    public static <T extends ComponentFinder> T find(Class<?> type,
            SearchContext searchContext) {

        if (type == null) {
            throw new IllegalArgumentException("finderType may not be null");
        }

        if (searchContext == null) {
            throw new IllegalArgumentException("searchContext may not be null");
        }

        Class<? extends ComponentFinder> cls;

        if (ComponentFinder.class.isAssignableFrom(type)) {
            // Cast to ComponentFinder
            cls = (Class<? extends ComponentFinder>) type;
        } else {
            // Attempt to find correct ComponentFinder class by class map
            cls = vaadinComponentToFinderMap.get(type.getName());
            if (cls == null) {
                throw new IllegalArgumentException(
                        "FinderType is not supported ");
            }
        }

        ComponentFinder finder = newFinderInstance(cls);
        if (finder != null) {
            finder.inContext(searchContext);
        }

        return (T) finder;

    }

    private static <F extends ComponentFinder> F newFinderInstance(
            Class<F> finderType) {
        try {
            return finderType.newInstance();
        } catch (InstantiationException e) {
            logger.log(Level.WARNING, e.getMessage(), e);
        } catch (IllegalAccessException e) {
            logger.log(Level.WARNING, e.getMessage(), e);
        }
        return null;
    }

    public static class AbsoluteLayoutFinder extends ComponentFinder {
        AbsoluteLayoutFinder() {
            super("VAbsoluteLayout");
        }
    }

    public static Class<? extends ComponentFinder> absoluteLayout() {
        return AbsoluteLayoutFinder.class;
    }

    public static class AccordionFinder extends ComponentFinder {
        AccordionFinder() {
            super("VAccordion");
        }
    }

    public static Class<? extends ComponentFinder> accordion() {
        return AccordionFinder.class;
    }

    public static class AudioFinder extends ComponentFinder {
        AudioFinder() {
            super("VAudio");
        }
    }

    public static Class<? extends ComponentFinder> audio() {
        return AudioFinder.class;
    }

    public static class ButtonFinder extends ComponentFinder {
        ButtonFinder() {
            super("VButton");
        }
    }

    public static Class<? extends ComponentFinder> button() {
        return ButtonFinder.class;
    }

    public static class CalendarFinder extends ComponentFinder {
        CalendarFinder() {
            super("VCalendar");
        }
    }

    public static Class<? extends ComponentFinder> calendar() {
        return CalendarFinder.class;
    }

    public static class CheckBoxFinder extends ComponentFinder {
        CheckBoxFinder() {
            super("VCheckBox");
        }
    }

    public static Class<? extends ComponentFinder> checkBox() {
        return CheckBoxFinder.class;
    }

    public static class ColorPickerFinder extends ComponentFinder {
        ColorPickerFinder() {
            super("VColorPicker");
        }
    }

    public static Class<? extends ComponentFinder> colorPicker() {
        return ColorPickerFinder.class;
    }

    public static class ColorPickerAreaFinder extends ComponentFinder {
        ColorPickerAreaFinder() {
            super("VColorPickerArea");
        }
    }

    public static Class<? extends ComponentFinder> colorPickerArea() {
        return ColorPickerAreaFinder.class;
    }

    public static class CssLayoutFinder extends ComponentFinder {
        CssLayoutFinder() {
            super("VCssLayout");
        }
    }

    public static Class<? extends ComponentFinder> cssLayout() {
        return CssLayoutFinder.class;
    }

    public static class ComboBoxFinder extends ComponentFinder {
        ComboBoxFinder() {
            super("VFilterSelect");
        }
    }

    public static Class<? extends ComponentFinder> comboBox() {
        return ComboBoxFinder.class;
    }

    public static class CustomComponentFinder extends ComponentFinder {
        CustomComponentFinder() {
            super("VCustomComponent");
        }
    }

    public static Class<? extends ComponentFinder> customComponent() {
        return CustomComponentFinder.class;
    }

    public static class CustomLayoutFinder extends ComponentFinder {
        CustomLayoutFinder() {
            super("VCustomLayout");
        }
    }

    public static Class<? extends ComponentFinder> customLayout() {
        return CustomLayoutFinder.class;
    }

    public static class DateFieldFinder extends ComponentFinder {
        DateFieldFinder() {
            super("VDateField");
        }
    }

    public static Class<? extends ComponentFinder> dateField() {
        return DateFieldFinder.class;
    }

    public static class TextualDateFinder extends ComponentFinder {
        TextualDateFinder() {
            super("VTextualDate");
        }
    }

    public static Class<? extends ComponentFinder> textualDate() {
        return TextualDateFinder.class;
    }

    public static class PopupCalendarFinder extends ComponentFinder {
        PopupCalendarFinder() {
            super("VPopupCalendar");
        }
    }

    public static Class<? extends ComponentFinder> popupCalendar() {
        return PopupCalendarFinder.class;
    }

    public static class DateFieldCalendarFinder extends ComponentFinder {
        DateFieldCalendarFinder() {
            super("VDateFieldCalendar");
        }
    }

    public static Class<? extends ComponentFinder> dateFieldCalendar() {
        return DateFieldCalendarFinder.class;
    }

    /**
     * Matches an embedded component. Note, that there are separate Audio, Video
     * and Flash finders for more specific purposes.
     */
    public static class EmbeddedFinder extends ComponentFinder {
        EmbeddedFinder() {
            super("VEmbedded");
        }
    }

    public static Class<? extends ComponentFinder> embedded() {
        return EmbeddedFinder.class;
    }

    public static class FlashFinder extends ComponentFinder {
        FlashFinder() {
            super("VFlash");
        }
    }

    public static Class<? extends ComponentFinder> flash() {
        return FlashFinder.class;
    }

    public static class FormFinder extends ComponentFinder {
        FormFinder() {
            super("VForm");
        }
    }

    public static Class<? extends ComponentFinder> form() {
        return FormFinder.class;
    }

    public static class FormLayoutFinder extends ComponentFinder {
        FormLayoutFinder() {
            super("VFormLayout");
        }
    }

    public static Class<? extends ComponentFinder> formLayout() {
        return FormLayoutFinder.class;
    }

    public static class GridLayoutFinder extends ComponentFinder {
        GridLayoutFinder() {
            super("VGridLayout");
        }
    }

    public static Class<? extends ComponentFinder> gridLayout() {
        return GridLayoutFinder.class;
    }

    public static class HorizontalLayoutFinder extends ComponentFinder {
        HorizontalLayoutFinder() {
            super("VHorizontalLayout");
        }
    }

    public static Class<? extends ComponentFinder> horizontalLayout() {
        return HorizontalLayoutFinder.class;
    }

    public static class ImageFinder extends ComponentFinder {
        ImageFinder() {
            super("VImage");
        }
    }

    public static Class<? extends ComponentFinder> image() {
        return ImageFinder.class;
    }

    public static class LabelFinder extends ComponentFinder {
        LabelFinder() {
            super("VLabel");
        }
    }

    public static Class<? extends ComponentFinder> label() {
        return LabelFinder.class;
    }

    public static class LinkFinder extends ComponentFinder {
        LinkFinder() {
            super("VLink");
        }
    }

    public static Class<? extends ComponentFinder> link() {
        return LinkFinder.class;
    }

    public static class ListSelectFinder extends ComponentFinder {
        ListSelectFinder() {
            super("VListSelect");
        }
    }

    public static Class<? extends ComponentFinder> listSelect() {
        return ListSelectFinder.class;
    }

    public static class MenuBarFinder extends ComponentFinder {
        MenuBarFinder() {
            super("VMenuBar");
        }
    }

    public static Class<? extends ComponentFinder> menubar() {
        return MenuBarFinder.class;
    }

    public static class NativeButtonFinder extends ComponentFinder {
        NativeButtonFinder() {
            super("VNativeButton");
        }
    }

    public static Class<? extends ComponentFinder> nativeButton() {
        return NativeButtonFinder.class;
    }

    public static class NativeSelectFinder extends ComponentFinder {
        NativeSelectFinder() {
            super("VNativeSelect");
        }
    }

    public static Class<? extends ComponentFinder> nativeSelect() {
        return NativeSelectFinder.class;
    }

    public static class NotificationFinder extends ComponentFinder {
        NotificationFinder() {
            super("VNotification");
        }
    }

    public static Class<? extends ComponentFinder> notification() {
        return NotificationFinder.class;
    }

    public static class OptionGroupFinder extends ComponentFinder {
        OptionGroupFinder() {
            super("VOptionGroup");
        }
    }

    public static Class<? extends ComponentFinder> optionGroup() {
        return OptionGroupFinder.class;
    }

    public static class PanelFinder extends ComponentFinder {
        PanelFinder() {
            super("VPanel");
        }
    }

    public static Class<? extends ComponentFinder> panel() {
        return PanelFinder.class;
    }

    public static class PasswordFieldFinder extends ComponentFinder {
        PasswordFieldFinder() {
            super("VPasswordField");
        }
    }

    public static Class<? extends ComponentFinder> passwordField() {
        return PasswordFieldFinder.class;
    }

    public static class PopupViewFinder extends ComponentFinder {
        PopupViewFinder() {
            super("VPopupView");
        }
    }

    public static Class<? extends ComponentFinder> popupView() {
        return PopupViewFinder.class;
    }

    public static class ProgressBarFinder extends ComponentFinder {
        ProgressBarFinder() {
            super("VProgressBar");
        }
    }

    public static Class<? extends ComponentFinder> progressBar() {
        return ProgressBarFinder.class;
    }

    public static class ProgressIndicatorFinder extends ComponentFinder {
        ProgressIndicatorFinder() {
            super("VProgressIndicator");
        }
    }

    public static Class<? extends ComponentFinder> progressIndicator() {
        return ProgressIndicatorFinder.class;
    }

    public static class RichTextAreaFinder extends ComponentFinder {
        RichTextAreaFinder() {
            super("VRichTextArea");
        }
    }

    public static Class<? extends ComponentFinder> richTextArea() {
        return RichTextAreaFinder.class;
    }

    public static class SliderFinder extends ComponentFinder {
        SliderFinder() {
            super("VSlider");
        }
    }

    public static Class<? extends ComponentFinder> slider() {
        return SliderFinder.class;
    }

    public static class HorizontalSplitPanelFinder extends ComponentFinder {
        HorizontalSplitPanelFinder() {
            super("VSplitPanelHorizontal");
        }
    }

    public static Class<? extends ComponentFinder> splitPanelHorizontal() {
        return HorizontalSplitPanelFinder.class;
    }

    public static class VerticalSplitPanel extends ComponentFinder {
        VerticalSplitPanel() {
            super("VSplitPanelVertical");
        }
    }

    public static Class<? extends ComponentFinder> splitPanelVertical() {
        return VerticalSplitPanel.class;
    }

    public static class TabSheetFinder extends ComponentFinder {
        TabSheetFinder() {
            super("VTabSheet");
        }
    }

    public static Class<? extends ComponentFinder> tabSheet() {
        return TabSheetFinder.class;
    }

    public static class TableFinder extends ComponentFinder {
        TableFinder() {
            super("VScrollTable");
        }
    }

    public static Class<? extends ComponentFinder> table() {
        return TableFinder.class;
    }

    public static class TextAreaFinder extends ComponentFinder {
        TextAreaFinder() {
            super("VTextArea");
        }
    }

    public static Class<? extends ComponentFinder> textArea() {
        return TextAreaFinder.class;
    }

    public static class TextFieldFinder extends ComponentFinder {
        TextFieldFinder() {
            super("VTextField");
        }
    }

    public static Class<? extends ComponentFinder> textField() {
        return TextFieldFinder.class;
    }

    public static class TwinColSelectFinder extends ComponentFinder {
        TwinColSelectFinder() {
            super("VTwinColSelect");
        }
    }

    public static Class<? extends ComponentFinder> twinColSelect() {
        return TwinColSelectFinder.class;
    }

    public static class TreeFinder extends ComponentFinder {
        TreeFinder() {
            super("VTree");
        }
    }

    public static Class<? extends ComponentFinder> tree() {
        return TreeFinder.class;
    }

    public static class TreeTableFinder extends ComponentFinder {
        TreeTableFinder() {
            super("VTreeTable");
        }
    }

    public static Class<? extends ComponentFinder> treeTable() {
        return TreeTableFinder.class;
    }

    public static class UIFinder extends ComponentFinder {
        UIFinder() {
            super("VUI");
        }
    }

    public static Class<? extends ComponentFinder> ui() {
        return UIFinder.class;
    }

    public static class UploadFinder extends ComponentFinder {
        UploadFinder() {
            super("VUpload");
        }
    }

    public static Class<? extends ComponentFinder> upload() {
        return UploadFinder.class;
    }

    public static class VerticalLayoutFinder extends ComponentFinder {
        VerticalLayoutFinder() {
            super("VVerticalLayout");
        }
    }

    public static Class<? extends ComponentFinder> verticalLayout() {
        return VerticalLayoutFinder.class;
    }

    public static class VideoFinder extends ComponentFinder {
        VideoFinder() {
            super("VVideo");
        }
    }

    public static Class<? extends ComponentFinder> video() {
        return VideoFinder.class;
    }

    public static class WindowFinder extends ComponentFinder {
        WindowFinder() {
            super("VWindow");
        }
    }

    public static Class<? extends ComponentFinder> window() {
        return WindowFinder.class;
    }

}
