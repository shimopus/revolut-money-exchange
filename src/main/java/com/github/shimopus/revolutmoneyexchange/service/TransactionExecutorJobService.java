package com.github.shimopus.revolutmoneyexchange.service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TransactionExecutorJobService {
    private static int THREAD_POOL_SIZE = 4;
    private static ScheduledExecutorService executorService = Executors.newScheduledThreadPool(THREAD_POOL_SIZE);

    static {
        executorService.scheduleAtFixedRate(() -> {
            TransactionsService.getInstance().executeTransactions();
        }, 0, 5, TimeUnit.SECONDS);
    }
}
