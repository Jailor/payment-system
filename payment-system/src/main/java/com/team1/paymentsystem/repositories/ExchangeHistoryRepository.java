package com.team1.paymentsystem.repositories;

import com.team1.paymentsystem.entities.ExchangeHistory;
import com.team1.paymentsystem.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExchangeHistoryRepository extends JpaRepository<ExchangeHistory, Long> {
    Optional<ExchangeHistory> findById(long id);
    List<ExchangeHistory> findByPayment(Payment payment);
}
