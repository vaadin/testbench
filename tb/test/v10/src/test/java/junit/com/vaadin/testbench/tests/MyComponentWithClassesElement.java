package junit.com.vaadin.testbench.tests;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.annotations.Attribute;

@Attribute(name = "class", contains = Attribute.SIMPLE_CLASS_NAME)
@Attribute(name = "class", contains = "my-component-first")
public class MyComponentWithClassesElement extends TestBenchElement {

}
