package com.team1.paymentsystem.repositories;

import com.team1.paymentsystem.entities.ExchangeRate;
import com.team1.paymentsystem.states.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {
    Optional<ExchangeRate> findById(long id);
    @Query("select e from ExchangeRate e where e.sourceCurrency = ?1 and e.toDate = null")
    List<ExchangeRate> findActiveExchangeBySourceCurrencyActive(Currency sourceCurrency);
    @Query("select e from ExchangeRate e where e.sourceCurrency = ?1 and e.destinationCurrency = ?2 and e.toDate = null")
    Optional<ExchangeRate> findActiveExchangeRateBySourceCurrencyAndDestinationCurrency(Currency sourceCurrency, Currency destinationCurrency);
}
