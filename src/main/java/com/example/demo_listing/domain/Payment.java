package com.example.demo_listing.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "payment")
public class Payment {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable=false) private String method; // CARD, CASH...
  @Column(nullable=false) private String status; // PAID, PENDING...

  // getters/setters
}