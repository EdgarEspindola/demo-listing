// api/OrderListFullDTO.java  (con Payment)
package com.example.demo_listing.api;

import java.time.LocalDateTime;

public record OrderListFullDTO(
  Long orderId, LocalDateTime createdAt, Double totalAmount, String status,
  String customerName, String customerEmail,
  String paymentMethod, String paymentStatus
) {}