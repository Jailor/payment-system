package com.team1.paymentsystem.repositories;

import com.team1.paymentsystem.entities.Audit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuditRepository extends JpaRepository<Audit, Long> {
    @Query(value = "SELECT a FROM Audit a where a.objectId = ?1 AND a.className=?2 ORDER BY a.timeStamp DESC ")
    List<Audit> findUsersWhichApprovedCheck(Long objId,String className);
}
