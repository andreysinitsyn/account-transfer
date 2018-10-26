package com.devstomper.account_transfer.model;

/**
 * SQL statements
 * @author asinitsyn
 * Date: 24.10.2018
 */
public enum Statement {

    GET ("SELECT * FROM Account WHERE AccountId = ? "),

    GET_ALL ("SELECT * FROM Account"),

    LOCK ("SELECT * FROM Account WHERE AccountId = ? FOR UPDATE"),

    UPDATE ("UPDATE Account SET Balance = ? WHERE AccountId = ? ");

    private String value;

    Statement(String value) {
        this.value = value;
    }

    public static String valueOf(Statement statement) {
        return statement.value;
    }

}
