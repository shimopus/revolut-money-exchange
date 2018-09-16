package com.github.shimopus.revolutmoneyexchange.dto;

import com.github.shimopus.revolutmoneyexchange.db.DbUtils;
import com.github.shimopus.revolutmoneyexchange.exceptions.ObjectModificationException;
import com.github.shimopus.revolutmoneyexchange.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class TransactionDto {
    private static final Logger log = LoggerFactory.getLogger(TransactionDto.class);

    private static final String TRANSACTION_TABLE_NAME = "transaction";
    private static final String TRANSACTION_ID_ROW = "id";
    private static final String TRANSACTION_FROM_ACCOUNT_ROW = "from_account_id";
    private static final String TRANSACTION_TO_ACCOUNT_ROW = "to_account_id";
    private static final String TRANSACTION_AMOUNT_ROW = "amount";
    private static final String TRANSACTION_CURRENCY_ROW = "currency_id";
    private static final String TRANSACTION_CREATION_DATE_ROW = "creation_date";
    private static final String TRANSACTION_UPDATE_DATE_ROW = "update_date";
    private static final String TRANSACTION_STATUS_ROW = "status_id";

    private static final TransactionDto transDto = new TransactionDto();

    private TransactionDto(){}

    public static TransactionDto getInstance() {
        return transDto;
    }

    public Collection<Transaction> getAllTransactions() {
        return new ArrayList<>();
    }

    public Transaction createTransaction(Transaction transaction) throws ObjectModificationException {
        String INSERT_TRANSACTION_SQL =
                "insert into " + TRANSACTION_TABLE_NAME +
                        " (" +
                        TRANSACTION_FROM_ACCOUNT_ROW + ", " +
                        TRANSACTION_TO_ACCOUNT_ROW + ", " +
                        TRANSACTION_AMOUNT_ROW + ", " +
                        TRANSACTION_CURRENCY_ROW + ", " +
                        TRANSACTION_STATUS_ROW + ", " +
                        TRANSACTION_CREATION_DATE_ROW + ", " +
                        TRANSACTION_UPDATE_DATE_ROW +
                        ") values (?, ?, ?, ?, ?, ?, ?)";

        verify(transaction);

        transaction = DbUtils.executeQuery(INSERT_TRANSACTION_SQL,
                new DbUtils.CreationQueryExecutor<>(transaction, TransactionDto::fillInPreparedStatement)).getResult();

        if (transaction == null) {
            throw new ObjectModificationException(ObjectModificationException.Type.COULD_NOT_OBTAIN_ID);
        }

        return transaction;

    }

    private void verify(Transaction transaction) throws ObjectModificationException {
        if (transaction.getAmount() == null || transaction.getFromBankAccount() == null ||
                transaction.getToBankAccount() == null || transaction.getCurrency() == null
                || transaction.getStatus() == null || transaction.getCreationDate() == null
                || transaction.getUpdateDate() == null) {
            throw new ObjectModificationException(ObjectModificationException.Type.OBJECT_IS_MALFORMED, "Fields could not be NULL");
        }
    }

    private static void fillInPreparedStatement(PreparedStatement preparedStatement, Transaction transaction) {
        try {
            preparedStatement.setLong(1, transaction.getFromBankAccount().getId());
            preparedStatement.setLong(2, transaction.getToBankAccount().getId());
            preparedStatement.setBigDecimal(3, transaction.getAmount());
            preparedStatement.setInt(4, transaction.getCurrency().getId());
            preparedStatement.setInt(5, transaction.getStatus().getId());
            preparedStatement.setDate(6, new java.sql.Date(transaction.getCreationDate().getTime()));
            preparedStatement.setDate(7, new java.sql.Date(transaction.getUpdateDate().getTime()));
        } catch (SQLException e) {
            log.error("Transactions prepared statement could not be initialized by values", e);
        }

    }
}
