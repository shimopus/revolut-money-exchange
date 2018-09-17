package com.github.shimopus.revolutmoneyexchange.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TransactionExecutorJobService {
    private static final Logger log = LoggerFactory.getLogger(TransactionExecutorJobService.class);

    private static int THREAD_POOL_SIZE = 4;
    private static ScheduledExecutorService executorService = Executors.newScheduledThreadPool(THREAD_POOL_SIZE);

    static {
        executorService.scheduleAtFixedRate(() -> {
            TransactionsService.getInstance().executeTransactions();
        }, 0, 5, TimeUnit.SECONDS);
    }

    public static void planToExecute() {
        log.info("Transaction Executor planned");
    }
}
