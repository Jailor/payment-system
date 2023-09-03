package com.team1.paymentsystem.services.entities;

import com.team1.paymentsystem.dto.user.UserDTO;
import com.team1.paymentsystem.entities.User;

import java.util.List;

public interface UserService extends GeneralService<User, UserDTO> {
    User save(User user);
    User update(User user);
    User remove(User user);
    List<User> findAll();
    User findByUsername(String username);
    User findById(long id);
    List<User> findByEmail(String email);
}
