package com.team1.paymentsystem.repositories;

import com.team1.paymentsystem.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findById(long id);
    Optional<User> findByUsername(String username);
    @Query("select u from User u where u.nextStateId is not NULL")
    List<User> findNeedsApproval();
    Optional<List<User>> findByEmail(String email);

}
