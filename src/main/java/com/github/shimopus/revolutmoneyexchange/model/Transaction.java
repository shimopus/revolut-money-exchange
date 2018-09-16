package com.github.shimopus.revolutmoneyexchange.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

public class Transaction implements ModelHasId{
    private Long id;
    private BankAccount fromBankAccount;
    private BankAccount toBankAccount;
    private BigDecimal amount;
    private Currency currency;
    private Date creationDate;
    private Date updateDate;
    private TransactionStatus status;

    public Transaction() {
        this.creationDate = new Date();
        this.updateDate = new Date();
        this.status = TransactionStatus.PLANNED;
    }

    public Transaction(BankAccount fromBankAccount, BankAccount toBankAccount, BigDecimal amount, Currency currency) {
        super();
        this.fromBankAccount = fromBankAccount;
        this.toBankAccount = toBankAccount;
        this.amount = amount;
        this.currency = currency;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BankAccount getFromBankAccount() {
        return fromBankAccount;
    }

    public void setFromBankAccount(BankAccount fromBankAccount) {
        this.fromBankAccount = fromBankAccount;
    }

    public BankAccount getToBankAccount() {
        return toBankAccount;
    }

    public void setToBankAccount(BankAccount toBankAccount) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
