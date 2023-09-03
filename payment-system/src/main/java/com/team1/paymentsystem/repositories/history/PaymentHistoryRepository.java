package com.team1.paymentsystem.repositories.history;

import com.team1.paymentsystem.entities.Account;
import com.team1.paymentsystem.entities.Payment;
import com.team1.paymentsystem.entities.history.CustomerHistory;
import com.team1.paymentsystem.entities.history.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {
    Optional<List<PaymentHistory>> findByOriginalId(long originalId);
    Optional<PaymentHistory> findById(long id);
    @Query("SELECT p FROM PaymentHistory p WHERE p.creditAccount = ?1 OR p.debitAccount = ?1 order by p.historyTimeStamp desc ")
    List<PaymentHistory> findByCreditAccountOrDebitAccount(Account acc);
    @Query("SELECT p FROM PaymentHistory p WHERE (p.creditAccount = ?1 OR p.debitAccount = ?1) AND " +
            "(p.status = 'COMPLETED' OR p.status = 'CANCELLED') order by p.historyTimeStamp desc ")
    List<PaymentHistory> findByCreditAccountOrDebitAccountCompleted(Account acc);
    @Query("SELECT p FROM PaymentHistory p WHERE p.status = 'COMPLETED' OR p.status = 'CANCELLED' order by p.historyTimeStamp desc ")
    List<PaymentHistory> findAllCompleted();
    @Query("SELECT p FROM PaymentHistory p WHERE p.status = 'BLOCKED_BY_FRAUD' order by p.historyTimeStamp desc ")
    List<PaymentHistory> findAllFraud();

    @Query("SELECT p FROM PaymentHistory p WHERE (p.creditAccount = ?1 OR p.debitAccount = ?1) AND " +
            " p.status = 'BLOCKED_BY_FRAUD' order by p.historyTimeStamp desc ")
    List<PaymentHistory> findByCreditAccountOrDebitAccountFraud(Account acc);
}
