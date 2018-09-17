package com.github.shimopus.revolutmoneyexchange.service;

import com.github.shimopus.revolutmoneyexchange.dto.TransactionDto;
import com.github.shimopus.revolutmoneyexchange.exceptions.ObjectModificationException;
import com.github.shimopus.revolutmoneyexchange.model.Transaction;
import com.github.shimopus.revolutmoneyexchange.model.TransactionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Collection;

public class TransactionsService {
    private final Logger log = LoggerFactory.getLogger(TransactionsService.class);

    private static final TransactionsService ts = new TransactionsService();
    private TransactionDto transactionDto = TransactionDto.getInstance();

    private TransactionsService() {
    }

    /**
     * Constructor made just for testing purpose
     */
    public TransactionsService(TransactionDto transactionDto) {
        this.transactionDto = transactionDto;
    }

    public static TransactionsService getInstance() {
        return ts;
    }

    public Collection<Transaction> getAllTransactions() {
        return transactionDto.getAllTransactions();
    }

    public Collection<Transaction> getAllTransactionsBySatus(TransactionStatus transactionStatus) {
        return transactionDto.getAllTransactionsByStatus(transactionStatus);
    }

    /**
     * Returns specific transaction by ID
     *
     * @return
     */
    public Transaction getTransactionById(Long id) {
        return null;
    }

    /**
     * Make it possible to create money transfer from one account to another.
     * The result of execution is created transaction with actual status. Usually it is "IN PROGRESS"
     *
     * The transaction <code>fromBankAccount</code> and <code>toBankAccount</code> may have not specified any
     * fields except id
     *
     * @return transaction object with the actual ID
     */
    public Transaction createTransaction(Transaction transaction) throws ObjectModificationException {
        if (transaction.getFromBankAccount() == null || transaction.getToBankAccount() == null) {
            throw new ObjectModificationException(ObjectModificationException.Type.OBJECT_IS_MALFORMED,
                    "The transaction has not provided from Bank Account or to Bank Account values");
        }
        if (transaction.getFromBankAccount().getId() == null || transaction.getToBankAccount().getId() == null) {
            throw new ObjectModificationException(ObjectModificationException.Type.OBJECT_IS_MALFORMED,
                    "The transaction has not provided from Bank Account or to Bank Account values");
        }
        if (transaction.getFromBankAccount().equals(transaction.getToBankAccount())) {
            throw new ObjectModificationException(ObjectModificationException.Type.OBJECT_IS_MALFORMED,
                    "The sender and recipient should not be same");
        }
        if (transaction.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ObjectModificationException(ObjectModificationException.Type.OBJECT_IS_MALFORMED,
                    "The amount should be more than 0");
        }

        return transactionDto.createTransaction(transaction);
    }

    /**
     * Here we are taking all PLANNED transactions and executing them.
     * After execution the transaction status will be changed
     */
    public void executeTransactions() {
        //TODO needs to be ended
        Collection<Transaction> plannedTransactions = getAllTransactions();
    }
}
