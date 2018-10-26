package com.devstomper.account_transfer.exceptions;

import java.math.BigDecimal;

/**
 * Exception in case there are not enough money
 * to complete transfer transaction
 * @author asinitsyn
 * 22/10/2018
 */
public class InsufficientBalanceException extends Exception {

    private static final String MESSAGE = "Not enough money to complete transfer operation. " +
            "Current balance: %s, Requested amount: %s";

    public InsufficientBalanceException(BigDecimal balance, BigDecimal requestedAmount) {
        super(String.format(MESSAGE, balance, requestedAmount));
    }
}
