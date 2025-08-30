package com.example.demo_listing.api;

// api/OrderListDTO.java (record/DTO con constructor expression)
import java.time.LocalDateTime;

public record OrderListDTO(
  Long orderId,
  LocalDateTime createdAt,
  Double totalAmount,
  String status,
  String customerName,
  String customerEmail,
  String paymentMethod,
  String paymentStatus
) {}