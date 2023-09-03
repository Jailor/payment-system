package com.team1.paymentsystem.services.entities;

import com.team1.paymentsystem.entities.Account;
import com.team1.paymentsystem.entities.ExchangeHistory;
import com.team1.paymentsystem.entities.ExchangeRate;
import com.team1.paymentsystem.entities.Payment;
import com.team1.paymentsystem.repositories.ExchangeHistoryRepository;
import com.team1.paymentsystem.repositories.ExchangeRateRepository;
import com.team1.paymentsystem.states.Currency;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Log
public class ExchangeRateServiceImpl implements ExchangeRateService{
    @Autowired
    ExchangeRateRepository exchangeRateRepository;
    @Autowired
    ExchangeHistoryRepository exchangeHistoryRepository;

    @Override
    public ExchangeRate findById(long id) {
        return exchangeRateRepository.findById(id).orElse(null);
    }

    @Override
    public ExchangeRate save(ExchangeRate exchangeRate) {
        ExchangeRate db = exchangeRateRepository.findById(exchangeRate.getId()).orElse(null);
        ExchangeRate ret = null;
        if(db == null){
            ret = exchangeRateRepository.save(exchangeRate);
        }
        return ret;
    }

    @Override
    public ExchangeRate update(ExchangeRate exchangeRate) {
        ExchangeRate db = exchangeRateRepository.findById(exchangeRate.getId()).orElse(null);
        ExchangeRate ret = null;
        if(db != null){
            ret = exchangeRateRepository.save(exchangeRate);
        }
        return ret;
    }

    @Override
    public List<ExchangeRate> findAll() {
        return exchangeRateRepository.findAll();
    }

    @Override
    public List<ExchangeRate> findActiveExchange(Currency sourceCurrency) {
        return exchangeRateRepository.findActiveExchangeBySourceCurrencyActive(sourceCurrency);
    }

    @Override
    public ExchangeRate findActiveExchangeSourceDestination(Currency sourceCurrency, Currency destinationCurrency) {
        return exchangeRateRepository
                .findActiveExchangeRateBySourceCurrencyAndDestinationCurrency(sourceCurrency, destinationCurrency).orElse(null);
    }

    @Override
    public Long getPaymentAmountInDestinationCurrency(Payment payment, Account destinationAccount) {
        Currency paymentCurrency = payment.getCurrency();
        Currency destinationCurrency = destinationAccount.getCurrency();
        Long convertedAmount = payment.getAmount();
        if(!destinationCurrency.getName().equals(payment.getCurrency().getName())){
            ExchangeRate exchangeRate = findActiveExchangeSourceDestination(paymentCurrency, destinationCurrency);
            if(exchangeRate == null){
                log.severe("No exchange rate found for payment currency: " + paymentCurrency.getName() +
                        " and destination currency: " + destinationCurrency.getName());
                convertedAmount = null;
            }
            else {
                convertedAmount = (long) (exchangeRate.getRatio() * payment.getAmount());
            }
        }
        return convertedAmount;
    }

    @Override
    public void saveExchangeRateHistory(Payment payment, Account destinationAccount){
        Currency paymentCurrency = payment.getCurrency();
        Currency destinationCurrency = destinationAccount.getCurrency();
        if(destinationCurrency.getName().equals(payment.getCurrency().getName())){
            return;
        }
        ExchangeRate exchangeRate = findActiveExchangeSourceDestination(paymentCurrency, destinationCurrency);
        ExchangeHistory exchangeHistory = new ExchangeHistory();
        exchangeHistory.setExchangeRate(exchangeRate);
        exchangeHistory.setPayment(payment);
        exchangeHistory.setDestinationAccount(destinationAccount);
        exchangeHistory.setTimeStamp(LocalDateTime.now());
        exchangeHistoryRepository.save(exchangeHistory);
    }
}
