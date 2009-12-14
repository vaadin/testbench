package com.vaadin.testbench.runner.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Enumeration;

import junit.framework.TestFailure;
import junit.framework.TestResult;

// TODO: Auto-generated Javadoc
/**
 * The Class IOFunctions.
 */
public class IOFunctions {

    /**
     * Prints the result information for given TestResult.
     * 
     * @param seconds
     *            Time for run in seconds
     * @param result
     *            Result for JUnit tests
     */
    public static void printResult(int seconds, TestResult result) {
        if (seconds > 60) {
            System.out.println("Tests run: " + result.runCount()
                    + ", Failures: " + result.failureCount() + ", Errors: "
                    + result.errorCount() + ", Time elapsed: " + (seconds / 60)
                    + " min " + (seconds % 60) + " sec");
        } else {
            System.out.println("Tests run: " + result.runCount()
                    + ", Failures: " + result.failureCount() + ", Errors: "
                    + result.errorCount() + ", Time elapsed: " + seconds
                    + " sec");
        }
        Enumeration<TestFailure> r = result.failures();
        while (r.hasMoreElements()) {
            System.out.println(r.nextElement().toString());
        }
        r = result.errors();
        while (r.hasMoreElements()) {
            System.out.println(r.nextElement().toString());
        }
    }

    /**
     * Create build.xml that can be used to run testSuite
     * 
     * @param dir
     *            Directory for this testSuite
     * @param testName
     *            Test suite name
     */
    public static void buildAntFile(File dir, String testName) {

        try {
            File build = new File(dir.getPath() + File.separator + "build.xml");
            if (build.exists()) {
                System.out.println("build.xml exists using " + testName
                        + ".xml");
                build = new File(dir.getPath() + File.separator + testName
                        + ".xml");
            }
            // Create build.xml
            BufferedWriter out = new BufferedWriter(new FileWriter(build));

            out.write("<?xml version=\"1.0\"?>\n");
            out.write("<project name=\"" + testName
                    + "\" basedir=\".\" default=\"testsuite\">\n\n");
            out.write("<property name=\"class-dir\" value=\"build\" />\n");
            if (System.getProperty("com.vaadin.testbench.tester.host") != null) {
                out
                        .write("<property name=\"com.vaadin.testbench.tester.host\" value=\""
                                + System
                                        .getProperty("com.vaadin.testbench.tester.host")
                                + "\" />\n");
            }
            if (System.getProperty("com.vaadin.testbench.deployment.url") != null) {
                out
                        .write("<property name=\"com.vaadin.testbench.deployment.url\" value=\""
                                + System
                                        .getProperty("com.vaadin.testbench.deployment.url")
                                + "\" />\n");
            }
            if (System.getProperty("com.vaadin.testbench.screenshot.directory") != null) {
                out
                        .write("<property name=\"com.vaadin.testbench.screenshot.directory\" value=\""
                                + System
                                        .getProperty("com.vaadin.testbench.screenshot.directory")
                                + "\" />\n");
            }
            out
                    .write("<property name=\"lib.dir\" value=\"${com.vaadin.testbench.lib.dir}\" />\n\n");

            out.write("<target name=\"compile\">\n");
            out.write("<mkdir dir=\"${class-dir}\" />\n");
            out.write("<javac debug=\"true\" destdir=\"${class-dir}\">\n");
            out.write("<src path=\"${basedir}\" />\n");
            out.write("<classpath>\n");
            out.write("<fileset dir=\"${lib.dir}\" includes=\"**/*.jar\" />\n");
            out.write("</classpath>\n");
            out.write("</javac>\n");
            out.write("</target>\n\n");

            out.write("<path id=\"classpath.test\">\n");
            out.write("<path path=\"${java.class.path}\" />\n");
            out.write("<pathelement location=\"${class-dir}\" />\n");
            out.write("<fileset dir=\"${lib.dir}\" includes=\"**/*.jar\" />\n");
            out.write("<path location=\"${class-dir}\" />\n");
            out.write("</path>\n\n");

            out.write("<target name=\"testsuite\" depends=\"compile\">\n");
            out.write("<junit haltonfailure=\"true\" fork=\"yes\">\n");
            out.write("<classpath refid=\"classpath.test\" />\n");
            out.write("<formatter type=\"brief\" usefile=\"false\" />\n");
            out
                    .write("<jvmarg value=\"-Dcom.vaadin.testbench.tester.host=${com.vaadin.testbench.tester.host}\" />\n");
            out
                    .write("<jvmarg value=\"-Dcom.vaadin.testbench.deployment.url=${com.vaadin.testbench.deployment.url}\" />\n");
            out
                    .write("<jvmarg value=\"-Dcom.vaadin.testbench.screenshot.directory=${com.vaadin.testbench.screenshot.directory}\" />\n");
            out.write("<jvmarg value=\"-Djava.awt.headless=true\" />\n");
            out
                    .write("<jvmarg value=\"-Dcom.vaadin.testbench.screenshot.softfail=${com.vaadin.testbench.screenshot.softfail}\" />\n");
            out
                    .write("<jvmarg value=\"-Dcom.vaadin.testbench.screenshot.reference.debug=${com.vaadin.testbench.screenshot.reference.debug}\" />\n");
            out.write("<batchtest fork=\"yes\">\n");
            out
                    .write("<fileset dir=\"${class-dir}\" includes=\"**/*_Suite.class\"/>\n");
            out.write("</batchtest>\n");
            out.write("</junit>\n");
            out.write("</target>\n");

            out.write("</project>\n");

            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Does a small search for file test from directory buildPath
     * 
     * @param test
     *            Name of file to be found
     * @param buildPath
     *            Path from where to search
     * @return File if found, null if not found
     */
    public static File getFile(String test, File buildPath, int depth) {
        File found = null;
        if (!buildPath.isDirectory() || depth == 10) {
            return found;
        }

        try {
            for (File file : buildPath.listFiles()) {
                if (file.isDirectory()) {
                    found = getFile(test, file, depth++);
                    if (found != null) {
                        return found;
                    }
                } else if (file.isFile()) {
                    if (file.getName().equals(test)) {
                        return file;
                    }
                }
            }
        } catch (NullPointerException npe) {
            System.err.println("Not nullpointer exception with message "
                    + npe.getMessage());
            System.err.println("Trying to continue search.");
            return null;
        }
        return found;
    }

    /**
     * Copy file.
     * 
     * @param in
     *            File to copy
     * @param out
     *            Target file
     * 
     * @throws IOException
     *             I/O error during execution
     */
    public static void copyFile(File in, File out) throws IOException {
        FileChannel inChannel = new FileInputStream(in).getChannel();
        FileChannel outChannel = new FileOutputStream(out).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } catch (IOException e) {
            throw e;
        } finally {
            if (inChannel != null) {
                inChannel.close();
            }
            if (outChannel != null) {
                outChannel.close();
            }
        }
    }
}
