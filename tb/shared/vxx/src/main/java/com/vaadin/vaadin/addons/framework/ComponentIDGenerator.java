/**
 * Copyright © 2017 Sven Ruppert (sven.ruppert@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vaadin.vaadin.addons.framework;

import java.util.function.BiFunction;
import java.util.function.Function;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;

/**
 *
 */
public interface ComponentIDGenerator {

  static Function<String, String> caption() {
    return (id) -> id + "." + "caption";
  }

  static Function<String, String> placeholder() {
    return (id) -> id + "." + "placeholder";
  }


  static Function<Class, BiFunction<Class, String, String>> typedComponentIDGenerator() {
    return (clazz) -> (uiClass , label) -> GenericIDGenerator.genericID().apply(uiClass , clazz , label);
  }

  static BiFunction<Class, String, String> gridID() {
    return (uiClass , label) -> GenericIDGenerator.genericID().apply(uiClass , Grid.class , label);
  }

  static BiFunction<Class, String, String> buttonID() {
    return (uiClass , label) -> GenericIDGenerator.genericID().apply(uiClass , Button.class , label);
  }

  static BiFunction<Class, String, String> comboBoxID() {
    return (uiClass , label) -> GenericIDGenerator.genericID().apply(uiClass , ComboBox.class , label);
  }

  static BiFunction<Class, String, String> datePickerID() {
    return (uiClass , label) -> GenericIDGenerator.genericID().apply(uiClass , DatePicker.class , label);
  }

  static BiFunction<Class, String, String> labelID() {
    return (uiClass , label) -> GenericIDGenerator.genericID().apply(uiClass , Label.class , label);
  }

  static BiFunction<Class, String, String> spanID() {
    return (uiClass , label) -> GenericIDGenerator.genericID().apply(uiClass , Span.class , label);
  }

  static BiFunction<Class, String, String> textfieldID() {
    return (uiClass , label) -> GenericIDGenerator.genericID().apply(uiClass , TextField.class , label);
  }

  static BiFunction<Class, String, String> passwordID() {
    return (uiClass , label) -> GenericIDGenerator.genericID().apply(uiClass , PasswordField.class , label);
  }

  static BiFunction<Class, String, String> checkboxID() {
    return (uiClass , label) -> GenericIDGenerator.genericID().apply(uiClass , Checkbox.class , label);
  }

//  Layouts

  static BiFunction<Class, String, String> verticalLayoutID() {
    return (uiClass , label) -> GenericIDGenerator.genericID().apply(uiClass , VerticalLayout.class , label);
  }

  static BiFunction<Class, String, String> horizontalLayoutID() {
    return (uiClass , label) -> GenericIDGenerator.genericID().apply(uiClass , HorizontalLayout.class , label);
  }
}
