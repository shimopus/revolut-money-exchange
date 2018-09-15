package com.github.shimopus.revolutmoneyexchange.service;

import com.github.shimopus.revolutmoneyexchange.dto.TransactionDto;
import com.github.shimopus.revolutmoneyexchange.model.Transaction;

import java.util.Collection;

public class TransactionsService {
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
     * @return
     */
    public Transaction createTransaction(Transaction transaction) {
        return null;
    }
}
