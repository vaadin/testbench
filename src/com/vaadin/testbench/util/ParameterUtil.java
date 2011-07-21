package com.vaadin.testbench.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.vaadin.testbench.Parameters;

/**
 * ParameterUtil is a singleton class to handle Properties file read and
 * returning of changing parameters if available
 */
public class ParameterUtil {

    private static ParameterUtil _parameter = null;
    private Properties properties = null;
    private Map<String, Properties> properties_map = new HashMap<String, Properties>();

    private ParameterUtil() {
        // Exists so class can't be instantiated.
    }

    public static ParameterUtil getInstance() {
        return _parameter;
    }

    public static void init() throws FileNotFoundException {
        if (_parameter == null) {
            _parameter = new ParameterUtil();
            _parameter.initParameter();
        }

    }

    /**
     * Initialize properties and load properties file if available
     * 
     * @throws FileNotFoundException
     * 
     * @throws RuntimeException
     *             If the properties file was defined but not found
     */
    private void initParameter() throws FileNotFoundException {
        properties = new Properties();

        if (Parameters.hasParameterFile()) {
            try {
                FileInputStream in = new FileInputStream(
                        Parameters.getParameterFile());
                properties.load(in);
                in.close();
            } catch (IOException ioe) {
                throw new FileNotFoundException("Properties file "
                        + Parameters.getParameterFile() + " was not found");
            }

            getEnviromentProperties();
        }
    }

    private void getEnviromentProperties() {
        File properties = new File(Parameters.getParameterFile());
        File directory = new File(properties.getParent());

        final String baseName = properties.getName().substring(0,
                properties.getName().indexOf("."));

        if (directory.isDirectory()) {
            FilenameFilter filter = new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.startsWith(baseName)
                            && name.endsWith(".properties");
                }
            };
            File[] files = directory.listFiles(filter);

            for (File file : files) {
                String fileName = file.getName();
                if (fileName.contains("_")) {
                    addPropertyToMap(file);
                }
            }
        }
    }

    private void addPropertyToMap(File file) {
        String fileName = file.getName();
        try {
            FileInputStream in = new FileInputStream(file);
            Properties prop = new Properties();
            prop.load(in);
            in.close();

            String packageName = fileName.substring(fileName.indexOf("_") + 1,
                    fileName.lastIndexOf("."));

            properties_map.put(packageName, prop);
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
        }
    }

    /**
     * Return string for key. Else return key
     * 
     * @param key
     *            Key to search for
     * @return Property for key. If key not found return key.
     */
    private String get(String key) {
        return get(key, key);
    }

    /**
     * Return string for key. Else return default value
     * 
     * @param key
     *            Key to search for
     * @return Property for key. If key not found return default value.
     */
    private String get(String key, String defaultValue) {
        // Properties is based on a HashTable that can't handle null values
        if (key == null) {
            return defaultValue;
        }

        return properties.getProperty(key, defaultValue);
    }

    private String get(String key, String defaultValue, String pack) {
        if (key == null) {
            return defaultValue;
        }

        String parameterized = properties.getProperty(key, defaultValue);

        if (properties_map.containsKey(pack)) {
            String packParameter = properties_map.get(pack).getProperty(key,
                    defaultValue);

            if (!packParameter.equals(defaultValue)) {
                parameterized = packParameter;
            }
        }

        return parameterized;
    }

    public static String translate(String value) {
        return getInstance().get(value);
    }

    /**
     * Returns string for key in given environment if properties file for
     * environment exists, else returns property from base property if exists.
     * 
     * @param value
     *            Parameter value
     * @param environment
     *            Property environment to check
     * @return Parameterized string if exists.
     */
    public static String translate(String value, String environment) {
        return getInstance().get(value, value, environment);
    }

}
