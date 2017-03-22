package com.vaadin.testbench.licensechecker;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.vaadin.testbench.tools.LicenseChecker;

public class LicenseCheckerTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final static String DEVELOPER_KEY = "0282c076-d6e1-4a11-9092-d36285b5a6f9";
    private final static String SUBSCRIPTION_KEY_EXPIRED = "9fa746f1-028e-4f02-823a-d022c29119e2";
    private final static String INVALID_KEY = "1111111111111111111111111111";
    private static String NEW_LINE = System.getProperty("line.separator");

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @Test
    public void testDeveloperLicense() {
        Properties props = System.getProperties();
        props.setProperty("vaadin.testbench.developer.license", DEVELOPER_KEY);
        LicenseChecker.nag();
        String output = outContent.toString();
        // output =
        // "-------------Vaadin TestBench 4 registered to dmitrii@vaadin.com
        // (Pro Tools subscription).------------------";
        String pattern = "-*" + NEW_LINE + "Vaadin TestBench 4 registered to .*"
                + NEW_LINE + "-*(" + NEW_LINE + ")*";
        Assert.assertTrue(output.matches(pattern));
    }

    @Test
    public void testSubscriptionExpiredLicense() {
        Properties props = System.getProperties();
        props.setProperty("vaadin.testbench.developer.license",
                SUBSCRIPTION_KEY_EXPIRED);
        String expectingMessage = "----------------------------------------------------------------------------------"
                + NEW_LINE
                + "License for Vaadin TestBench 4 has expired. Get a valid license at vaadin.com/pro"
                + NEW_LINE
                + "----------------------------------------------------------------------------------"
                + NEW_LINE;

        exception.expect(Error.class);
        exception.expectMessage(expectingMessage);
        LicenseChecker.nag();
    }

    @Ignore("Test fails if you have a file vaadin.testbench.developer.license in home dir")
    @Test
    public void testNoLicense() {
        Properties props = System.getProperties();
        props.setProperty("vaadin.testbench.developer.license", "");
        String expectingMessage = "---------------------------------------------------------------------------------"
                + NEW_LINE
                + "License for Vaadin TestBench 5 not found. Go to vaadin.com/pro for more details."
                + NEW_LINE
                + "---------------------------------------------------------------------------------"
                + NEW_LINE;
        exception.expect(Error.class);
        exception.expectMessage(expectingMessage);
        LicenseChecker.nag();
    }

    @Test
    public void testInvalidLicense() {
        Properties props = System.getProperties();
        props.setProperty("vaadin.testbench.developer.license", INVALID_KEY);
        String expectingMessage = "-------------------------------------------------------------------------------------"
                + NEW_LINE
                + "License for Vaadin TestBench 5 is not valid. Get a valid license from vaadin.com/pro"
                + NEW_LINE
                + "-------------------------------------------------------------------------------------"
                + NEW_LINE;
        exception.expect(Error.class);
        exception.expectMessage(expectingMessage);
        LicenseChecker.nag();
    }
}
