package com.vaadin.testbench.util;

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
                testSuite = getFile(testSuite.getName(),
                        testSuite.getParentFile(), 0);
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

        // Check given path if found in xml file
        if (result.getPath() != null
                && getFile(tests.get(0),
                        new File(testSuite.getParentFile().getAbsolutePath()
                                + File.separator + result.getPath()), 0) == null) {
            System.err.println("Path definition in " + file
                    + " seems to be faulty.\nIgnoring given path "
                    + result.getPath());
            result.setPath(null);
        }

        // Check that all test files are found
        List<String> confirmedTests = new LinkedList<String>();
        for (String test : tests) {
            File testFile = new File(test);
            if (!testFile.exists()) {
                if (result.getPath() != null) {
                    testFile = new File(testSuite.getParentFile()
                            .getAbsolutePath()
                            + File.separator
                            + result.getPath() + File.separator + test);
                } else {
                    testFile = new File(testSuite.getParentFile()
                            .getAbsolutePath() + File.separator + test);
                }
                if (!testFile.exists()) {
                    System.out.println("Resorting to search in suite "
                            + testSuite.getName() + " for "
                            + testFile.getName());
                    // If not found do a small search for file
                    testFile = getFile(testFile.getName(),
                            testFile.getParentFile(), 0);
                }
            }

            if (testFile == null) {
                throw new FileNotFoundException("Could not find file " + test);
            }

            // add full path to test file to Suite tests
            confirmedTests.add(testFile.getAbsolutePath());
        }
        result.setSuiteTests(confirmedTests);

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
                testSuite = getFile(testSuite.getName(),
                        testSuite.getParentFile(), 0);
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
                    line = line.substring(line.indexOf("\"") + 1,
                            line.lastIndexOf("\""));
                    // check that .html file
                    if (!line.contains(".html")) {
                        System.err
                                .println("Suite should only consist of TestBench tests.");
                        throw new UnsupportedOperationException(
                                "Only TestBench tests supported in a .html suite file.");
                    }
                    File testFile = new File(line);
                    if (!testFile.exists()) {
                        testFile = new File(testSuite.getParentFile()
                                .getAbsolutePath() + File.separator + line);
                        if (!testFile.exists()) {
                            System.out.println("Resorting to search in suite "
                                    + testSuite.getName() + " for "
                                    + testFile.getName());
                            // If not found do a small search for file
                            testFile = getFile(testFile.getName(),
                                    testFile.getParentFile(), 0);
                        }
                    }

                    if (testFile == null) {
                        throw new FileNotFoundException("Could not find file "
                                + line);
                    }

                    // add test to list
                    tests.add(testFile.getAbsolutePath());
                } else if (line.contains("<b>")) {
                    line = line.substring(line.indexOf("<b>") + 3,
                            line.lastIndexOf("</b>"));
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
                    file = getFile(test, new File(path), 0);
                }
            }

            if (file == null) {
                throw new FileNotFoundException(
                        "Combine test couldn't locate file " + test);
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
                combineHTMLTests(str, file);
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
        // if (!name.endsWith("" + testsToCombine.hashCode())) {
        // name = name + "_" + testsToCombine.hashCode();
        // }
        // File targetFile = new File(path + name + ".html");
        File targetFile = new File(System.getProperty("java.io.tmpdir")
                + File.separator + name + ".html");
        // if (targetFile.exists()
        // && !name.equals("test_" + testsToCombine.hashCode())
        // && !name.equals("Suite")) {
        // targetFile = new File(path + name + testsToCombine.hashCode()
        // + ".html");
        // }

        BufferedWriter out = new BufferedWriter(new FileWriter(targetFile));
        try {
            writeHeader(out, name);
            out.write(str.toString());
            // Add combined file in front
            combinedFiles.add(0, targetFile.getAbsolutePath());// name +
            // ".html");
            writeFooter(out);
        } finally {
            out.flush();
            out.close();
        }
        return combinedFiles;
    }

    private static void writeHeader(Writer out, String name) throws Exception {
        out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        out.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n");
        out.write("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">\n");
        out.write("<head profile=\"http://selenium-ide.openqa.org/profiles/test-case\">\n");
        out.write("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\n");
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

    private static void combineHTMLTests(StringBuilder str, File file)
            throws Exception {

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

            // add name at start of file
            str.append("<tr>\n<td>htmlTest</td>\n");
            str.append("<td></td>\n");
            str.append("<td>" + file.getName() + "</td>\n</tr>\n");
            // Get lines until condition met.
            if (line != null) {
                do {
                    if (line.equals("</tbody></table>")) {
                        break;
                    }
                    str.append(line + "\n");
                } while ((line = in.readLine()) != null);
            }
        } finally {
            in.close();
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
        if (buildPath == null) {
            System.err.println("Path was null.");
            return null;
        }
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
            System.err.println("Got nullpointer exception with message: "
                    + npe.getMessage());
            System.err.println("Continuing search.");
            return null;
        }
        return found;
    }

}
