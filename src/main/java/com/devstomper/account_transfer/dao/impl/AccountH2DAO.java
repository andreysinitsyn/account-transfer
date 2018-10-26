package com.devstomper.account_transfer.dao.impl;

import com.devstomper.account_transfer.PropertiesManager;
import com.devstomper.account_transfer.dao.GenericDao;
import com.devstomper.account_transfer.model.Account;
import com.devstomper.account_transfer.model.Statement;
import org.apache.commons.dbutils.DbUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.devstomper.account_transfer.ConnectionManager.getConnection;
import static com.devstomper.account_transfer.model.Statement.*;

/**
 * Class to work with Accounts data
 * using H2 Database
 * @author asinitsyn
 * Date: 24.10.2018
 */
@Singleton
public class AccountH2DAO implements GenericDao<Account> {

    private final static String ID_COLUMN = "AccountId";
    private final static String BALANCE_COLUMN = "Balance";
    private final static int ID_PARAMETER_INDEX = 1;
    private final static int BALANCE_PARAMETER_INDEX = 2;

    private static Logger log = LoggerFactory.getLogger(PropertiesManager.class);


    @Override
    public List<Account> retrieveAll() throws Exception {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(Statement.valueOf(GET_ALL));
             ResultSet resultSet = statement.executeQuery()) {
            List<Account> accounts = new ArrayList<>();
            while (resultSet.next()) {
                Account account = new Account(resultSet.getLong(ID_COLUMN), resultSet.getBigDecimal(BALANCE_COLUMN));
                accounts.add(account);
            }
            return accounts;
        }
    }

    @Override
    public Account retrieve(String id) throws Exception {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(Statement.valueOf(GET))) {
            statement.setLong(ID_PARAMETER_INDEX, Long.parseLong(id));
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() ? new Account(rs.getLong(ID_COLUMN), rs.getBigDecimal(BALANCE_COLUMN)) : null;
            }
        }
    }

    @Override
    public void update(Account... accounts) throws Exception {
        PreparedStatement updateStmt = null;
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            updateStmt = connection.prepareStatement(Statement.valueOf(UPDATE));
            for (Account account : accounts) {
                lock(connection, account.getId());
                updateStmt.setBigDecimal(ID_PARAMETER_INDEX, account.getBalance());
                updateStmt.setLong(BALANCE_PARAMETER_INDEX, account.getId());
                updateStmt.addBatch();
            }
            updateStmt.executeBatch();
            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackException) {
                    log.warn("Failed to rollback update transaction: ", rollbackException);
                }
                throw new Exception("Fail to update account: ", e);
            }
        } finally {
            DbUtils.closeQuietly(connection);
            DbUtils.closeQuietly(updateStmt);
        }
    }

    /**
     * Locks row for update
     * @param connection connection
     * @param id         account ID
     * @throws SQLException in case of a db error
     */
    private void lock(Connection connection, long id) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(Statement.valueOf(LOCK))) {
            statement.setLong(ID_PARAMETER_INDEX, id);
            statement.executeQuery();
        }
    }

}
