package com.github.shimopus.revolutmoneyexchange.service;

import com.github.shimopus.revolutmoneyexchange.model.Currency;

import java.math.BigDecimal;

/**
 * The interface specifies the service which could make currency conversion. Used to abstract the transferring service
 * from the specific implementation of the service.
 */
public interface MoneyExchangeService {
    BigDecimal exchange(BigDecimal amount, Currency amountCurrency, Currency targetCurrency);
}
