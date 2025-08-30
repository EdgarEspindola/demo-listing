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

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public Double getTotalAmount() {
    return totalAmount;
  }

  public void setTotalAmount(Double totalAmount) {
    this.totalAmount = totalAmount;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Customer getCustomer() {
    return customer;
  }

  public void setCustomer(Customer customer) {
    this.customer = customer;
  }

  public Payment getPayment() {
    return payment;
  }

  public void setPayment(Payment payment) {
    this.payment = payment;
  }

  // getters/setters
  
}