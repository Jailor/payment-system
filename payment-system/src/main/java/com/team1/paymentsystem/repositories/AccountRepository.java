package com.team1.paymentsystem.repositories;

import com.team1.paymentsystem.entities.Account;
import com.team1.paymentsystem.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account,Long> {
    Optional<Account> findByAccountNumber(String accountNumber);
    Optional<List<Account>> findByOwner(User owner);
    @Query("select a from Account a where a.nextStateId is not NULL")
    List<Account> findNeedsApproval();
    @Query("select a from Account a where a.owner.email = ?1")
    List<Account> findByEmail(String email);
}
