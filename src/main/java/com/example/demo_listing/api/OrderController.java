package com.example.demo_listing.api;

import org.springframework.web.bind.annotation.*;

import com.example.demo_listing.service.OrderQueryService;

import org.springframework.data.domain.Slice;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/orders")
public class OrderController {
  private final OrderQueryService service;
  public OrderController(OrderQueryService service) { this.service = service; }

  // Listado con DTO (recomendado):
  @GetMapping
  public Slice<OrderListDTO> list(
      @RequestParam(required=false) String status,
      @RequestParam(required=false) String from,
      @RequestParam(required=false) String to,
      @RequestParam(required=false) String q,
      @RequestParam(defaultValue="20") int size,
      @RequestParam(defaultValue="0") int page) {

    var f = (from == null || from.isBlank()) ? null : LocalDateTime.parse(from);
    var t = (to   == null || to.isBlank())   ? null : LocalDateTime.parse(to);
    return service.listDTO(status, f, t, q, size, page);
  }
}