package com.github.shimopus.revolutmoneyexchange.dto;

import com.github.shimopus.revolutmoneyexchange.db.DbUtils;
import com.github.shimopus.revolutmoneyexchange.model.BankAccount;
import com.github.shimopus.revolutmoneyexchange.model.Currency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class BankAccountDto {
    private static final String BANK_ACCOUNT_TABLE_NAME = "bank_account";
    private static final String BANK_ACCOUNT_ID_ROW = "id";
    private static final String BANK_ACCOUNT_OWNER_NAME_ROW = "owner_name";
    private static final String BANK_ACCOUNT_BALANCE_ROW = "balance";
    private static final String BANK_ACCOUNT_BLOCKED_AMOUNT_ROW = "blocked_amount";
    private static final String BANK_ACCOUNT_CURRENCY_ID_ROW = "currency_id";

    private final Logger log = LoggerFactory.getLogger(BankAccountDto.class);

    private static final BankAccountDto bas = new BankAccountDto();

    private BankAccountDto() {
    }

    public static BankAccountDto getInstance() {
        return bas;
    }

    public Collection<BankAccount> getAllBankAccounts() {
        return DbUtils.executeQuery("select * from " + BANK_ACCOUNT_TABLE_NAME, getBankAccounts -> {
            Collection<BankAccount> bankAccounts = new ArrayList<>();

            ResultSet bankAccountsRS = getBankAccounts.executeQuery();

            if (bankAccountsRS != null) {
                while (bankAccountsRS.next()) {
                    bankAccounts.add(extractBankAccountFromResultSet(bankAccountsRS));
                }
            }

            return bankAccounts;
        }).getResult();
    }

    public BankAccount getBankAccountById(Long id) {
        String GET_BANK_ACCOUNT_BY_ID_SQL =
                "select * from " + BANK_ACCOUNT_TABLE_NAME + " ba " +
                        "where " +
                        "ba.id = ?";

        return DbUtils.executeQuery(GET_BANK_ACCOUNT_BY_ID_SQL, getBankAccount -> {
            getBankAccount.setLong(1, id);
            ResultSet bankAccountRS = getBankAccount.executeQuery();

            if (bankAccountRS != null) {
                bankAccountRS.first();
                return extractBankAccountFromResultSet(bankAccountRS);
            }

            return null;
        }).getResult();
    }

    private BankAccount extractBankAccountFromResultSet(ResultSet bankAccountsRS) throws SQLException {
        BankAccount bankAccount = new BankAccount();
        bankAccount.setId(bankAccountsRS.getLong(BANK_ACCOUNT_ID_ROW));
        bankAccount.setOwnerName(bankAccountsRS.getString(BANK_ACCOUNT_OWNER_NAME_ROW));
        bankAccount.setBalance(bankAccountsRS.getBigDecimal(BANK_ACCOUNT_BALANCE_ROW));
        bankAccount.setBlockedAmount(bankAccountsRS.getBigDecimal(BANK_ACCOUNT_BLOCKED_AMOUNT_ROW));
        bankAccount.setCurrency(Currency.valueOf(bankAccountsRS.getInt(BANK_ACCOUNT_CURRENCY_ID_ROW)));

        return bankAccount;
    }
}
