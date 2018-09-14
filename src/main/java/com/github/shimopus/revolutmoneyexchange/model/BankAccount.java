package com.github.shimopus.revolutmoneyexchange.model;

import java.math.BigDecimal;

public class BankAccount {
    private Long id;
    private String ownerName;
    private BigDecimal balance;
    private BigDecimal blockedAmount;
    private Currency currency;

    public BankAccount() {
    }

    public BankAccount(Long id, String ownerName, BigDecimal balance, BigDecimal blockedAmount, Currency currency) {
        this.id = id;
        this.ownerName = ownerName;
        this.balance = balance;
        this.blockedAmount = blockedAmount;
        this.currency = currency;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getBlockedAmount() {
        return blockedAmount;
    }

    public void setBlockedAmount(BigDecimal blockedAmount) {
        this.blockedAmount = blockedAmount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }
}
