package com.devstomper.account_transfer;


import com.devstomper.account_transfer.dao.GenericDao;
import com.devstomper.account_transfer.dao.impl.AccountH2DAO;
import com.devstomper.account_transfer.model.Account;
import com.devstomper.account_transfer.model.Statement;
import org.apache.commons.dbutils.DbUtils;
import org.h2.tools.RunScript;
import org.junit.*;

import java.io.FileReader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import static com.devstomper.account_transfer.ConnectionManager.getConnection;
import static com.devstomper.account_transfer.PropertiesManager.loadConfiguration;
import static com.devstomper.account_transfer.model.Statement.LOCK;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * AccountH2DAO Test
 * @author asinitsyn
 * Date: 23.10.2018
 */
public class TestAccountH2DAO {

    private GenericDao<Account> dao = new AccountH2DAO();

    @BeforeClass
    public static void setUp() throws Exception {
        loadConfiguration("application.properties");
        new ConnectionManager();
        RunScript.execute(getConnection(), new FileReader("src/test/resources/test_data.sql"));
    }

    @AfterClass
    public static void tearDown() {
    }

    @Test
    public void shouldRetrieveAllAccounts() throws Exception {
        List<Account> allAccounts = dao.retrieveAll();
        assertTrue(allAccounts.size() == 5);
        assertEquals(1, allAccounts.get(0).getId());
        assertEquals(BigDecimal.valueOf(100).setScale(2, BigDecimal.ROUND_UP), allAccounts.get(0).getBalance());
    }

    @Test
    public void shouldRetrieveNullForNonExistingAccountByID() throws Exception {
        Account account = dao.retrieve("10");
        assertTrue(account == null);
    }

    @Test
    public void shouldRetrieveAccountByID() throws Exception {
        Account account = dao.retrieve("2");
        assertEquals(2, account.getId());
        assertEquals(BigDecimal.valueOf(200).setScale(2, BigDecimal.ROUND_UP), account.getBalance());
    }

    @Test
    public void shouldUpdateAccount() throws Exception {
        long id = 3;
        Account account = new Account(id, BigDecimal.valueOf(600));
        dao.update(account);
        Account updatedAccount = dao.retrieve(String.valueOf(id));
        assertEquals(account.getBalance().setScale(2, BigDecimal.ROUND_UP), updatedAccount.getBalance());
    }

    @Test
    public void shouldNotUpdateAccountWhileItIsLocked() throws Exception {
        long id = 3;
        Account originalAccount = dao.retrieve(String.valueOf(id));
        Connection connection = getConnection();
        try (PreparedStatement statement = connection.prepareStatement(Statement.valueOf(LOCK))) {
            connection.setAutoCommit(false);
            statement.setLong(1, id);
            statement.executeQuery();
            Account accountToUpdate = new Account(id, BigDecimal.valueOf(600));
            dao.update(accountToUpdate);
        } catch (Exception e) {
            connection.commit();
            assertTrue(originalAccount.getBalance().equals(dao.retrieve(String.valueOf(id)).getBalance()));
            DbUtils.closeQuietly(connection);
        }
    }
}
