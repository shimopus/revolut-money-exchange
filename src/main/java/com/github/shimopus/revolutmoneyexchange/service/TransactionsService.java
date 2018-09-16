package com.github.shimopus.revolutmoneyexchange.service;

import com.github.shimopus.revolutmoneyexchange.db.DbUtils;
import com.github.shimopus.revolutmoneyexchange.db.H2DataSource;
import com.github.shimopus.revolutmoneyexchange.dto.BankAccountDto;
import com.github.shimopus.revolutmoneyexchange.dto.TransactionDto;
import com.github.shimopus.revolutmoneyexchange.exceptions.ImpossibleOperationExecution;
import com.github.shimopus.revolutmoneyexchange.exceptions.ObjectModificationException;
import com.github.shimopus.revolutmoneyexchange.model.BankAccount;
import com.github.shimopus.revolutmoneyexchange.model.Transaction;
import com.github.shimopus.revolutmoneyexchange.model.TransactionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.Collection;
import java.util.Comparator;

public class TransactionsService {
    private final Logger log = LoggerFactory.getLogger(TransactionsService.class);

    private static final TransactionsService ts = new TransactionsService();
    private TransactionDto transactionDto = TransactionDto.getInstance();
    private BankAccountDto bankAccountDto = BankAccountDto.getInstance();

    private TransactionsService() {
    }

    /**
     * Constructor made just for testing purpose
     */
    public TransactionsService(TransactionDto transactionDto) {
        this.transactionDto = transactionDto;
    }

    /**
     * Constructor made just for testing purpose
     */
    public TransactionsService(TransactionDto transactionDto, BankAccountDto bankAccountDto) {
        this.transactionDto = transactionDto;
        this.bankAccountDto = bankAccountDto;
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

        BankAccount toBankAccount = bankAccountDto.getBankAccountById(transaction.getToBankAccount().getId());
        transaction.setToBankAccount(toBankAccount);

        Connection con = H2DataSource.getConnection();

        try {
            BankAccount fromBankAccount = bankAccountDto.
                    getForUpdateBankAccountById(con, transaction.getFromBankAccount().getId());

            //Check that from bank account has enough money
            //TODO MONEY CONVERSION
            if (fromBankAccount.getBalance().subtract(fromBankAccount.getBlockedAmount())
                    .compareTo(transaction.getAmount()) <= 0) {
                throw new ObjectModificationException(ObjectModificationException.Type.OBJECT_IS_MALFORMED,
                        "The specified bank account could not transfer this amount of money. " +
                                "His balance does not have enough money");
            }

            transaction.setFromBankAccount(fromBankAccount);

            //TODO Money conversion
            fromBankAccount.setBlockedAmount(fromBankAccount.getBlockedAmount().add(transaction.getAmount()));

            bankAccountDto.updateBankAccount(fromBankAccount, con);
        } catch (Throwable th) {
            DbUtils.safeRollback(con);
            log.error("Unexpected exception", th);
            throw new ImpossibleOperationExecution(th);
        } finally {
            DbUtils.quietlyClose(con);
        }

        return transactionDto.createTransaction(transaction);
    }
}
