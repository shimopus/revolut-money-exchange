package com.github.shimopus.revolutmoneyexchange.service;

import com.github.shimopus.revolutmoneyexchange.dto.BankAccountDto;
import com.github.shimopus.revolutmoneyexchange.model.BankAccount;

import java.sql.SQLException;
import java.util.Collection;

public class BankAccountService {
    private static final BankAccountService bas = new BankAccountService();

    public static BankAccountService getInstance() {
        return bas;
    }

    public Collection<BankAccount> getAllBankAccounts() {
        return BankAccountDto.getInstance().getAllBankAccounts();
    }

    public BankAccount getBankAccountById(Long id) {
        return BankAccountDto.getInstance().getBankAccountById(id);
    }
}
