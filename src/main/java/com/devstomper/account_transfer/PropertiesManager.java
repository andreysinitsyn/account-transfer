package com.devstomper.account_transfer;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Properties Manager
 * @author asinitsyn
 * Date: 24.10.2018
 */
public class PropertiesManager {

    private static Properties properties = new Properties();

    private static Logger log = LoggerFactory.getLogger(PropertiesManager.class);

    /**
     * Loads configuration using file with properties
     * @param fileName name of a file with properties
     * @throws Exception in case of a file loading error
     */
    static void loadConfiguration(String fileName) throws Exception {
        try {
            log.info("Initializing property file: " + fileName);
            InputStream fis = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
            properties.load(fis);
            log.info("Property file " + fileName + " has been successfully loaded.");
        } catch (IOException e) {
            throw new Exception("Failed to load " + fileName + " file: ", e);
        }
    }


    static String getProperty(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            value = System.getProperty(key);
        }
        return value;
    }


}
