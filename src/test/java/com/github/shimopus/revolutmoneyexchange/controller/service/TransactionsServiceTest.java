package com.github.shimopus.revolutmoneyexchange.controller.service;

import com.github.shimopus.revolutmoneyexchange.dto.BankAccountDto;
import com.github.shimopus.revolutmoneyexchange.dto.TransactionDto;
import com.github.shimopus.revolutmoneyexchange.exceptions.ObjectModificationException;
import com.github.shimopus.revolutmoneyexchange.model.BankAccount;
import com.github.shimopus.revolutmoneyexchange.model.Currency;
import com.github.shimopus.revolutmoneyexchange.model.Transaction;
import com.github.shimopus.revolutmoneyexchange.model.TransactionStatus;
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

    @Test
    public void testAllTransactionsRetrieval(){
        TransactionDto transactionDto = mock(TransactionDto.class);
        TransactionsService transactionsService = new TransactionsService(transactionDto);

        Collection<Transaction> testList = new ArrayList<>(Arrays.asList(
                new Transaction(new BankAccount(), new BankAccount(), BigDecimal.ZERO, Currency.EUR),
                new Transaction(new BankAccount(), new BankAccount(), BigDecimal.ZERO, Currency.EUR)
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
        Transaction transaction = TransactionsService.getInstance().createTransaction(new Transaction(
                null, new BankAccount(), BigDecimal.TEN, Currency.RUB
        ));
    }

    /**
     * Test null to account
     *
     * @throws ObjectModificationException
     */
    @Test(expectedExceptions=ObjectModificationException.class)
    public void testCreateTransactionWithNullTo() throws ObjectModificationException {
        Transaction transaction = TransactionsService.getInstance().createTransaction(new Transaction(
                new BankAccount(), null, BigDecimal.TEN, Currency.RUB
        ));
    }

    /**
     * Test transaction creation with the same accounts
     *
     * @throws ObjectModificationException
     */
    @Test(expectedExceptions=ObjectModificationException.class)
    public void testCreateTransactionWithSameAccounts() throws ObjectModificationException {
        BankAccount sbAccount = sbAccount();
        BankAccount sbAccount2 = sbAccount();

        Transaction transaction = TransactionsService.getInstance().createTransaction(new Transaction(
                sbAccount, sbAccount2, BigDecimal.TEN, Currency.RUB
        ));
    }

    /**
     * Test transaction creation with zero amount
     *
     * @throws ObjectModificationException
     */
    @Test(expectedExceptions=ObjectModificationException.class)
    public void testCreateTransactionWithZeroAmount() throws ObjectModificationException {
        Transaction transaction = TransactionsService.getInstance().createTransaction(new Transaction(
                new BankAccount(), new BankAccount(), BigDecimal.ZERO, Currency.RUB
        ));
    }

    @Test
    public void testCreateTransaction() throws ObjectModificationException {
        TransactionDto transactionDto = mock(TransactionDto.class);
        BankAccountDto bankAccountDto = mock(BankAccountDto.class);

        TransactionsService transactionsService = new TransactionsService(transactionDto);

        BankAccount sbAccount = new BankAccount(
                BankAccountDto.SERGEY_BABINSKIY_BANK_ACCOUNT_ID,
                "",
                new BigDecimal(1000),
                BigDecimal.ZERO,
                Currency.RUB);

        BankAccount nsAccount = new BankAccount(
                BankAccountDto.NIKOLAY_STORONSKY_BANK_ACCOUNT_ID,
                "",
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                Currency.EUR);

        when(bankAccountDto.getBankAccountById(BankAccountDto.SERGEY_BABINSKIY_BANK_ACCOUNT_ID)).thenReturn(
                sbAccount
        );

        when(bankAccountDto.getBankAccountById(BankAccountDto.NIKOLAY_STORONSKY_BANK_ACCOUNT_ID)).thenReturn(
                nsAccount
        );

        Transaction createdTransaction = transactionsService.createTransaction(
                new Transaction(
                        sbAccount,
                        nsAccount,
                        BigDecimal.TEN,
                        Currency.RUB
                        )
        );

        assertNotNull(createdTransaction);
        assertEquals(createdTransaction.getStatus(), TransactionStatus.PLANNED);
    }

    private BankAccount sbAccount() {
        return new BankAccount(
                BankAccountDto.SERGEY_BABINSKIY_BANK_ACCOUNT_ID,
                "",
                new BigDecimal(1000),
                BigDecimal.ZERO,
                Currency.RUB);
    }
}
