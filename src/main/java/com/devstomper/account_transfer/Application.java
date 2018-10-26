package com.devstomper.account_transfer;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static com.devstomper.account_transfer.PropertiesManager.loadConfiguration;

/**
 * Bank Accounting Application
 * @author asinitsyn
 * Date: 24.10.2018
 */
public class Application {

    private static final String CONTEXT_PATH = "";
    private static final String BASE_DIRECTORY_PATH = ".";
    private static final String SERVLET_NAME = "accounting";
    private static final String URL_PATTERN = "/api/*";

    private static Logger log = LoggerFactory.getLogger(PropertiesManager.class);


    /**
     * Application entering point
     * @param args parameters
     */
    public static void main(String[] args) {
        try {
            initConfiguration();
            initDbConnection();
            initServlet();
        } catch (Exception e) {
            log.error("Failed to start application: ", e);
        }
    }

    private static void initConfiguration() throws Exception {
        loadConfiguration("application.properties");
    }

    private static void initDbConnection() throws Exception {
        ConnectionManager connectionManager = new ConnectionManager();
        connectionManager.loadTestData();
    }

    private static void initServlet() throws LifecycleException {
        Tomcat tomcat = new Tomcat();
        String port = PropertiesManager.getProperty("tomcat.port");
        tomcat.setPort(Integer.parseInt(port));
        Context context = tomcat.addContext(CONTEXT_PATH, new File(BASE_DIRECTORY_PATH).getAbsolutePath());
        Tomcat.addServlet(context, SERVLET_NAME, new ServletContainer(new ServletConfiguration()));
        context.addServletMappingDecoded(URL_PATTERN, SERVLET_NAME);
        tomcat.start();
        log.info("Bank Accounting app has been successfully started!");
        tomcat.getServer().await();
    }
}
