package com.vaadin.testbench.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import com.vaadin.testbench.Parameters;

/**
 * ParameterUtil is a singleton class to handle Properties file read and
 * returning of changing parameters if available
 */
public class ParameterUtil {

    private static ParameterUtil _parameter = null;
    private Properties properties = null;

    protected ParameterUtil() {
        // Exists so class can't be instantiated.
    }

    public static ParameterUtil getInstance() {
        if (_parameter == null) {
            _parameter = new ParameterUtil();
            _parameter.initParameter();
        }

        return _parameter;
    }

    /**
     * Initialize properties and load properties file if available
     */
    private void initParameter() {
        properties = new Properties();

        if (Parameters.hasParameterFile()) {
            try {
                FileInputStream in = new FileInputStream(Parameters
                        .getParameterFile());
                properties.load(in);
                in.close();
            } catch (IOException ioe) {
                System.err.println("Properties file "
                        + Parameters.getParameterFile() + " was not found");
            }
        }
    }

    /**
     * Return string for key. Else return key
     * 
     * @param key
     *            Key to search for
     * @return Property for key. If key not found return key.
     */
    public String get(String key) {
        return properties.getProperty(key, key);
    }

    /**
     * Return string for key. Else return default value
     * 
     * @param key
     *            Key to search for
     * @return Property for key. If key not found return default value.
     */
    public String get(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
}
