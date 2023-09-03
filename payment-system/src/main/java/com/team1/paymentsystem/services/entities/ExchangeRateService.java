package com.team1.paymentsystem.services.entities;

import com.team1.paymentsystem.entities.Account;
import com.team1.paymentsystem.entities.ExchangeRate;
import com.team1.paymentsystem.entities.Payment;
import com.team1.paymentsystem.states.Currency;

import java.util.List;

/**
 * Service for {@link ExchangeRate} entity, provides methods for CRUD operations
 * and a method to find active exchange rates and for currency conversion:
 * see {@link #getPaymentAmountInDestinationCurrency(Payment, Account)}
 */
public interface ExchangeRateService {
    ExchangeRate findById(long id);
    ExchangeRate save(ExchangeRate exchangeRate);
    ExchangeRate update(ExchangeRate exchangeRate);
    List<ExchangeRate> findAll();
    List<ExchangeRate> findActiveExchange(Currency sourceCurrency);
    ExchangeRate findActiveExchangeSourceDestination(Currency sourceCurrency, Currency destinationCurrency);

    /**
     * Saves into history the exchange rate that was used for the payment
     * @param payment payment for which we want to save the exchange rate history
     * @param destinationAccount destination account with the currency in which we want to convert the payment amount.
     */
    void saveExchangeRateHistory(Payment payment, Account destinationAccount);

    /**
     * @param payment  payment for which we want to convert the amount, the payment must have a valid currency.
     * @param destinationAccount destination account with the currency in which we want to convert the payment amount.
     * @return the amount of the payment converted in the destination currency, or the payment amount in case
     * the payment currency is the same as the destination currency.
     */
    Long getPaymentAmountInDestinationCurrency(Payment payment, Account destinationAccount);
}
