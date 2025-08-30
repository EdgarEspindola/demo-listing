package com.example.demo_listing.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders") // 'order' es reservada en SQL
public class Order {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable=false) private LocalDateTime createdAt;
  @Column(nullable=false) private Double totalAmount;
  @Column(nullable=false) private String status; // PAID, PENDING, etc.

  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="customer_id", nullable=false)
  private Customer customer;

  // Sencillo: el Order posee la relación a Payment
  @OneToOne(fetch = FetchType.LAZY) @JoinColumn(name="payment_id")
  private Payment payment;

  // getters/setters
}