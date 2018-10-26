package com.devstomper.account_transfer.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * Transfer request details
 * @author asinitsyn
 * Date: 24.10.2018
 */
@JsonInclude
public class TransferTransaction {

    private String sourceId;

    private String targetId;

    private BigDecimal amount;

    @JsonCreator
    public TransferTransaction(@JsonProperty("sourceId") String sourceId,
                               @JsonProperty("targetId") String targetId,
                               @JsonProperty("amount") BigDecimal amount) {
        this.sourceId = sourceId;
        this.targetId = targetId;
        this.amount = amount;
    }

    public String getSourceId() {
        return sourceId;
    }

    public String getTargetId() {
        return targetId;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
