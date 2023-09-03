package com.team1.paymentsystem.repositories;

import com.team1.paymentsystem.entities.Account;
import com.team1.paymentsystem.entities.Balance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BalanceRepository extends JpaRepository<Balance,Long> {
    Optional<Balance> findById(long id);
    Optional<List<Balance>> findByAccount(Account account);
    @Query("select b from Balance b where b.account = ?1 order by b.timeStamp DESC limit 1")
    Balance findLastBalanceByAccount(Account account);



}
