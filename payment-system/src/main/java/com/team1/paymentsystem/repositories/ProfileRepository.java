package com.team1.paymentsystem.repositories;


import com.team1.paymentsystem.entities.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfileRepository  extends JpaRepository<Profile, Long> {
    Optional<Profile> findByName(String name);
    Optional<Profile> findById(long id);
    @Query("select p from Profile p where p.nextStateId is not NULL")
    List<Profile> findNeedsApproval();

}
