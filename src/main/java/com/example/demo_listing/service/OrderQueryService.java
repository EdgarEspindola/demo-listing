package com.example.demo_listing.service;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo_listing.api.OrderListDTO;
import com.example.demo_listing.api.OrderListView;
import com.example.demo_listing.repo.OrderRepository;

import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
public class OrderQueryService {
  private final OrderRepository repo;
  public OrderQueryService(OrderRepository repo) { this.repo = repo; }

  public Slice<OrderListDTO> listDTO(String status, LocalDateTime from, LocalDateTime to, String q, int size, int page) {
    var sort = Sort.by("createdAt").descending().and(Sort.by("id").descending());
    var pageable = PageRequest.of(page, size, sort);
    return repo.searchDTO(status, from, to, q, pageable);
  }

  public Slice<OrderListView> listView(String status, LocalDateTime from, LocalDateTime to, String q, int size, int page) {
    var sort = Sort.by("createdAt").descending().and(Sort.by("id").descending());
    var pageable = PageRequest.of(page, size, sort);
    return repo.searchView(status, from, to, q, pageable);
  }
}