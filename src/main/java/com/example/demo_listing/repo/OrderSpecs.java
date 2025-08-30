package com.example.demo_listing.repo;

import org.springframework.data.jpa.domain.Specification;

import com.example.demo_listing.domain.Customer;
import com.example.demo_listing.domain.Payment;

import jakarta.persistence.criteria.*;

/*
 * Útil para reusar filtros y testear cada uno. Aquí devolvemos entidades con fetch join para evitar N+1 y luego mapeamos a DTO. 
 * (Para listados gigantes, prefiero el repo Criteria anterior que ya selecciona DTO).
 */
public interface OrderSpecs {
    static Specification<Order> statusEq(String status) {
    return (root, query, cb) -> status == null ? cb.conjunction() : cb.equal(root.get("status"), status);
  }

  static Specification<Order> statusesIn(java.util.List<String> statuses) {
    return (root, query, cb) -> (statuses == null || statuses.isEmpty()) ? cb.conjunction() : root.get("status").in(statuses);
  }

  static Specification<Order> createdBetween(java.time.LocalDateTime from, java.time.LocalDateTime to) {
    return (root, query, cb) -> {
      var p = cb.conjunction();
      if (from != null) p = cb.and(p, cb.greaterThanOrEqualTo(root.get("createdAt"), from));
      if (to   != null) p = cb.and(p, cb.lessThan(root.get("createdAt"), to));
      return p;
    };
  }

  static Specification<Order> qInCustomer(String q) {
    return (root, query, cb) -> {
      if (q == null || q.isBlank()) return cb.conjunction();
      // evitar N+1: si devolvemos entidades, hacemos fetch join (solo en selects, no en count)
      if (!Long.class.equals(query.getResultType()) && !long.class.equals(query.getResultType())) {
        root.fetch("customer", JoinType.INNER);
        root.fetch("payment", JoinType.LEFT);
        query.distinct(true);
      }
      Join<Order, Customer> c = root.join("customer", JoinType.INNER);
      var like = "%"+q.toLowerCase()+"%";
      return cb.or(
        cb.like(cb.lower(c.get("name")),  like),
        cb.like(cb.lower(c.get("email")), like)
      );
    };
  }

  static Specification<Order> paymentFilters(String method, String status) {
    return (root, query, cb) -> {
      if ((method == null || method.isBlank()) && (status == null || status.isBlank())) return cb.conjunction();
      Join<Order, Payment> p = root.join("payment", JoinType.LEFT);
      var conj = cb.conjunction();
      if (method != null && !method.isBlank()) conj = cb.and(conj, cb.equal(p.get("method"), method));
      if (status != null && !status.isBlank()) conj = cb.and(conj, cb.equal(p.get("status"), status));
      return conj;
    };
  }
}
