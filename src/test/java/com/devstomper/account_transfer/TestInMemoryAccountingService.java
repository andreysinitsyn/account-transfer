package com.devstomper.account_transfer;

import com.devstomper.account_transfer.model.Account;
import com.devstomper.account_transfer.model.TransactionResult;
import com.devstomper.account_transfer.model.TransactionStatus;
import com.devstomper.account_transfer.model.TransferTransaction;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.glassfish.jersey.servlet.ServletContainer;
import org.h2.tools.RunScript;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

import static com.devstomper.account_transfer.ConnectionManager.getConnection;
import static com.devstomper.account_transfer.PropertiesManager.loadConfiguration;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Controller Test
 * @author asinitsyn
 * Date: 23.10.2018
 */
public class TestInMemoryAccountingService  {

    private static Tomcat server;
    private static HttpClient client;
    private ObjectMapper mapper = new ObjectMapper();

    @BeforeClass
    public static void setup() throws Exception {
        loadConfiguration("application.properties");
        initDbConnection();
        startServer();
        initClient();
    }

    @Test
    public void shouldRetrieve200WhenHandleRetrieveAllAccountsRequest() throws Exception {
        URI uri = new URI("http://localhost:8081/api/accounts/all");
        HttpGet request = new HttpGet(uri);
        HttpResponse response = client.execute(request);
        assertEquals(200, response.getStatusLine().getStatusCode());
        String content = EntityUtils.toString(response.getEntity());
        List<Account> accounts = mapper.readValue(content, new TypeReference<List<Account>>() {
        });
        assertTrue(accounts.size() == 5);
        assertEquals(1, accounts.get(0).getId());
        assertEquals(BigDecimal.valueOf(100).setScale(2, BigDecimal.ROUND_UP), accounts.get(0).getBalance());
    }

    @Test
    public void shouldRetrieve200WhenHandleRetrieveAccountByExistingIdRequest() throws Exception {
        URI uri = new URI("http://localhost:8081/api/accounts/2");
        HttpGet request = new HttpGet(uri);
        HttpResponse response = client.execute(request);
        assertEquals(200, response.getStatusLine().getStatusCode());
        String content = EntityUtils.toString(response.getEntity());
        Account account = mapper.readValue(content, Account.class);
        assertEquals(2, account.getId());
        assertEquals(BigDecimal.valueOf(200).setScale(2, BigDecimal.ROUND_UP), account.getBalance());
    }

    @Test
    public void shouldRetrieve404WhenHandleRetrieveAccountByNonExistingIdRequest() throws Exception {
        URI uri = new URI("http://localhost:8081/api/accounts/10");
        HttpGet request = new HttpGet(uri);
        HttpResponse response = client.execute(request);
        assertEquals(404, response.getStatusLine().getStatusCode());
        String content = EntityUtils.toString(response.getEntity());
        assertEquals("", content);
    }

    @Test
    public void shouldRetrieve200WithSuccessTransactionResultWhenTransferWithSufficientBalance() throws Exception {
        URI uri = new URI("http://localhost:8081/api/accounts/transfer");
        TransferTransaction transferTransaction = new TransferTransaction("3", "4", BigDecimal.valueOf(100));
        String body = mapper.writeValueAsString(transferTransaction);
        StringEntity entity = new StringEntity(body);
        HttpPut request = new HttpPut(uri);
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);
        HttpResponse response = client.execute(request);
        assertEquals(200, response.getStatusLine().getStatusCode());
        String content = EntityUtils.toString(response.getEntity());
        TransactionResult result = mapper.readValue(content, TransactionResult.class);
        assertEquals(TransactionStatus.SUCCESS.getCode(), result.getCode());
        assertEquals(TransactionStatus.SUCCESS.name(), result.getStatusName());
    }

    @Test
    public void shouldRetrieve200WithFailTransactionResultWhenTransferWithInsufficientBalance() throws Exception {
        URI uri = new URI("http://localhost:8081/api/accounts/transfer");
        TransferTransaction transferTransaction = new TransferTransaction("5",
                "4", BigDecimal.valueOf(700).setScale(2, BigDecimal.ROUND_UP));
        String body = mapper.writeValueAsString(transferTransaction);
        StringEntity entity = new StringEntity(body);
        HttpPut request = new HttpPut(uri);
        request.setHeader("Content-type", "application/json");
        request.setEntity(entity);
        HttpResponse response = client.execute(request);
        assertEquals(200, response.getStatusLine().getStatusCode());
        String content = EntityUtils.toString(response.getEntity());
        TransactionResult result = mapper.readValue(content, TransactionResult.class);
        assertEquals(TransactionStatus.FAIL.getCode() , result.getCode());
        assertEquals(TransactionStatus.FAIL.name(), result.getStatusName());
        assertEquals("Not enough money to complete transfer" +
                " operation. Current balance: 300.00, Requested amount: 700.00", result.getMessage());
    }

    @AfterClass
    public static void stop() throws Exception {
        server.stop();
        HttpClientUtils.closeQuietly(client);
    }

    private static void startServer() throws Exception {
        server = new Tomcat();
        server.setPort(8081);
        Context context = server.addContext("", new File(".").getAbsolutePath());
        Tomcat.addServlet(context, "accounting", new ServletContainer(new ServletConfiguration()));
        context.addServletMappingDecoded("/api/*", "accounting");
        server.start();
    }


    private static void initDbConnection() throws Exception {
        new ConnectionManager();
        RunScript.execute(getConnection(), new FileReader("src/test/resources/test_data.sql"));
    }

    private static void initClient() throws Exception {
        PoolingHttpClientConnectionManager clientConnectionManager = new PoolingHttpClientConnectionManager();
        clientConnectionManager.setDefaultMaxPerRoute(100);
        clientConnectionManager.setMaxTotal(200);
        client = HttpClients.custom()
                .setConnectionManager(clientConnectionManager)
                .setConnectionManagerShared(true)
                .build();
    }
}
