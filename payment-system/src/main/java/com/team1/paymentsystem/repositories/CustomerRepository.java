package com.team1.paymentsystem.repositories;

import com.team1.paymentsystem.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface CustomerRepository extends JpaRepository<Customer,Long> {
    Optional<Customer> findById(long id);
    Optional<Customer> findByEmail(String email);
    Optional<Customer> findByPhoneNumber(String phoneNumber);
    @Query("select c from Customer c where c.nextStateId is not null ")
    List<Customer> findNeedsApproval();
}
