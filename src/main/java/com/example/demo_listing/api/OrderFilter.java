// api/OrderFilter.java

package com.example.demo_listing.api;
import java.time.LocalDateTime;
import java.util.List;

public record OrderFilter(
  String status,
  List<String> statuses,          // IN (...)
  LocalDateTime from, LocalDateTime to, // rango fechas
  String q,                        // búsqueda (name/email)
  String customerEmail,
  Double minAmount, Double maxAmount,
  String paymentMethod, String paymentStatus
) {}
