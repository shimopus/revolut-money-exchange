package com.github.shimopus.revolutmoneyexchange.model;

import java.math.BigDecimal;
import java.util.Date;

public class Transaction {
    private Long id;
    private Long fromBankAccount;
    private Long toBankAccount;
    private BigDecimal amount;
    private Currency currency;
    private Date creationDate;
    private Date updateDate;
    private TransactionStatus status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFromBankAccount() {
        return fromBankAccount;
    }

    public void setFromBankAccount(Long fromBankAccount) {
        this.fromBankAccount = fromBankAccount;
    }

    public Long getToBankAccount() {
        return toBankAccount;
    }

    public void setToBankAccount(Long toBankAccount) {
        this.toBankAccount = toBankAccount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }
}
