package com.team1.paymentsystem.repositories.history;

import com.team1.paymentsystem.entities.history.UserHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Repository
public interface UserHistoryRepository extends JpaRepository<UserHistory,Long> {
    Optional<List<UserHistory>> findByOriginalId(long originalId);
    Optional<UserHistory> findByTimeStampAndOriginalId(LocalDateTime timeStamp, long originalId);

}
