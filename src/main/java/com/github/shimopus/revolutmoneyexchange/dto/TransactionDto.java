package com.github.shimopus.revolutmoneyexchange.dto;

import com.github.shimopus.revolutmoneyexchange.db.DbUtils;
import com.github.shimopus.revolutmoneyexchange.db.H2DataSource;
import com.github.shimopus.revolutmoneyexchange.exceptions.ImpossibleOperationExecution;
import com.github.shimopus.revolutmoneyexchange.exceptions.ObjectModificationException;
import com.github.shimopus.revolutmoneyexchange.model.BankAccount;
import com.github.shimopus.revolutmoneyexchange.model.Currency;
import com.github.shimopus.revolutmoneyexchange.model.Transaction;
import com.github.shimopus.revolutmoneyexchange.model.TransactionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.*;
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

    private static final TransactionDto transactionDto = new TransactionDto();
    private BankAccountDto bankAccountDto = BankAccountDto.getInstance();

    private TransactionDto() {
    }

    public static TransactionDto getInstance() {
        return transactionDto;
    }

    public Collection<Transaction> getAllTransactions() {
        return new ArrayList<>();
    }

    public Collection<Long> getAllTransactionIdsByStatus(TransactionStatus transactionStatus) {
        if (transactionStatus == null) {
            return null;
        }

        String GET_TRANSACTIONS_BY_STATUS_SQL =
                "select id from " + TRANSACTION_TABLE_NAME + " trans " +
                        "where trans." + TRANSACTION_STATUS_ROW + " = ?";


        return DbUtils.executeQuery(GET_TRANSACTIONS_BY_STATUS_SQL, getTransactionsByStatus -> {
            Collection<Long> transactionIds = new ArrayList<>();

            getTransactionsByStatus.setLong(1, transactionStatus.getId());
            try (ResultSet transactionsRS = getTransactionsByStatus.executeQuery()) {
                if (transactionsRS != null) {
                    while (transactionsRS.next()) {
                        transactionIds.add(transactionsRS.getLong(TRANSACTION_ID_ROW));
                    }
                }
            }

            return transactionIds;
        }).getResult();
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

        Connection con = H2DataSource.getConnection();

        try {
            BankAccount fromBankAccount = bankAccountDto.
                    getForUpdateBankAccountById(con, transaction.getFromBankAccountId());

            //Check that from bank account has enough money
            //TODO MONEY CONVERSION
            if (fromBankAccount.getBalance().subtract(fromBankAccount.getBlockedAmount())
                    .compareTo(transaction.getAmount()) <= 0) {
                throw new ObjectModificationException(ObjectModificationException.Type.OBJECT_IS_MALFORMED,
                        "The specified bank account could not transfer this amount of money. " +
                                "His balance does not have enough money");
            }

            //TODO Money conversion
            fromBankAccount.setBlockedAmount(fromBankAccount.getBlockedAmount().add(transaction.getAmount()));

            bankAccountDto.updateBankAccount(fromBankAccount, con);

            transaction = DbUtils.executeQueryInConnection(con, INSERT_TRANSACTION_SQL,
                    new DbUtils.CreationQueryExecutor<>(transaction, TransactionDto::fillInPreparedStatement)).getResult();

            if (transaction == null) {
                throw new ObjectModificationException(ObjectModificationException.Type.COULD_NOT_OBTAIN_ID);
            }

            con.commit();
        } catch (RuntimeException | SQLException e) {
            DbUtils.safeRollback(con);
            log.error("Unexpected exception", e);
            throw new ImpossibleOperationExecution(e);
        } finally {
            DbUtils.quietlyClose(con);
        }

        return transaction;

    }

    public void executeTransaction(Long id) throws ObjectModificationException {
        if (id == null) {
            throw new ObjectModificationException(ObjectModificationException.Type.OBJECT_IS_MALFORMED,
                    "The specified transaction doesn't exists");
        }

        Connection con = H2DataSource.getConnection();

        Transaction transaction = null;
        try {
            transaction = getForUpdateTransactionById(id, con);

            BankAccount fromBankAccount = bankAccountDto.
                    getForUpdateBankAccountById(con, transaction.getFromBankAccountId());

            BankAccount toBankAccount = bankAccountDto.
                    getForUpdateBankAccountById(con, transaction.getToBankAccountId());

            //TODO Money conversion
            BigDecimal newBlockedAmount = fromBankAccount.getBlockedAmount().subtract(transaction.getAmount());
            BigDecimal newBalance = fromBankAccount.getBalance().subtract(transaction.getAmount());

            if (newBlockedAmount.compareTo(BigDecimal.ZERO) < 0 || newBalance.compareTo(BigDecimal.ZERO) < 0) {
                transaction.setStatus(TransactionStatus.FAILED);
                //TODO set error message into the transaction
                updateTransaction(transaction, con);
            } else {
                fromBankAccount.setBlockedAmount(newBlockedAmount);
                fromBankAccount.setBalance(newBalance);

                bankAccountDto.updateBankAccount(fromBankAccount, con);

                toBankAccount.setBalance(toBankAccount.getBalance().add(transaction.getAmount()));

                bankAccountDto.updateBankAccount(toBankAccount, con);

                transaction.setStatus(TransactionStatus.SUCCEED);

                updateTransaction(transaction, con);
            }

            con.commit();
        } catch (RuntimeException | SQLException e) {
            DbUtils.safeRollback(con);
            if (transaction != null) {
                transaction.setStatus(TransactionStatus.FAILED);
                updateTransaction(transaction);
            }
            log.error("Unexpected exception", e);
            throw new ImpossibleOperationExecution(e);
        } finally {
            DbUtils.quietlyClose(con);
        }
    }

    public Transaction getForUpdateTransactionById(Long id, Connection con) {
        String GET_TRANSACTIONS_FOR_UPDATE_BY_ID_SQL =
                "select * from " + TRANSACTION_TABLE_NAME + " trans " +
                        "where trans." + TRANSACTION_ID_ROW + " = ? " +
                        "for update";

        return DbUtils.executeQueryInConnection(con, GET_TRANSACTIONS_FOR_UPDATE_BY_ID_SQL, getTransaction -> {
            getTransaction.setLong(1, id);
            try (ResultSet transactionRS = getTransaction.executeQuery()) {
                if (transactionRS != null && transactionRS.first()) {
                    return extractTransactionFromResultSet(transactionRS);
                }
            }

            return null;
        }).getResult();
    }

    public void updateTransaction(Transaction transaction) throws ObjectModificationException {
        updateTransaction(transaction, null);
    }

    public void updateTransaction(Transaction transaction, Connection con) throws ObjectModificationException {
        String UPDATE_TRANSACTION_SQL =
                "update " + TRANSACTION_TABLE_NAME +
                        " set " +
                        TRANSACTION_STATUS_ROW + " = ?, " +
                        TRANSACTION_UPDATE_DATE_ROW + " = ? " +
                        "where " + TRANSACTION_ID_ROW + " = ?";

        verify(transaction);

        DbUtils.QueryExecutor<Integer> queryExecutor = updateTransaction -> {
            updateTransaction.setInt(1, transaction.getStatus().getId());
            updateTransaction.setDate(2, new Date(new java.util.Date().getTime()));
            updateTransaction.setLong(3, transaction.getId());

            return updateTransaction.executeUpdate();
        };

        int result;
        if (con == null) {
            result = DbUtils.executeQuery(UPDATE_TRANSACTION_SQL, queryExecutor).getResult();;
        } else {
            result = DbUtils.executeQueryInConnection(con, UPDATE_TRANSACTION_SQL, queryExecutor).getResult();;
        }

        if (result == 0) {
            throw new ObjectModificationException(ObjectModificationException.Type.OBJECT_IS_NOT_FOUND);
        }
    }

    private void verify(Transaction transaction) throws ObjectModificationException {
        if (transaction.getAmount() == null || transaction.getFromBankAccountId() == null ||
                transaction.getToBankAccountId() == null || transaction.getCurrency() == null
                || transaction.getStatus() == null || transaction.getCreationDate() == null
                || transaction.getUpdateDate() == null) {
            throw new ObjectModificationException(ObjectModificationException.Type.OBJECT_IS_MALFORMED, "Fields could not be NULL");
        }
    }

    private static void fillInPreparedStatement(PreparedStatement preparedStatement, Transaction transaction) {
        try {
            preparedStatement.setLong(1, transaction.getFromBankAccountId());
            preparedStatement.setLong(2, transaction.getToBankAccountId());
            preparedStatement.setBigDecimal(3, transaction.getAmount());
            preparedStatement.setInt(4, transaction.getCurrency().getId());
            preparedStatement.setInt(5, transaction.getStatus().getId());
            preparedStatement.setDate(6, new java.sql.Date(transaction.getCreationDate().getTime()));
            preparedStatement.setDate(7, new java.sql.Date(transaction.getUpdateDate().getTime()));
        } catch (SQLException e) {
            log.error("Transactions prepared statement could not be initialized by values", e);
        }

    }

    private Transaction extractTransactionFromResultSet(ResultSet transactionsRS) throws SQLException {
        Transaction transaction = new Transaction();
        transaction.setId(transactionsRS.getLong(TRANSACTION_ID_ROW));
        transaction.setFromBankAccountId(transactionsRS.getLong(TRANSACTION_FROM_ACCOUNT_ROW));
        transaction.setToBankAccountId(transactionsRS.getLong(TRANSACTION_TO_ACCOUNT_ROW));
        transaction.setAmount(transactionsRS.getBigDecimal(TRANSACTION_AMOUNT_ROW));
        transaction.setCurrency(Currency.valueOf(transactionsRS.getInt(TRANSACTION_CURRENCY_ROW)));
        transaction.setStatus(TransactionStatus.valueOf(transactionsRS.getInt(TRANSACTION_STATUS_ROW)));
        transaction.setCreationDate(transactionsRS.getDate(TRANSACTION_CREATION_DATE_ROW));
        transaction.setUpdateDate(transactionsRS.getDate(TRANSACTION_UPDATE_DATE_ROW));
        return transaction;
    }
}
