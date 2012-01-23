package com.vaadin.testbench.tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Ignore;

@Ignore(value = "Magic file. Re-enable when updating test converter for selenium 2")
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
                "public void testwinxp_firefox35() throws Throwable{",
                "private void internal_parameter_demo() throws Throwable {",
                "doCommand(\"assertText\",new String[] {\"//body/div[2]\",\"original text\"});" };

        File target = new File(
                System.getProperty("user.dir")
                        + "/temp/src/parameter_demo/winxp_firefox35/parameter_demo.java");

        Assert.assertTrue("File doesn't exists. Check conversion.",
                target.exists());

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

        Assert.assertTrue("File doesn't exists. Check conversion.",
                target.exists());

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

    public void testverify_environment_parameter_conversion_result()
            throws Exception {
        String[] shouldExist = new String[] {
                "public class parameter_demo extends AbstractVaadinTestCase {",
                "public void setUp(){",
                "public void testwinxp_firefox4() throws Throwable{",
                "private void internal_parameter_demo() throws Throwable {",
                "doCommand(\"assertText\",new String[] {\"//body/div[2]\",\"Text has been replaced from ff36.properties\"});" };

        File target = new File(System.getProperty("user.dir")
                + "/temp/src/parameter_demo/winxp_firefox4/parameter_demo.java");

        Assert.assertTrue("File doesn't exists. Check conversion.",
                target.exists());

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

    public void testsuite_conversion_result() throws Exception {
        String[] shouldExist = new String[] {
                "cmd.setFile(\"OpenSampler.html\");",
                "Start test OpenSampler.html",
                "cmd.setFile(\"GoToCheckBox.html\");",
                "Start test GoToCheckBox.html" };

        File target = new File(System.getProperty("user.dir")
                + "/temp/src/TestSuite/winxp_firefox36/TestSuite.java");

        Assert.assertTrue("File doesn't exists. Check conversion.",
                target.exists());

        BufferedReader in = new BufferedReader(new FileReader(target));
        String line = in.readLine();
        int need = 0;
        String lineNotFound = shouldExist[need];
        while (line != null) {
            if (need < shouldExist.length && line.contains(shouldExist[need])) {
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

    public void testset_current_command() throws Exception {
        String[] shouldExist = new String[] {
                "cmd.setCommand(\"open\", \"/6.3.0/sampler/#TextFieldSingle\", \"\");",
                "cmd.setCommand(\"mouseClick\", \"vaadin=630sampler::/VVerticalLayout[0]/ChildComponentContainer[1]/VSplitPanelHorizontal[0]/VHorizontalLayout[0]/ChildComponentContainer[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[0]/VTextField[0]\", \"58,8\");",
                "cmd.setCommand(\"enterCharacter\", \"vaadin=630sampler::/VVerticalLayout[0]/ChildComponentContainer[1]/VSplitPanelHorizontal[0]/VHorizontalLayout[0]/ChildComponentContainer[0]/VVerticalLayout[0]/ChildComponentContainer[1]/VVerticalLayout[0]/ChildComponentContainer[0]/VTextField[0]\", \"Text has been replaced\");",
                "cmd.setCommand(\"waitForVaadin\", \"\", \"\");",
                "cmd.setCommand(\"assertText\", \"//body/div[2]\", \"Text has been replaced\");",
                "cmd.setCommand(\"closeNotification\", \"//body/div[2]\", \"0,0\");" };

        File target = new File(
                System.getProperty("user.dir")
                        + "/temp/src/parameter_demo/winxp_firefox36/parameter_demo.java");

        Assert.assertTrue("File doesn't exists. Check conversion.",
                target.exists());

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
