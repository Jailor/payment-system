package com.team1.paymentsystem.mappers.history;

import com.team1.paymentsystem.mappers.Mapper;
import com.team1.paymentsystem.mappers.entity.UserMapper;
import com.team1.paymentsystem.dto.user.UserDTO;
import com.team1.paymentsystem.dto.user.UserHistoryDTO;
import com.team1.paymentsystem.entities.User;
import com.team1.paymentsystem.entities.history.UserHistory;
import com.team1.paymentsystem.states.Operation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class UserHistoryMapper implements Mapper<UserHistoryDTO, UserHistory> {
    @Autowired
    UserMapper userMapper;
    @Override
    public UserHistoryDTO toDTO(UserHistory entity) {
        UserHistoryDTO userHistoryDTO = new UserHistoryDTO();
        BeanUtils.copyProperties(entity,userHistoryDTO);
        userHistoryDTO.setProfileName(entity.getProfile().getName());
        userHistoryDTO.setStringTimeStamp(entity.getTimeStamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return userHistoryDTO;
    }

    @Override
    public UserHistory toEntity(UserHistoryDTO dto, Operation operation) {
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(dto,userDTO);
        User user = userMapper.toEntity(userDTO, operation);
        UserHistory userHistory = new UserHistory();
        BeanUtils.copyProperties(dto,userHistory);
        userHistory.setId(0);
        userHistory.setOriginalId(user.getId());
        if(userHistory.getTimeStamp() == null){
            userHistory.setTimeStamp(LocalDateTime.now());
        }
        return userHistory;
    }
}
