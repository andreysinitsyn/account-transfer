package com.devstomper.account_transfer.model;

import com.devstomper.account_transfer.exceptions.InsufficientBalanceException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * Account
 * @author asinitsyn
 * Date: 24.10.2018
 */
@JsonInclude
public class Account {

    private long id;
    private BigDecimal balance;

    /**
     * Creates account
     * @param id      account ID
     * @param balance account balance
     */
    @JsonCreator
    public Account(@JsonProperty("id") long id,
                   @JsonProperty("balance") BigDecimal balance) {
        this.id = id;
        this.balance = balance.setScale(2, BigDecimal.ROUND_UP);
    }

    public void withdraw(BigDecimal amount) throws InsufficientBalanceException {
        if (balance.compareTo(amount) < 0) {
            throw new InsufficientBalanceException(balance, amount);
        }
        this.balance = balance.subtract(amount);
    }

    public void deposit(BigDecimal amount) {
        this.balance = balance.add(amount);
    }

    public long getId() {
        return id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

}
