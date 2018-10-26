package com.devstomper.account_transfer;


import org.glassfish.jersey.server.ResourceConfig;


/**
 * Servlet custom configuration
 * @author asinitsyn
 * Date: 24.10.2018
 */
class ServletConfiguration extends ResourceConfig {

    ServletConfiguration() {
        packages(true, "com.devstomper.account_transfer");
        register(new AccountingServiceBinder());
    }
}
