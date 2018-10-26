package com.devstomper.account_transfer;

import com.devstomper.account_transfer.dao.impl.AccountH2DAO;
import com.devstomper.account_transfer.model.Account;
import com.devstomper.account_transfer.model.TransactionResult;
import com.devstomper.account_transfer.model.TransactionStatus;
import com.devstomper.account_transfer.model.TransferTransaction;
import com.devstomper.account_transfer.service.impl.AccountingServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.devstomper.account_transfer.PropertiesManager.loadConfiguration;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * AccountingServiceImpl Test
 * @author asinitsyn
 * Date: 23.10.2018
 */
public class TestAccountingService {

    @Mock
    private AccountH2DAO daoMock;

    @InjectMocks
    private AccountingServiceImpl service;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldRetrieveAllAccounts() throws Exception {
        List<Account> expectedAccountList = new ArrayList<>();
        expectedAccountList.add(new Account(1, BigDecimal.TEN));
        when(daoMock.retrieveAll()).thenReturn(expectedAccountList);
        List<Account> accounts = service.retrieveAllAccounts();
        assertTrue(accounts.size() == 1);
        assertEquals(1, accounts.get(0).getId());
        assertEquals(BigDecimal.valueOf(10).setScale(2, BigDecimal.ROUND_UP), accounts.get(0).getBalance());
    }

    @Test
    public void shouldRetrieveAccountById() throws Exception {
        Account expectedAccount = new Account(1, BigDecimal.TEN);
        String accountId = "1";
        when(daoMock.retrieve(accountId)).thenReturn(expectedAccount);
        Account account = service.retrieveAccountById(accountId);
        assertEquals(1, account.getId());
        assertEquals(BigDecimal.valueOf(10).setScale(2, BigDecimal.ROUND_UP), account.getBalance());
    }

    @Test
    public void shouldRetrieveTransactionResultWithSuccessStatusWhenSufficientBalance() throws Exception {
        String sourceAccountId = "1";
        String targetAccountId = "2";
        Account source = new Account(Long.parseLong(sourceAccountId), BigDecimal.TEN);
        Account target = new Account(Long.parseLong(targetAccountId), BigDecimal.ZERO);
        when(daoMock.retrieve(sourceAccountId)).thenReturn(source);
        when(daoMock.retrieve(targetAccountId)).thenReturn(target);
        TransferTransaction transferTransaction = new TransferTransaction(sourceAccountId, targetAccountId, BigDecimal.TEN);
        TransactionResult result = service.transfer(transferTransaction);
        assertEquals(TransactionStatus.SUCCESS.getCode(), result.getCode());
        assertEquals(TransactionStatus.SUCCESS.name(), result.getStatusName());
    }

    @Test
    public void shouldRetrieveTransactionResultWithFailStatusWhenInsufficientBalance() throws Exception {
        String sourceAccountId = "1";
        String targetAccountId = "2";
        Account source = new Account(Long.parseLong(sourceAccountId), BigDecimal.ONE);
        Account target = new Account(Long.parseLong(targetAccountId), BigDecimal.ZERO);
        when(daoMock.retrieve(sourceAccountId)).thenReturn(source);
        when(daoMock.retrieve(targetAccountId)).thenReturn(target);
        TransferTransaction transferTransaction = new TransferTransaction(sourceAccountId,
                targetAccountId, BigDecimal.TEN.setScale(2, BigDecimal.ROUND_UP));
        TransactionResult result = service.transfer(transferTransaction);
        assertEquals(TransactionStatus.FAIL.getCode() , result.getCode());
        assertEquals(TransactionStatus.FAIL.name(), result.getStatusName());
        assertEquals("Not enough money to complete transfer" +
                " operation. Current balance: 1.00, Requested amount: 10.00", result.getMessage());
    }
}
