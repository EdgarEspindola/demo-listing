package com.example.demo_listing.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "payment")
public class Payment {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable=false) private String method; // CARD, CASH...
  @Column(nullable=false) private String status; // PAID, PENDING...
  public Long getId() {
    return id;
  }
  public void setId(Long id) {
    this.id = id;
  }
  public String getMethod() {
    return method;
  }
  public void setMethod(String method) {
    this.method = method;
  }
  public String getStatus() {
    return status;
  }
  public void setStatus(String status) {
    this.status = status;
  }
  
}