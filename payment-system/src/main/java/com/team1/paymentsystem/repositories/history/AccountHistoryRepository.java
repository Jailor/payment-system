package com.team1.paymentsystem.repositories.history;

import com.team1.paymentsystem.entities.history.AccountHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AccountHistoryRepository extends JpaRepository<AccountHistory,Long> {
    Optional<AccountHistory> findById(long id);
    Optional<AccountHistory> findByTimeStampAndOriginalId(LocalDateTime timeStamp, long originalId);
    Optional<List<AccountHistory>> findByOriginalId(long originalId);

}
