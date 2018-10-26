package com.devstomper.account_transfer.model;

/**
 * Transaction statuses
 * @author asinitsyn
 * Date: 24.10.2018
 */
public enum TransactionStatus {

    SUCCESS (1),

    FAIL (2);

    private int code;

    TransactionStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
