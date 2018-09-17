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
        TransactionsService transactionsService = new TransactionsService(transactionDto, new ConstantMoneyExchangeService());

        Collection<Transaction> testList = new ArrayList<>(Arrays.asList(
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
        ));

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

    @Test
    public void testCreateTransaction() throws ObjectModificationException {
        TransactionDto transactionDto = mock(TransactionDto.class);

        TransactionsService transactionsService = new TransactionsService(transactionDto, new ConstantMoneyExchangeService());

        Transaction createdTransaction = transactionsService.createTransaction(
                new Transaction(
                        BankAccountDto.SERGEY_BABINSKIY_BANK_ACCOUNT_ID,
                        BankAccountDto.NIKOLAY_STORONSKY_BANK_ACCOUNT_ID,
                        BigDecimal.TEN,
                        Currency.RUB
                        )
        );

        assertNotNull(createdTransaction);
        assertEquals(createdTransaction.getStatus(), TransactionStatus.PLANNED);
    }
}
