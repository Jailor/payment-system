package com.team1.paymentsystem.repositories;

import com.team1.paymentsystem.entities.Account;
import com.team1.paymentsystem.entities.Payment;
import com.team1.paymentsystem.states.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment,Long> {
    Optional<Payment> findById(long id);
    Optional<Payment> findBySystemReference(String systemReference);
    List<Payment> findByStatus(PaymentStatus status);
    @Query("SELECT p FROM Payment p WHERE p.status IN ?1")
    List<Payment> findByStatuses(List<PaymentStatus> statuses);
    @Query("SELECT p FROM Payment p WHERE p.creditAccount = ?1 OR p.debitAccount = ?1  ORDER BY p.timeStamp desc")
    List<Payment> findByCreditAccountOrDebitAccount(Account acc);
    @Query("SELECT p FROM Payment p WHERE p.creditAccount.accountNumber = ?1 AND p.status = 'COMPLETED' ORDER BY p.amount DESC")
    List<Payment> findByCreditAccountNumberOrderByAmountCompleted(String creditAccountNumber);
    @Query("SELECT p FROM Payment p WHERE p.debitAccount.accountNumber = ?1 AND p.status = 'COMPLETED' ORDER BY p.amount DESC")
    List<Payment> findByDebitAccountNumberOrderByAmountCompleted(String debitAccountNumber);
    @Query("SELECT p FROM Payment p WHERE p.debitAccount.accountNumber = ?1 AND p.status = 'COMPLETED' ORDER BY p.timeStamp desc")
    List<Payment> findByDebitAccountNumberOrderByTimeStampCompleted(String debitAccountNumber);
    @Query("SELECT p FROM Payment p WHERE p.debitAccount.accountNumber = ?1 AND p.status = 'COMPLETED' ORDER BY p.timeStamp desc LIMIT 1")
    Optional<Payment> findLastByDebitAccount(String debitAccountNumber);
    @Query("SELECT p FROM Payment p order by p.timeStamp desc ")
    List<Payment> findAllOrdered();
    @Query("SELECT p FROM Payment p WHERE p.status = 'COMPLETED' OR p.status = 'CANCELLED' order by p.timeStamp desc")
    List<Payment> findAllOrderedCompleted();

    @Query("SELECT p FROM Payment p WHERE " +
            "(p.creditAccount.owner.email = ?1 OR p.debitAccount.owner.email = ?1) order by p.timeStamp desc")
    List<Payment> findByEmail(String email);

    @Query("SELECT p FROM Payment p WHERE " +
            "(p.creditAccount.accountNumber = ?1 OR p.debitAccount.accountNumber = ?1) " +
            "AND p.status IN ?2 order by p.timeStamp desc")
    List<Payment> findByStatusesAndAccount(String accountNumber, List<PaymentStatus> statuses);

    @Query("SELECT p FROM Payment p WHERE " +
            " p.debitAccount.accountNumber = ?1 " +
            "AND p.status IN ?2 order by p.timeStamp desc")
    List<Payment> findByStatusesAndDebitAccount(String accountNumber, List<PaymentStatus> statuses);
}
