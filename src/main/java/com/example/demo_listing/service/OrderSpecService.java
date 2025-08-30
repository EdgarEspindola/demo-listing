package com.example.demo_listing.service;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo_listing.api.OrderFilter;
import com.example.demo_listing.api.OrderListFullDTO;
import com.example.demo_listing.repo.OrderRepository;
import com.example.demo_listing.repo.OrderSpecs;

@Service
@Transactional(readOnly = true)
public class OrderSpecService {
     private final OrderRepository repo;
  public OrderSpecService(OrderRepository repo){ this.repo = repo; }

  public Page<OrderListFullDTO> search(OrderFilter f, Pageable pageable) {
      var spec = OrderSpecs.statusEq(f.status())
        .and(OrderSpecs.statusesIn(f.statuses()))
        .and(OrderSpecs.createdBetween(f.from(), f.to()))
        .and(OrderSpecs.qInCustomer(f.q()))
        .and(OrderSpecs.paymentFilters(f.paymentMethod(), f.paymentStatus()));

    var page = repo.findAll(spec, pageable); // entidades con fetch
    
    return page.map(o -> new OrderListFullDTO(
      o.getId(), o.getCreatedAt(), o.getTotalAmount(), o.getStatus(),
      o.getCustomer().getName(), o.getCustomer().getEmail(),
      o.getPayment()!=null ? o.getPayment().getMethod() : null,
      o.getPayment()!=null ? o.getPayment().getStatus() : null
    ));
  }
}
