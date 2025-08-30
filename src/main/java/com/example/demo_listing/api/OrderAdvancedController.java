package com.example.demo_listing.api;

import org.springframework.data.domain.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import com.example.demo_listing.repo.OrderQueryDslRepository;
import com.example.demo_listing.repo.OrderQueryRepository;
import com.example.demo_listing.service.OrderSpecService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/orders-adv")
public class OrderAdvancedController {

    private final OrderQueryRepository criteriaRepo;
  private final OrderQueryDslRepository qdslRepo;
  private final OrderSpecService specService;

  public OrderAdvancedController(OrderQueryRepository criteriaRepo,
                                 OrderQueryDslRepository qdslRepo,
                                 OrderSpecService specService) {
    this.criteriaRepo = criteriaRepo;
    this.qdslRepo = qdslRepo;
    this.specService = specService;
}

    // ---- Criteria (Light) con join condicional a Payment ----
  @GetMapping("/criteria/light")
  public Slice<OrderListLightDTO> criteriaLight(
      @RequestParam(required=false) String status,
      @RequestParam(required=false) List<String> statuses,
      @RequestParam(required=false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
      @RequestParam(required=false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
      @RequestParam(required=false) String q,
      @RequestParam(required=false) String customerEmail,
      @RequestParam(required=false) Double minAmount,
      @RequestParam(required=false) Double maxAmount,
      @RequestParam(required=false) String paymentMethod,
      @RequestParam(required=false) String paymentStatus,
      @RequestParam(defaultValue="0") int page,
      @RequestParam(defaultValue="20") int size
  ) {
    var filter = new OrderFilter(
      norm(status), statuses, from, to, norm(q), norm(customerEmail),
      minAmount, maxAmount, norm(paymentMethod), norm(paymentStatus)
    );
    Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending().and(Sort.by("id").descending()));
    return criteriaRepo.searchLight(filter, pageable);
  }

  // ---- Criteria (Full) con LEFT JOIN + ON condicional ----
  @GetMapping("/criteria/full")
  public Slice<OrderListFullDTO> criteriaFull(
      @RequestParam(required=false) String status,
      @RequestParam(required=false) List<String> statuses,
      @RequestParam(required=false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
      @RequestParam(required=false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
      @RequestParam(required=false) String q,
      @RequestParam(required=false) String customerEmail,
      @RequestParam(required=false) Double minAmount,
      @RequestParam(required=false) Double maxAmount,
      @RequestParam(required=false) String paymentMethod,
      @RequestParam(required=false) String paymentStatus,
      @RequestParam(defaultValue="0") int page,
      @RequestParam(defaultValue="20") int size
  ) {
    var filter = new OrderFilter(
      norm(status), statuses, from, to, norm(q), norm(customerEmail),
      minAmount, maxAmount, norm(paymentMethod), norm(paymentStatus)
    );
    Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending().and(Sort.by("id").descending()));
    return criteriaRepo.searchFull(filter, pageable);
  }

  // ---- QueryDSL (Light) con join condicional ----
  @GetMapping("/querydsl/light")
  public Slice<OrderListLightDTO> qdslLight(
      @RequestParam(required=false) String status,
      @RequestParam(required=false) List<String> statuses,
      @RequestParam(required=false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
      @RequestParam(required=false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
      @RequestParam(required=false) String q,
      @RequestParam(required=false) String customerEmail,
      @RequestParam(required=false) Double minAmount,
      @RequestParam(required=false) Double maxAmount,
      @RequestParam(required=false) String paymentMethod,
      @RequestParam(required=false) String paymentStatus,
      @RequestParam(defaultValue="0") int page,
      @RequestParam(defaultValue="20") int size
  ) {
    var filter = new OrderFilter(
      norm(status), statuses, from, to, norm(q), norm(customerEmail),
      minAmount, maxAmount, norm(paymentMethod), norm(paymentStatus)
    );
    Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending().and(Sort.by("id").descending()));
    return qdslRepo.searchLight(filter, pageable);
  }

  // ---- QueryDSL (Full) con Payment en SELECT ----
  @GetMapping("/querydsl/full")
  public Slice<OrderListFullDTO> qdslFull(
      @RequestParam(required=false) String status,
      @RequestParam(required=false) List<String> statuses,
      @RequestParam(required=false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
      @RequestParam(required=false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
      @RequestParam(required=false) String q,
      @RequestParam(required=false) String customerEmail,
      @RequestParam(required=false) Double minAmount,
      @RequestParam(required=false) Double maxAmount,
      @RequestParam(required=false) String paymentMethod,
      @RequestParam(required=false) String paymentStatus,
      @RequestParam(defaultValue="0") int page,
      @RequestParam(defaultValue="20") int size
  ) {
    var filter = new OrderFilter(
      norm(status), statuses, from, to, norm(q), norm(customerEmail),
      minAmount, maxAmount, norm(paymentMethod), norm(paymentStatus)
    );
    Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending().and(Sort.by("id").descending()));
    return qdslRepo.searchFull(filter, pageable);
  }

  // ---- Specifications (devuelve Page porque calcula count) ----
  @GetMapping("/specs/full")
  public Page<OrderListFullDTO> specsFull(
      @RequestParam(required=false) String status,
      @RequestParam(required=false) List<String> statuses,
      @RequestParam(required=false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
      @RequestParam(required=false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
      @RequestParam(required=false) String q,
      @RequestParam(required=false) String customerEmail,
      @RequestParam(required=false) Double minAmount,
      @RequestParam(required=false) Double maxAmount,
      @RequestParam(required=false) String paymentMethod,
      @RequestParam(required=false) String paymentStatus,
      @RequestParam(defaultValue="0") int page,
      @RequestParam(defaultValue="20") int size
  ) {
    var filter = new OrderFilter(
      norm(status), statuses, from, to, norm(q), norm(customerEmail),
      minAmount, maxAmount, norm(paymentMethod), norm(paymentStatus)
    );
    Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending().and(Sort.by("id").descending()));
    return specService.search(filter, pageable);
  }

  private static String norm(String s) { return (s==null || s.isBlank()) ? null : s; }
  

}
