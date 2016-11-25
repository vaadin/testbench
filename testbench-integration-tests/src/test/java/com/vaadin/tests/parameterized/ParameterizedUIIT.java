package com.vaadin.tests.parameterized;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import com.vaadin.testbench.annotations.RunLocally;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.parallel.Browser;
import com.vaadin.tests.testbenchapi.MultiBrowserTest;

@RunLocally(Browser.CHROME)
public class ParameterizedUIIT extends MultiBrowserTest {

    @Parameter
    public String firstName;

    @Parameters
    public static Collection<String> getFirstNames() {
        return Arrays.asList("John", "Jeff", "Guillermo", "Dmitrii");
    }


    @Test
    public void ensureText() {
        openTestURL("name=" + firstName);
        System.out.println("Running test for " + firstName);
        Assert.assertEquals("Hello " + firstName,
                $(LabelElement.class).first().getText());
    }

}
