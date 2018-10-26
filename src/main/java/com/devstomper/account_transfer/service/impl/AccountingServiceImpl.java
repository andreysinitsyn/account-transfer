package com.devstomper.account_transfer.service.impl;


import com.devstomper.account_transfer.dao.GenericDao;
import com.devstomper.account_transfer.exceptions.InsufficientBalanceException;
import com.devstomper.account_transfer.model.Account;
import com.devstomper.account_transfer.model.TransactionResult;
import com.devstomper.account_transfer.model.TransactionStatus;
import com.devstomper.account_transfer.model.TransferTransaction;
import com.devstomper.account_transfer.service.AccountingService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

/**
 * Bank accounting service implementation
 * @author asinitsyn
 * Date: 24.10.2018
 */
@Singleton
public class AccountingServiceImpl implements AccountingService {

    /** Account DAO */
    private GenericDao<Account> dao;

    @Inject
    public AccountingServiceImpl(GenericDao<Account> dao) {
        this.dao = dao;
    }

    @Override
    public List<Account> retrieveAllAccounts() throws Exception {
        return dao.retrieveAll();
    }

    @Override
    public Account retrieveAccountById(String id) throws Exception {
        return dao.retrieve(id);
    }

    @Override
    public TransactionResult transfer(TransferTransaction transaction) throws Exception {
        try {
            Account source = retrieveAccountById(transaction.getSourceId());
            Account target = retrieveAccountById(transaction.getTargetId());
            source.withdraw(transaction.getAmount());
            target.deposit(transaction.getAmount());
            dao.update(source, target);
            return new TransactionResult(TransactionStatus.SUCCESS);
        } catch (InsufficientBalanceException e) {
            return new TransactionResult(TransactionStatus.FAIL, e.getMessage());
        }
    }

}
