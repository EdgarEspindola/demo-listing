package com.example.demo_listing.repo;

import com.example.demo_listing.api.OrderListView;
import com.example.demo_listing.api.OrderListDTO;
import com.example.demo_listing.domain.Order;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

  // Opción A: proyección por interfaz (rápida para UI)
  @Query("""
        select
          o.id as orderId,
          o.createdAt as createdAt,
          o.totalAmount as totalAmount,
          o.status as status,
          c.name as customerName,
          c.email as customerEmail,
          p.method as paymentMethod,
          p.status as paymentStatus
        from Order o
          join o.customer c
          left join o.payment p
        where (:status is null or o.status = :status)
          and (:from   is null or o.createdAt >= :from)
          and (:to     is null or o.createdAt <  :to)
        and (c.name is not null and lower(c.name) like lower(concat('%', :q, '%')))
        order by o.createdAt desc, o.id desc
      """)
  Slice<OrderListView> searchView(
      @Param("status") String status,
      @Param("from") LocalDateTime from,
      @Param("to") LocalDateTime to,
      @Param("q") String q,
      Pageable pageable);

  // Opción B: proyección por DTO (record) con constructor expression
  @Query("""
        select new com.example.demo_listing.api.OrderListDTO(
          o.id, o.createdAt, o.totalAmount, o.status,
          c.name, c.email, p.method, p.status
        )
        from Order o
          join o.customer c
          left join o.payment p
        where (:status is null or o.status = :status)
          and (:from is null or o.createdAt >= :from)
          and (:to is null or o.createdAt < :to)
          and (:q is null or lower(c.name) like lower(concat('%', :q, '%')))
      """)
  Slice<OrderListDTO> searchDTO(
      @Param("status") String status,
      @Param("from") LocalDateTime from,
      @Param("to") LocalDateTime to,
      @Param("q") String q,
      Pageable pageable);
}