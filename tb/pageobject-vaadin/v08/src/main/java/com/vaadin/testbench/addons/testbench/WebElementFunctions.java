package com.vaadin.testbench.addons.testbench;

import com.vaadin.frp.functions.CheckedFunction;
import com.vaadin.testbench.elements.TextFieldElement;

/**
 *
 */
public interface WebElementFunctions {

  static CheckedFunction<TextFieldElement, Float> floatOfTextField() {
    return (tf) -> Float.valueOf(tf.getValue());
  }

  static CheckedFunction<TextFieldElement, Integer> intOfTextField() {
    return (tf) -> Integer.valueOf(tf.getValue());
  }

  static CheckedFunction<TextFieldElement, Long> longOfTextField() {
    return (tf) -> Long.valueOf(tf.getValue());
  }


}
