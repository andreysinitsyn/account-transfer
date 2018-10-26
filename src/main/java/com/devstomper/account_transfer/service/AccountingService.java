package com.devstomper.account_transfer.service;

import com.devstomper.account_transfer.model.Account;
import com.devstomper.account_transfer.model.TransactionResult;
import com.devstomper.account_transfer.model.TransferTransaction;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service for using bank accounting
 * @author asinitsyn
 * Date: 24.10.2018
 */
public interface AccountingService {

    /**
     * Retrieve all accounts
     * @return {@link List} accounts
     * @throws Exception in case of a retrieve operation error
     */
    List<Account> retrieveAllAccounts() throws Exception;

    /**
     * Retrieve account by its ID
     * @param id account ID
     * @return {@link Account} account
     * @throws Exception in case of a retrieve operation error
     */
    Account retrieveAccountById(String id) throws Exception;

    /**
     * Transfer money from one account to another
     * @param transaction transfer request details
     * @return {@link TransactionResult} transaction result details
     * @throws Exception in case of an error during transfer transaction
     */
    TransactionResult transfer(TransferTransaction transaction) throws Exception;

}
