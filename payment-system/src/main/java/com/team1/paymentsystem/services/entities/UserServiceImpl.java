package com.team1.paymentsystem.services.entities;

import com.team1.paymentsystem.dto.user.UserDTO;
import com.team1.paymentsystem.entities.Audit;
import com.team1.paymentsystem.entities.User;
import com.team1.paymentsystem.mappers.entity.UserMapper;
import com.team1.paymentsystem.managers.response.ErrorInfo;
import com.team1.paymentsystem.repositories.AuditRepository;
import com.team1.paymentsystem.repositories.ProfileRepository;
import com.team1.paymentsystem.repositories.UserRepository;
import com.team1.paymentsystem.services.validation.UserValidator;
import com.team1.paymentsystem.states.Operation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service()
public class UserServiceImpl implements UserService{
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected ProfileRepository profileRepository;
    @Autowired
    protected UserMapper userMapper;
    @Autowired
    protected AuditRepository auditRepository;
    @Autowired
    protected UserValidator userValidator;

    @Override
    public User save(User user) {
        if(user!=null&& userRepository.findByUsername(user.getUsername()).isEmpty()){
          return userRepository.save(user);
        }
        return null;
    }

    @Override
    public User update(User user) {
        ///username is unique
        User fromDb = userRepository.findByUsername(user.getUsername()).orElse(null);
        if(fromDb==null){
            return null;
        }
        ///user can't be modified
        if(fromDb.getUsername().equals(user.getUsername())){
            BeanUtils.copyProperties(user,fromDb);
            return userRepository.save(fromDb);
        }
        return null;
    }
    @Override
    public User remove(User user) {
        User fromDb = userRepository.findByUsername(user.getUsername()).orElse(null);
        if(fromDb == null){
            return null;
        }
        userRepository.delete(fromDb);
        return fromDb;
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
    public User findById(long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public List<User> findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    public User findByDiscriminator(User user) {
        return userRepository.findByUsername(user.getUsername()).orElse(null);
    }

    @Override
    public User toEntity(UserDTO userDTO, Operation operation) {
        return userMapper.toEntity(userDTO, operation);
    }

    @Override
    public List<User> findNeedsApproval() {
        return userRepository.findNeedsApproval();
    }

    @Override
    public UserDTO toDTO(User entity) {
        return userMapper.toDTO(entity);
    }

    @Override
    public User makeCopy(User obj) {
        User user = new User();
        BeanUtils.copyProperties(obj, user);
        return user;
    }

    @Override
    public List<ErrorInfo> validate(User obj, Operation operation) {
        return userValidator.validate(obj, operation);
    }

    @Override
    public boolean fourEyesCheck(User obj, String username, int nEyesCheck) {
        int limit = nEyesCheck/2-1;
        List<Audit> audits = auditRepository.findUsersWhichApprovedCheck(obj.getId(),"USER");
        if(audits.size() > limit){
            audits = audits.subList(0,limit);
        }
        for(Audit audit : audits){
            if(audit.getUser().getUsername().equals(username)){
                return false;
            }
        }
        return true;
    }
}
