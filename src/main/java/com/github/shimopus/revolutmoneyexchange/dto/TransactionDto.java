package com.github.shimopus.revolutmoneyexchange.dto;

import com.github.shimopus.revolutmoneyexchange.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;

public class TransactionDto {
    private final Logger log = LoggerFactory.getLogger(TransactionDto.class);

    private static final String TRANSACTION_TABLE_NAME = "transaction";
    private static final String TRANSACTION_ID_ROW = "id";
    private static final String TRANSACTION_FROM_ACCOUNT_ROW = "from_account_id";
    private static final String TRANSACTION_TO_ACCOUNT_ROW = "to_account_id";
    private static final String TRANSACTION_AMOUNT_ROW = "amount";
    private static final String TRANSACTION_CURRENCY_ROW = "currency_id";
    private static final String TRANSACTION_CREATION_DATE_ROW = "creation_date";
    private static final String TRANSACTION_UPDATE_DATE_ROW = "update_date";
    private static final String TRANSACTION_STATUS_ROW = "status_id";

    private static final TransactionDto transDto = new TransactionDto();

    private TransactionDto(){}

    public static TransactionDto getInstance() {
        return transDto;
    }

    public Collection<Transaction> getAllTransactions() {
        return new ArrayList<>();
    }

}
