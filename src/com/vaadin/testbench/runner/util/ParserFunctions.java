package com.vaadin.testbench.runner.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

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
     * @throws FactoryConfigurationError
     *             - if the implementation is not available or cannot be
     *             instantiated.
     * @throws ParserConfigurationException
     *             - if a DocumentBuilder cannot be created which satisfies the
     *             configuration requested.
     * @throws IOException
     *             - If any IO errors occur.
     * @throws SAXException
     *             - If any parse errors occur.
     */
    public static ParsedSuite readXmlFile(String file, String path)
            throws Exception {

        // Map<String, Object> result = new HashMap<String, Object>();
        ParsedSuite result = new ParsedSuite();

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

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(testSuite);
        doc.getDocumentElement().normalize();
        // Get <title> node and set title if exists
        NodeList nodeList = doc.getElementsByTagName("title");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            result.setTestName(node.getChildNodes().item(0).getNodeValue());
        }

        // Get <path> node and set path if exists
        nodeList = doc.getElementsByTagName("path");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            result.setPath(node.getChildNodes().item(0).getNodeValue());
        }

        List<String> tests = new LinkedList<String>();
        // Get all <testfile> nodes and create list of tests
        nodeList = doc.getElementsByTagName("testfile");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            tests.add(node.getChildNodes().item(0).getNodeValue());
        }
        result.setSuiteTests(tests);

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
     * @throws UnsupportedOperationException
     *             Html suite contained files other than TestBench .html tests
     */
    public static ParsedSuite readHtmlFile(String file, String path)
            throws Exception {
        // Map<String, Object> result = new HashMap<String, Object>();
        ParsedSuite result = new ParsedSuite();

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
        BufferedReader in = new BufferedReader(new FileReader(testSuite));
        List<String> tests = new LinkedList<String>();
        String line = "";
        try {
            while ((line = in.readLine()) != null) {
                if (line.contains("a href=\"")) {
                    // Get file name and path
                    line = line.substring(line.indexOf("\"") + 1, line
                            .lastIndexOf("\""));
                    // check that .html file
                    if (!line.contains(".html")) {
                        System.err
                                .println("Suite should only consist of TestBench tests.");
                        throw new UnsupportedOperationException(
                                "Only TestBench tests supported in a .html suite file.");
                    }
                    // add test to list
                    tests.add(line);
                } else if (line.contains("<b>")) {
                    line = line.substring(line.indexOf("<b>") + 3, line
                            .lastIndexOf("</b>"));
                    result.setTestName(line);
                }
            }
            result.setSuiteTests(tests);
        } finally {
            in.close();
        }

        return result;
    }

    /**
     * Combine html tests to for one test (only one browser will be opened)
     * 
     * @param testsToCombine
     *            List of tests to combine (Can handle mixed filetypes)
     * @param name
     *            Name of test file (if null filename will be generated)
     * @param path
     *            Path to test files
     * @return List with combined tests
     * @throws Exception
     */
    public static List<String> combineTests(List<String> testsToCombine,
            String name, String path) throws Exception {

        List<String> combinedFiles = new LinkedList<String>();
        List<String> combineThese = new LinkedList<String>();
        Boolean combineNew = false;

        if (name == null) {
            name = "test_" + testsToCombine.hashCode();
        }

        StringBuilder str = new StringBuilder();
        for (int i = 0; i < testsToCombine.size(); i++) {
            String test = testsToCombine.get(i);
            // Get file
            File file = new File(test);
            if (!file.exists()) {
                file = new File(path + test);
                if (!file.exists()) {
                    file = IOFunctions.getFile(test, new File(path), 0);
                }
            }
            if (file == null) {
                throw new FileNotFoundException("Couldn't locate file " + test);
            }

            // if not .html file add to list and combine html tests in
            // combineThese
            if (!file.getName().endsWith(".html")) {
                if (combineNew && !combineThese.isEmpty()) {
                    if (combinedFiles.size() > 1) {
                        combinedFiles.addAll(combineTests(combineThese, null,
                                path));
                    } else {
                        combinedFiles.addAll(combineThese);
                    }
                    combineThese.clear();
                }
                combinedFiles.add(test);
                combineNew = true;
            } else if (!combineNew) {
                // Combine html tests
                BufferedReader in = new BufferedReader(new FileReader(file));
                try {
                    String line = "";
                    // Search file till start
                    do {
                        if (line.equals("</thead><tbody>")) {
                            line = in.readLine();
                            break;
                        }
                    } while ((line = in.readLine()) != null);
                    // Get lines until condition met.
                    if (line != null) {
                        do {
                            if (line.equals("</tbody></table>")) {
                                str.append("<tr>\n<td>htmlTest</td>\n");
                                str.append("<td></td>\n");
                                str.append("<td>" + file.getName()
                                        + "</td>\n</tr>\n");
                                break;
                            }
                            str.append(line + "\n");
                        } while ((line = in.readLine()) != null);
                    }

                } finally {
                    in.close();
                }
            } else {
                // if collecting tests add test to combineThese
                combineThese.add(test);
            }
        }
        // If combineThese has files combine to file
        if (!combineThese.isEmpty()) {
            combinedFiles.addAll(combineTests(combineThese, null, path));
            combineThese.clear();
        }
        // Check that file !exist
        File targetFile = new File(path + name + ".html");
        if (targetFile.exists()
                && !name.equals("test_" + testsToCombine.hashCode())
                && !name.equals("Suite")) {
            targetFile = new File(path + name + testsToCombine.hashCode()
                    + ".html");
        }

        BufferedWriter out = new BufferedWriter(new FileWriter(targetFile));
        try {
            writeHeader(out, name);
            out.write(str.toString());
            // Add combined file in front
            combinedFiles.add(0, name + ".html");
            writeFooter(out);
        } finally {
            out.flush();
            out.close();
        }
        return combinedFiles;
    }

    private static void writeHeader(Writer out, String name) throws Exception {
        out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        out
                .write("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n");
        out
                .write("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">\n");
        out
                .write("<head profile=\"http://selenium-ide.openqa.org/profiles/test-case\">\n");
        out
                .write("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\n");
        out.write("<link rel=\"selenium.base\" href=\"\" />\n");
        out.write("<title>" + name + "</title>\n");
        out.write("</head>\n");
        out.write("<body>\n");
        out.write("<table cellpadding=\"1\" cellspacing=\"1\" border=\"1\">\n");
        out.write("<thead>\n");
        out.write("<tr><td rowspan=\"1\" colspan=\"3\">" + name
                + "</td></tr>\n");
        out.write("</thead><tbody>\n");
    }

    private static void writeFooter(Writer out) throws Exception {
        out.write("</tbody></table>\n");
        out.write("</body>\n");
        out.write("</html>\n");
    }
}
