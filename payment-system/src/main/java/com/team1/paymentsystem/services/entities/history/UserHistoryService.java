package com.team1.paymentsystem.services.entities.history;

import com.team1.paymentsystem.entities.User;
import com.team1.paymentsystem.entities.history.UserHistory;

import java.util.List;

public interface UserHistoryService extends HistoryService<User, UserHistory>{
    UserHistory createHistory(User user);
    UserHistory save(UserHistory userHistory);
    List<UserHistory> findAll();
    List<UserHistory> findByOriginalId(long originalId);
    UserHistory findById(long id);
}
