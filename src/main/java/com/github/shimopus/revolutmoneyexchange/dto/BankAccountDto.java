package com.github.shimopus.revolutmoneyexchange.dto;

import com.github.shimopus.revolutmoneyexchange.db.DbUtils;
import com.github.shimopus.revolutmoneyexchange.exceptions.ObjectModificationException;
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

    public static final Long SERGEY_BABINSKIY_BANK_ACCOUNT_ID = 1L;
    public static final Long NIKOLAY_STORONSKY_BANK_ACCOUNT_ID = 2L;
    public static final Long VLAD_YATSENKO_BANK_ACCOUNT_ID = 3L;

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
                        "where ba." + BANK_ACCOUNT_ID_ROW + " = ?";

        return DbUtils.executeQuery(GET_BANK_ACCOUNT_BY_ID_SQL, getBankAccount -> {
            getBankAccount.setLong(1, id);
            ResultSet bankAccountRS = getBankAccount.executeQuery();

            if (bankAccountRS != null && bankAccountRS.first()) {
                return extractBankAccountFromResultSet(bankAccountRS);
            }

            return null;
        }).getResult();
    }

    public void updateBankAccount(BankAccount bankAccount) throws ObjectModificationException {
        String UPDATE_BANK_ACCOUNT_SQL =
                "update " + BANK_ACCOUNT_TABLE_NAME +
                        " set " +
                        BANK_ACCOUNT_OWNER_NAME_ROW + " = ?, " +
                        BANK_ACCOUNT_BALANCE_ROW + " = ?, " +
                        BANK_ACCOUNT_BLOCKED_AMOUNT_ROW + " = ?, " +
                        BANK_ACCOUNT_CURRENCY_ID_ROW + " = ? " +
                        "where " + BANK_ACCOUNT_ID_ROW + " = ?";

        verify(bankAccount);

        int result = DbUtils.executeQuery(UPDATE_BANK_ACCOUNT_SQL, updateBankAccount -> {
            updateBankAccount.setString(1, bankAccount.getOwnerName());
            updateBankAccount.setBigDecimal(2, bankAccount.getBalance());
            updateBankAccount.setBigDecimal(3, bankAccount.getBlockedAmount());
            updateBankAccount.setLong(4, bankAccount.getCurrency().getId());
            updateBankAccount.setLong(5, bankAccount.getId());

            return updateBankAccount.executeUpdate();
        }).getResult();


        if (result == 0) {
            throw new ObjectModificationException(ObjectModificationException.Type.OBJECT_IS_NOT_FOUND);
        }
    }

    public BankAccount createBankAccount(BankAccount bankAccount) throws ObjectModificationException {
        String INSERT_BANK_ACCOUNT_SQL =
                "insert into " + BANK_ACCOUNT_TABLE_NAME +
                        " (" +
                            BANK_ACCOUNT_OWNER_NAME_ROW + ", " +
                            BANK_ACCOUNT_BALANCE_ROW + ", " +
                            BANK_ACCOUNT_BLOCKED_AMOUNT_ROW + ", " +
                            BANK_ACCOUNT_CURRENCY_ID_ROW +
                        ") values (?, ?, ?, ?)";

        verify(bankAccount);

        Long obtainedId = DbUtils.executeQuery(INSERT_BANK_ACCOUNT_SQL, insertBankAccount -> {
            insertBankAccount.setString(1, bankAccount.getOwnerName());
            insertBankAccount.setBigDecimal(2, bankAccount.getBalance());
            insertBankAccount.setBigDecimal(3, bankAccount.getBlockedAmount());
            insertBankAccount.setLong(4, bankAccount.getCurrency().getId());

            int res = insertBankAccount.executeUpdate();

            if (res == 0) {
                return null;
            }

            try (ResultSet generatedKeys = insertBankAccount.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                }
                else {
                    return null;
                }
            }
        }).getResult();

        if (obtainedId == null) {
            throw new ObjectModificationException(ObjectModificationException.Type.COULD_NOT_OBTAIN_ID);
        }

        bankAccount.setId(obtainedId);

        return bankAccount;
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

    private void verify(BankAccount bankAccount) throws ObjectModificationException {
        if (bankAccount.getId() == null) {
            throw new ObjectModificationException(ObjectModificationException.Type.OBJECT_IS_MALFORMED,
                    "ID value is invalid");
        }

        if (bankAccount.getOwnerName() == null || bankAccount.getBalance() == null ||
                bankAccount.getBlockedAmount() == null || bankAccount.getCurrency() == null) {
            throw new ObjectModificationException(ObjectModificationException.Type.OBJECT_IS_MALFORMED, "Fields could not be NULL");
        }
    }
}
