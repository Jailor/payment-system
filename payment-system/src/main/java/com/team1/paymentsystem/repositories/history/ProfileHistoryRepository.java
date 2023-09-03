package com.team1.paymentsystem.repositories.history;

import com.team1.paymentsystem.entities.history.ProfileHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProfileHistoryRepository extends JpaRepository<ProfileHistory, Long> {
    Optional<ProfileHistory> findByTimeStamp(LocalDateTime timeStamp);
    Optional<ProfileHistory> findById(long originalId);
    Optional<ProfileHistory> findByTimeStampAndOriginalId(LocalDateTime timeStamp, long originalId);
    Optional<List<ProfileHistory>> findByOriginalId(long originalId);
}
