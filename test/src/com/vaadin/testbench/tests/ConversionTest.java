package com.vaadin.testbench.tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import junit.framework.TestCase;

import org.junit.Assert;

public class ConversionTest extends TestCase {

    /**
     * Check that Conversion created a file with required methods
     * 
     * @throws Exception
     */
    public void testverify_conversion_result() throws Exception {
        String[] shouldExist = new String[] {
                "public class parameter_demo extends AbstractVaadinTestCase {",
                "public void setUp(){",
                "public void testwinxp_firefox3() throws Throwable{",
                "private void internal_parameter_demo() throws Throwable {",
                "doCommand(\"assertText\",new String[] {\"//body/div[2]\",\"original text\"});" };

        File target = new File(System.getProperty("user.dir")
                + "/temp/src/parameter_demo/winxp_firefox3/parameter_demo.java");

        Assert.assertTrue("File doesn't exists. Check conversion.", target
                .exists());

        BufferedReader in = new BufferedReader(new FileReader(target));
        String line = in.readLine();
        int need = 0;
        String lineNotFound = shouldExist[need];
        while (line != null) {
            if (need < shouldExist.length && line.equals(shouldExist[need])) {
                need++;
                if (need < shouldExist.length) {
                    lineNotFound = shouldExist[need];
                }
            }
            line = in.readLine();
        }

        Assert.assertEquals("Didn't find expected line " + lineNotFound,
                shouldExist.length, need);
    }

    /**
     * Check that Conversion created a file with required methods and that value
     * replacement worked
     * 
     * @throws Exception
     */
    public void testverify_parameter_conversion_result() throws Exception {
        String[] shouldExist = new String[] {
                "public class parameter_demo extends AbstractVaadinTestCase {",
                "public void setUp(){",
                "public void testwinxp_firefox36() throws Throwable{",
                "private void internal_parameter_demo() throws Throwable {",
                "doCommand(\"assertText\",new String[] {\"//body/div[2]\",\"Text has been replaced\"});" };

        File target = new File(
                System.getProperty("user.dir")
                        + "/temp/src/parameter_demo/winxp_firefox36/parameter_demo.java");

        Assert.assertTrue("File doesn't exists. Check conversion.", target
                .exists());

        BufferedReader in = new BufferedReader(new FileReader(target));
        String line = in.readLine();
        int need = 0;
        String lineNotFound = shouldExist[need];
        while (line != null) {
            if (need < shouldExist.length && line.equals(shouldExist[need])) {
                need++;
                if (need < shouldExist.length) {
                    lineNotFound = shouldExist[need];
                }
            }
            line = in.readLine();
        }

        Assert.assertEquals("Didn't find expected line " + lineNotFound,
                shouldExist.length, need);
    }
}
