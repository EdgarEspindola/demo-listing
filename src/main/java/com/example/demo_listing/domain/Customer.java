package com.example.demo_listing.domain;

import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "customer")
public class Customer {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable=false) private String name;
  @Column(nullable=false, unique=true) private String email;

  @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
  private List<Order> orders = new ArrayList<>();

  // getters/setters
}