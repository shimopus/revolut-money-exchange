package com.github.shimopus.revolutmoneyexchange.dto;

import com.github.shimopus.revolutmoneyexchange.db.DbUtils;
import com.github.shimopus.revolutmoneyexchange.exceptions.ObjectModificationException;
import com.github.shimopus.revolutmoneyexchange.model.BankAccount;
import com.github.shimopus.revolutmoneyexchange.model.Currency;
import com.github.shimopus.revolutmoneyexchange.model.Transaction;
import com.github.shimopus.revolutmoneyexchange.model.TransactionStatus;
import com.github.shimopus.revolutmoneyexchange.service.ConstantMoneyExchangeService;
import com.github.shimopus.revolutmoneyexchange.service.MoneyExchangeService;
import org.hamcrest.Matchers;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public class TransactionDtoTest {
    private TransactionDto transactionDto;
    private Collection<Transaction> testList;
    private MoneyExchangeService moneyExchangeService = new ConstantMoneyExchangeService();

    private static final Long TRANSACTION_1_ID = 1L;
    private static final Long TRANSACTION_2_ID = 2L;

    private Transaction transaction1;
    private Transaction transaction2;

    @BeforeClass
    public void initTestData() {
        DbUtils dbUtils = mock(DbUtils.class);
        transactionDto = new TransactionDto(dbUtils);

        transaction1 = new Transaction(
                BankAccountDto.SERGEY_BABINSKIY_BANK_ACCOUNT_ID,
                BankAccountDto.NIKOLAY_STORONSKY_BANK_ACCOUNT_ID,
                BigDecimal.ONE,
                Currency.EUR);
        transaction1.setId(TRANSACTION_1_ID);

        transaction2 = new Transaction(
                BankAccountDto.NIKOLAY_STORONSKY_BANK_ACCOUNT_ID,
                BankAccountDto.VLAD_YATSENKO_BANK_ACCOUNT_ID,
                BigDecimal.TEN,
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
                .thenReturn(new DbUtils.QueryResult<>(testList));
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

    @Test
    public void testTransactionCreation() throws ObjectModificationException {
        TransactionDto transactionDto = TransactionDto.getInstance(moneyExchangeService);
        BankAccountDto bankAccountDto = BankAccountDto.getInstance();

        BankAccount sergey = bankAccountDto.getBankAccountById(BankAccountDto.SERGEY_BABINSKIY_BANK_ACCOUNT_ID);
        BankAccount nikolay = bankAccountDto.getBankAccountById(BankAccountDto.NIKOLAY_STORONSKY_BANK_ACCOUNT_ID);

        BigDecimal sergeyInitialBalance = sergey.getBalance();
        BigDecimal sergeyInitialBlocked = sergey.getBlockedAmount();
        BigDecimal nikolayInitialBalance = nikolay.getBalance();
        BigDecimal nikolayInitialBlocked = nikolay.getBlockedAmount();

        Transaction resultTransaction = transactionDto.createTransaction(transaction1);

        assertEquals(resultTransaction.getStatus(), TransactionStatus.PLANNED);

        sergey = bankAccountDto.getBankAccountById(BankAccountDto.SERGEY_BABINSKIY_BANK_ACCOUNT_ID);
        nikolay = bankAccountDto.getBankAccountById(BankAccountDto.NIKOLAY_STORONSKY_BANK_ACCOUNT_ID);

        assertThat(sergeyInitialBalance, Matchers.comparesEqualTo(sergey.getBalance()));
        assertThat(sergeyInitialBlocked.add(
                moneyExchangeService.exchange(transaction1.getAmount(), transaction1.getCurrency(), sergey.getCurrency())),
                Matchers.comparesEqualTo(sergey.getBlockedAmount()));
        assertThat(nikolayInitialBalance, Matchers.comparesEqualTo(nikolay.getBalance()));
        assertThat(nikolayInitialBlocked, Matchers.comparesEqualTo(nikolay.getBlockedAmount()));
    }

    @Test
    public void testTransactionExecution() throws ObjectModificationException {
        TransactionDto transactionDto = TransactionDto.getInstance(moneyExchangeService);
        BankAccountDto bankAccountDto = BankAccountDto.getInstance();

        BankAccount nikolay = bankAccountDto.getBankAccountById(BankAccountDto.NIKOLAY_STORONSKY_BANK_ACCOUNT_ID);
        BankAccount vlad = bankAccountDto.getBankAccountById(BankAccountDto.VLAD_YATSENKO_BANK_ACCOUNT_ID);

        BigDecimal nikolayInitialBalance = nikolay.getBalance();
        BigDecimal nikolayInitialBlocked = nikolay.getBlockedAmount();
        BigDecimal vladInitialBalance = vlad.getBalance();
        BigDecimal vladInitialBlocked = vlad.getBlockedAmount();

        Transaction resultTransaction = transactionDto.createTransaction(transaction2);
        transactionDto.executeTransaction(resultTransaction.getId());

        resultTransaction = transactionDto.getTransactionById(resultTransaction.getId());
        nikolay = bankAccountDto.getBankAccountById(transaction2.getFromBankAccountId());
        vlad = bankAccountDto.getBankAccountById(transaction2.getToBankAccountId());
        BigDecimal needToWithdraw = moneyExchangeService.exchange(
                transaction2.getAmount(),
                transaction2.getCurrency(),
                nikolay.getCurrency()
        );
        BigDecimal needToTransfer = moneyExchangeService.exchange(
                transaction2.getAmount(),
                transaction2.getCurrency(),
                vlad.getCurrency()
        );

        assertEquals(resultTransaction.getStatus(), TransactionStatus.SUCCEED);
        assertThat(nikolayInitialBalance.subtract(needToWithdraw), Matchers.comparesEqualTo(nikolay.getBalance()));

        assertThat(nikolayInitialBlocked, Matchers.comparesEqualTo(nikolay.getBlockedAmount()));

        assertThat(vladInitialBalance.add(needToTransfer), Matchers.comparesEqualTo(vlad.getBalance()));

        assertThat(vladInitialBlocked, Matchers.comparesEqualTo(vlad.getBlockedAmount()));
    }

    @Test(expectedExceptions = ObjectModificationException.class)
    public void testWrongTransactionCreation() throws ObjectModificationException {
        TransactionDto transactionDto = TransactionDto.getInstance(moneyExchangeService);
        BankAccountDto bankAccountDto = BankAccountDto.getInstance();

        Transaction transaction = new Transaction(
                BankAccountDto.NIKOLAY_STORONSKY_BANK_ACCOUNT_ID,
                BankAccountDto.VLAD_YATSENKO_BANK_ACCOUNT_ID,
                BigDecimal.valueOf(10000), //much more than Nikolay has
                Currency.EUR
        );

        transactionDto.createTransaction(transaction);
    }
}
