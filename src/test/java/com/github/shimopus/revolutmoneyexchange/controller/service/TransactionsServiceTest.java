package com.github.shimopus.revolutmoneyexchange.controller.service;

import com.github.shimopus.revolutmoneyexchange.dto.BankAccountDto;
import com.github.shimopus.revolutmoneyexchange.dto.TransactionDto;
import com.github.shimopus.revolutmoneyexchange.exceptions.ObjectModificationException;
import com.github.shimopus.revolutmoneyexchange.model.Currency;
import com.github.shimopus.revolutmoneyexchange.model.Transaction;
import com.github.shimopus.revolutmoneyexchange.model.TransactionStatus;
import com.github.shimopus.revolutmoneyexchange.service.ConstantMoneyExchangeService;
import com.github.shimopus.revolutmoneyexchange.service.TransactionsService;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;
import static org.testng.AssertJUnit.assertArrayEquals;

public class TransactionsServiceTest {
    private static final TransactionsService staticTransactionService = TransactionsService.getInstance(
            new ConstantMoneyExchangeService()
    );

    @Test
    public void testAllTransactionsRetrieval(){
        TransactionDto transactionDto = mock(TransactionDto.class);
        TransactionsService transactionsService = new TransactionsService(transactionDto);

        Collection<Transaction> testList = Arrays.asList(
                new Transaction(
                        BankAccountDto.SERGEY_BABINSKIY_BANK_ACCOUNT_ID,
                        BankAccountDto.NIKOLAY_STORONSKY_BANK_ACCOUNT_ID,
                        BigDecimal.ZERO,
                        Currency.EUR),
                new Transaction(
                        BankAccountDto.NIKOLAY_STORONSKY_BANK_ACCOUNT_ID,
                        BankAccountDto.VLAD_YATSENKO_BANK_ACCOUNT_ID,
                        BigDecimal.ZERO,
                        Currency.EUR)
        );

        when(transactionDto.getAllTransactions()).thenReturn(testList);

        Collection<Transaction> transactions = transactionsService.getAllTransactions();

        assertNotNull(transactions);
        assertArrayEquals(testList.toArray(), transactions.toArray());
    }

    /**
     * //Test null from account
     *
     * @throws ObjectModificationException
     */
    @Test(expectedExceptions=ObjectModificationException.class)
    public void testCreateTransactionWithNullFrom() throws ObjectModificationException {
        staticTransactionService.createTransaction(new Transaction(
                null, 2L, BigDecimal.TEN, Currency.RUB
        ));
    }

    /**
     * Test null to account
     *
     * @throws ObjectModificationException
     */
    @Test(expectedExceptions=ObjectModificationException.class)
    public void testCreateTransactionWithNullTo() throws ObjectModificationException {
        staticTransactionService.createTransaction(new Transaction(
                1L, null, BigDecimal.TEN, Currency.RUB
        ));
    }

    /**
     * Test transaction creation with the same accounts
     *
     * @throws ObjectModificationException
     */
    @Test(expectedExceptions=ObjectModificationException.class)
    public void testCreateTransactionWithSameAccounts() throws ObjectModificationException {
        staticTransactionService.createTransaction(new Transaction(
                BankAccountDto.SERGEY_BABINSKIY_BANK_ACCOUNT_ID,
                BankAccountDto.SERGEY_BABINSKIY_BANK_ACCOUNT_ID,
                BigDecimal.TEN,
                Currency.RUB
        ));
    }

    /**
     * Test transaction creation with zero amount
     *
     * @throws ObjectModificationException
     */
    @Test(expectedExceptions=ObjectModificationException.class)
    public void testCreateTransactionWithZeroAmount() throws ObjectModificationException {
        staticTransactionService.createTransaction(new Transaction(
                BankAccountDto.SERGEY_BABINSKIY_BANK_ACCOUNT_ID,
                BankAccountDto.NIKOLAY_STORONSKY_BANK_ACCOUNT_ID,
                BigDecimal.ZERO,
                Currency.RUB
        ));
    }

    /**
     * Testing of Transaction creation and execution. Once transaction has been created
     * the scheduled job will execute it.
     *
     * @throws ObjectModificationException
     */
    @Test
    public void testCreateTransaction() throws ObjectModificationException {
        Long TRANSACTION_ID = 123L;

        TransactionDto transactionDto = mock(TransactionDto.class);

        Transaction transaction = new Transaction(
                BankAccountDto.SERGEY_BABINSKIY_BANK_ACCOUNT_ID,
                BankAccountDto.NIKOLAY_STORONSKY_BANK_ACCOUNT_ID,
                BigDecimal.TEN,
                Currency.RUB
        );
        transaction.setId(TRANSACTION_ID);

        when(transactionDto.createTransaction(any())).thenReturn(transaction);

        when(transactionDto.getAllTransactionIdsByStatus(any())).thenReturn(
                Collections.singletonList(transaction.getId())
        );

        doAnswer(invocation -> {
            transaction.setStatus(TransactionStatus.SUCCEED);
            return null;
        }).when(transactionDto).executeTransaction(anyLong());

        TransactionsService transactionsService = new TransactionsService(transactionDto);
        Transaction createdTransaction = transactionsService.createTransaction(transaction);

        assertEquals(createdTransaction, transaction);
        assertEquals(createdTransaction.getStatus(), TransactionStatus.PLANNED);

        transactionsService.executeTransactions();

        assertEquals(transaction.getStatus(), TransactionStatus.SUCCEED);
    }
}
