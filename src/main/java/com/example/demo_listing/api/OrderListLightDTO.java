// api/OrderListLightDTO.java  (sin campos de Payment)
package com.example.demo_listing.api;

import java.time.LocalDateTime;

public record OrderListLightDTO(
  Long orderId, LocalDateTime createdAt, Double totalAmount, String status,
  String customerName, String customerEmail
) {}