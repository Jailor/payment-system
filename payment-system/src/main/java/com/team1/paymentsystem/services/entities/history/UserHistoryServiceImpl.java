package com.team1.paymentsystem.services.entities.history;

import com.team1.paymentsystem.entities.User;
import com.team1.paymentsystem.entities.history.UserHistory;
import com.team1.paymentsystem.repositories.history.UserHistoryRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
@Service
public class UserHistoryServiceImpl implements UserHistoryService{
    @Autowired
    UserHistoryRepository userHistoryRepository;

    @Override
    public UserHistory createHistory(User user) {
        UserHistory userHistory = new UserHistory();
        BeanUtils.copyProperties(user, userHistory);
        userHistory.setId(0);
        userHistory.setOriginalId(user.getId());
        userHistory.setTimeStamp(LocalDateTime.now());
        return userHistory;
    }

    @Override
    public UserHistory save(UserHistory userHistory){
        userHistoryRepository.save(userHistory);
        return findByTimeStampAndOriginalId(userHistory.getTimeStamp(), userHistory.getOriginalId());
    }

    @Override
    public List<UserHistory> findAll() {
        return userHistoryRepository.findAll();
    }

    @Override
    public List<UserHistory> findByOriginalId(long originalId) {
        return userHistoryRepository.findByOriginalId(originalId).orElse(null);
    }

    @Override
    public UserHistory findById(long id) {
        return userHistoryRepository.findById(id).orElse(null);
    }

    private UserHistory findByTimeStampAndOriginalId(LocalDateTime timeStamp, long originalId) {
        return userHistoryRepository.findByTimeStampAndOriginalId(timeStamp, originalId).orElse(null);
    }
}
