package com.github.shimopus.revolutmoneyexchange.controller.dto;

import com.github.shimopus.revolutmoneyexchange.db.DbUtils;
import com.github.shimopus.revolutmoneyexchange.dto.BankAccountDto;
import com.github.shimopus.revolutmoneyexchange.dto.TransactionDto;
import com.github.shimopus.revolutmoneyexchange.model.Currency;
import com.github.shimopus.revolutmoneyexchange.model.Transaction;
import com.github.shimopus.revolutmoneyexchange.model.TransactionStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public class TransactionDtoTest {
    private TransactionDto transactionDto;
    private Collection<Transaction> testList;

    private static final Long TRANSACTION_1_ID = 1L;
    private static final Long TRANSACTION_2_ID = 2L;

    @BeforeClass
    public void initTestData() {
        DbUtils dbUtils = mock(DbUtils.class);
        transactionDto = new TransactionDto(dbUtils);

        Transaction transaction1 = new Transaction(
                BankAccountDto.SERGEY_BABINSKIY_BANK_ACCOUNT_ID,
                BankAccountDto.NIKOLAY_STORONSKY_BANK_ACCOUNT_ID,
                BigDecimal.ZERO,
                Currency.EUR);
        transaction1.setId(TRANSACTION_1_ID);

        Transaction transaction2 = new Transaction(
                BankAccountDto.NIKOLAY_STORONSKY_BANK_ACCOUNT_ID,
                BankAccountDto.VLAD_YATSENKO_BANK_ACCOUNT_ID,
                BigDecimal.ZERO,
                Currency.EUR);
        transaction2.setId(TRANSACTION_2_ID);

        testList = Arrays.asList(transaction1, transaction2);

        when(dbUtils.executeQuery(eq(TransactionDto.GET_ALL_TRANSACTIONS_SQL), any())).thenReturn(
                new DbUtils.QueryResult<>(testList)
        );

        when(dbUtils.executeQuery(eq(TransactionDto.GET_TRANSACTIONS_BY_STATUS_SQL), any())).thenReturn(
                new DbUtils.QueryResult<>(testList.stream().map(Transaction::getId).collect(Collectors.toList()))
        );

        when(dbUtils.executeQueryInConnection(any(), eq(TransactionDto.GET_TRANSACTIONS_FOR_UPDATE_BY_ID_SQL), any()))
                .thenReturn()
    }

    /**
     * Tests that all transactions from DB will be returned
     */
    @Test
    public void testGetAllTransactions() {
        Collection<Transaction> resultList = transactionDto.getAllTransactions();

        assertNotNull(resultList);
        assertEquals(testList, resultList);
    }

    /**
     * Tests that all transaction's id with particular status will be returned
     */
    @Test
    public void testGetAllTransactionIdsByStatus() {
        Collection<Long> resultTransactionIds = transactionDto.getAllTransactionIdsByStatus(TransactionStatus.PLANNED);

        assertNotNull(resultTransactionIds);
        assertEquals(resultTransactionIds.size(), 2);
        assertTrue(resultTransactionIds.contains(BankAccountDto.SERGEY_BABINSKIY_BANK_ACCOUNT_ID));
        assertTrue(resultTransactionIds.contains(BankAccountDto.NIKOLAY_STORONSKY_BANK_ACCOUNT_ID));
    }
}
