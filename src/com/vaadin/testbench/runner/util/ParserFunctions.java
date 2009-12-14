package com.vaadin.testbench.runner.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ParserFunctions {

    /**
     * Parse xml suite file.
     * 
     * @param file
     *            Xml suite file
     * @param path
     *            Base path
     * 
     * @return the map< String, Object> with possible keys 'title', 'path' and
     *         'tests'
     * 
     * @throws FileNotFoundException
     *             the target file was not found
     */
    public static Map<String, Object> readXmlFile(String file, String path)
            throws FileNotFoundException {

        Map<String, Object> result = new HashMap<String, Object>();

        if (path == null) {
            path = System.getProperty("user.dir");
        }
        // Check that path ends with fileseparator token for later use.
        if (!File.separator.equals(path.charAt(path.length() - 1))) {
            path = path + File.separator;
        }

        File testSuite = new File(file);
        if (!testSuite.exists()) {
            testSuite = new File(path + file);
            if (!testSuite.exists()) {
                // If not found do a small search for file
                testSuite = IOFunctions.getFile(testSuite.getName(), testSuite
                        .getParentFile(), 0);
            }
        }

        if (testSuite == null) {
            throw new FileNotFoundException("Could not find file " + file);
        }

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(testSuite);
            doc.getDocumentElement().normalize();
            // Get <title> node and set title if exists
            NodeList nodeList = doc.getElementsByTagName("title");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                result
                        .put("title", node.getChildNodes().item(0)
                                .getNodeValue());
            }

            // Get <path> node and set path if exists
            nodeList = doc.getElementsByTagName("path");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                result.put("path", node.getChildNodes().item(0).getNodeValue());
            }

            List<String> tests = new LinkedList<String>();
            // Get all <testfile> nodes and create list of tests
            nodeList = doc.getElementsByTagName("testfile");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                tests.add(node.getChildNodes().item(0).getNodeValue());
            }
            result.put("tests", tests);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Parse html suite file.
     * 
     * @param file
     *            Html suite file
     * @param path
     *            Base Path
     * 
     * @return map< String, Object> with possible keys 'title' and 'tests'
     * 
     * @throws FileNotFoundException
     *             the target file was not found
     */
    public static Map<String, Object> readHtmlFile(String file, String path)
            throws FileNotFoundException {
        Map<String, Object> result = new HashMap<String, Object>();

        if (path == null) {
            path = System.getProperty("user.dir");
        }
        // Check that path ends with fileseparator token for later use.
        if (!File.separator.equals(path.charAt(path.length() - 1))) {
            path = path + File.separator;
        }

        File testSuite = new File(file);
        if (!testSuite.exists()) {
            testSuite = new File(path + file);
            if (!testSuite.exists()) {
                // If not found do a small search for file
                testSuite = IOFunctions.getFile(testSuite.getName(), testSuite
                        .getParentFile(), 0);
            }
        }

        if (testSuite == null) {
            throw new FileNotFoundException("Could not find file " + file);
        }

        // not the best possible html parser. but it works for IDE test
        // suite
        try {
            BufferedReader in = new BufferedReader(new FileReader(testSuite));
            List<String> tests = new LinkedList<String>();
            String line = "";
            while ((line = in.readLine()) != null) {
                if (line.contains("a href=\"")) {
                    line = line.substring(line.indexOf("\"") + 1, line
                            .lastIndexOf("\""));
                    tests.add(line);
                } else if (line.contains("<b>")) {
                    line = line.substring(line.indexOf("<b>") + 3, line
                            .lastIndexOf("</b>"));
                    result.put("title", line);
                }
            }
            result.put("tests", tests);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

}
