package com.team1.paymentsystem.services.entities.history;

import java.util.List;

public interface HistoryService<T, HIST> {
    HIST createHistory(T obj);
    HIST save(HIST hist);
    List<HIST> findAll();
    List<HIST> findByOriginalId(long originalId);
    HIST findById(long id);
}
