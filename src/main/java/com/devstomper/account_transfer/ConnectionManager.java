package com.devstomper.account_transfer;


import org.apache.commons.dbutils.DbUtils;
import org.h2.tools.RunScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * H2DB Connection Manager
 * @author asinitsyn
 * Date: 24.10.2018
 */
public class ConnectionManager {

    private static final String H2_DRIVER = PropertiesManager.getProperty("h2.driver");
    private static final String URL = PropertiesManager.getProperty("h2.url");
    private static final String TEST_DATA_FILE = PropertiesManager.getProperty("test.data");

    private static Logger log = LoggerFactory.getLogger(PropertiesManager.class);

    ConnectionManager() {
        DbUtils.loadDriver(H2_DRIVER);
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    void loadTestData() throws Exception {
        try (Connection connection = getConnection()) {
            log.info("Loading test data...");
            String outputFile = TEST_DATA_FILE;
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(TEST_DATA_FILE);
            Files.copy(is, Paths.get(outputFile));
            File file = new File(outputFile);
            RunScript.execute(connection, new FileReader(file));
        } catch (SQLException | FileNotFoundException e) {
            throw new Exception("Failed to load test data: ", e);
        }
    }

}
