package com.github.shimopus.revolutmoneyexchange.integration;

import com.github.shimopus.revolutmoneyexchange.dto.BankAccountDto;
import com.github.shimopus.revolutmoneyexchange.exceptions.ObjectModificationException;
import com.github.shimopus.revolutmoneyexchange.model.BankAccount;
import com.github.shimopus.revolutmoneyexchange.model.Currency;
import com.github.shimopus.revolutmoneyexchange.model.Transaction;
import com.github.shimopus.revolutmoneyexchange.service.BankAccountService;
import com.github.shimopus.revolutmoneyexchange.service.ConstantMoneyExchangeService;
import com.github.shimopus.revolutmoneyexchange.service.TransactionsService;
import org.hamcrest.Matchers;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;

public class ConcurrentlyTransactionCreationTest {
    private TransactionsService transactionsService = TransactionsService.getInstance(new ConstantMoneyExchangeService());
    private BankAccountService bankAccountService = BankAccountService.getInstance();

    private static final BigDecimal INITIAL_BALANCE = BigDecimal.valueOf(100L);
    private static final BigDecimal TRANSACTION_AMOUNT = BigDecimal.ONE;
    private static final int INVOCATION_COUNT = 100;

    private Long newBankAccountId;

    @BeforeClass
    public void initData() throws ObjectModificationException {
        BankAccount bankAccount = new BankAccount(
                "New Bank Account",
                INITIAL_BALANCE,
                BigDecimal.ZERO,
                Currency.EUR
        );

        newBankAccountId = bankAccountService.createBankAccount(bankAccount).getId();
    }

    @Test(threadPoolSize = 10, invocationCount = INVOCATION_COUNT)
    public void testConcurrentTransactionCreation() throws ObjectModificationException {
        Transaction transaction = new Transaction(
                newBankAccountId,
                BankAccountDto.NIKOLAY_STORONSKY_BANK_ACCOUNT_ID,
                TRANSACTION_AMOUNT,
                Currency.EUR
        );

        transactionsService.createTransaction(transaction);
    }

    @AfterClass
    public void checkResults() {
        BankAccount bankAccount = bankAccountService.getBankAccountById(newBankAccountId);

        assertThat(bankAccount.getBalance(), Matchers.comparesEqualTo(INITIAL_BALANCE));
        assertThat(bankAccount.getBlockedAmount(),
                Matchers.comparesEqualTo(
                        BigDecimal.ZERO.add(
                            TRANSACTION_AMOUNT.multiply(BigDecimal.valueOf(INVOCATION_COUNT)))
                )
        );
    }
}
