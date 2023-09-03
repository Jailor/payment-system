package com.team1.paymentsystem.repositories.history;

import com.team1.paymentsystem.entities.history.CustomerHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface CustomerHistoryRepository extends JpaRepository<CustomerHistory,Long> {
    Optional<List<CustomerHistory>> findByOriginalId(long originalId);
    Optional<CustomerHistory> findById(long id);
}
