package com.example.demo_listing.api;

import java.time.LocalDateTime;
// api/OrderListView.java (closed projection por interfaz)
public interface OrderListView {
  Long getOrderId();
  LocalDateTime getCreatedAt();
  Double getTotalAmount();
  String getStatus();
  String getCustomerName();
  String getCustomerEmail();
  String getPaymentMethod();
  String getPaymentStatus();
}