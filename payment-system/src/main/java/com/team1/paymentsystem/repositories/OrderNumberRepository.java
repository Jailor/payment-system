package com.team1.paymentsystem.repositories;

import com.team1.paymentsystem.entities.OrderNumber;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface OrderNumberRepository  extends JpaRepository<OrderNumber,Long> {
    Optional<OrderNumber> findByDate(LocalDate date);
}
