package com.example.demo_listing.repo;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo_listing.domain.Customer;

import java.util.*;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

  // (DEMO de N+1) findAll() y luego acceder c.getOrders() en un loop disparará N+1
  // List<Customer> findAll();

  // Solución con JOIN FETCH para detalle/listado simple
  @Query("select distinct c from Customer c join fetch c.orders where c.id = :id")
  Optional<Customer> findByIdWithOrders(@Param("id") Long id);
}