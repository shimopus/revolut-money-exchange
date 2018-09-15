package com.github.shimopus.revolutmoneyexchange.controller;

import com.github.shimopus.revolutmoneyexchange.dto.TransactionDto;
import com.github.shimopus.revolutmoneyexchange.model.Transaction;
import com.github.shimopus.revolutmoneyexchange.service.TransactionsService;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;
import static org.testng.AssertJUnit.assertArrayEquals;

public class TransactionsServiceTest {
    private TransactionsService transactionsService = TransactionsService.getInstance();

    @Test
    public void testAllTransactionsRetrieval(){
        TransactionDto transactionDto = mock(TransactionDto.class);

        Collection<Transaction> testList = new ArrayList<>(Arrays.asList(
                new Transaction(),
                new Transaction()
        ));

        when(transactionDto.getAllTransactions()).thenReturn(testList);

        Collection<Transaction> transactions = transactionsService.getAllTransactions();

        assertNotNull(transactions);
        assertArrayEquals(transactions.toArray(), testList.toArray());
    }
}
