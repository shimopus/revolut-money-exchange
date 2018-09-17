package com.github.shimopus.revolutmoneyexchange.service;

import com.github.shimopus.revolutmoneyexchange.model.Currency;

import java.math.BigDecimal;

public interface MoneyExchangeService {
    BigDecimal exchange(BigDecimal amount, Currency amountCurrency, Currency targetCurrency);
}
