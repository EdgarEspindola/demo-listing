package com.example.demo_listing.repo;

import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import com.example.demo_listing.api.OrderFilter;
import com.example.demo_listing.api.OrderListFullDTO;
import com.example.demo_listing.api.OrderListLightDTO;
import com.example.demo_listing.domain.Customer;
import com.example.demo_listing.domain.Payment;

import java.util.*;
// Criteria API (Repo custom)
@Repository
public class OrderQueryRepository {
    private final EntityManager em;
  public OrderQueryRepository(EntityManager em){ this.em = em; }

  // -------- Light: sin Payment en el SELECT (join condicional a Payment solo si hace falta) --------
  public Slice<OrderListLightDTO> searchLight(OrderFilter f, Pageable pageable) {
    var cb = em.getCriteriaBuilder();
    var cq = cb.createQuery(OrderListLightDTO.class);

    Root<Order> o = cq.from(Order.class);
    Join<Order, Customer> c = o.join("customer", JoinType.INNER);

    // JOIN condicional a Payment SOLO si hay filtros por payment*
    Join<Order, Payment> p = null;
    boolean needPaymentJoin = (f.paymentMethod()!=null && !f.paymentMethod().isBlank())
                           || (f.paymentStatus()!=null && !f.paymentStatus().isBlank());
    if (needPaymentJoin) p = o.join("payment", JoinType.LEFT);

    cq.select(cb.construct(OrderListLightDTO.class,
      o.get("id"), o.get("createdAt"), o.get("totalAmount"), o.get("status"),
      c.get("name"), c.get("email")
    ));

    var preds = new ArrayList<Predicate>();

    // Filtros opcionales (AND)
    if (f.status()!=null) preds.add(cb.equal(o.get("status"), f.status()));
    if (f.statuses()!=null && !f.statuses().isEmpty()) preds.add(o.get("status").in(f.statuses()));
    if (f.from()!=null) preds.add(cb.greaterThanOrEqualTo(o.get("createdAt"), f.from()));
    if (f.to()!=null)   preds.add(cb.lessThan(o.get("createdAt"), f.to()));
    if (f.minAmount()!=null) preds.add(cb.ge(o.get("totalAmount"), f.minAmount()));
    if (f.maxAmount()!=null) preds.add(cb.le(o.get("totalAmount"), f.maxAmount()));
    if (f.customerEmail()!=null && !f.customerEmail().isBlank()) {
      preds.add(cb.like(cb.lower(cb.coalesce(c.get("email"), "")),
                        "%"+f.customerEmail().toLowerCase()+"%"));
    }

    // Combinaciones variables (OR block) para búsqueda en varios campos
    if (f.q()!=null && !f.q().isBlank()) {
      var like = "%"+f.q().toLowerCase()+"%";
      var orBlock = cb.or(
        cb.like(cb.lower(cb.coalesce(c.get("name"), "")),  like),
        cb.like(cb.lower(cb.coalesce(c.get("email"), "")), like)
      );
      preds.add(orBlock);
    }

    // Si hay filtros de Payment, ahora sí usar el join condicional
    if (needPaymentJoin) {
      if (f.paymentMethod()!=null && !f.paymentMethod().isBlank())
        preds.add(cb.equal(p.get("method"), f.paymentMethod()));
      if (f.paymentStatus()!=null && !f.paymentStatus().isBlank())
        preds.add(cb.equal(p.get("status"), f.paymentStatus()));
    }

    cq.where(preds.toArray(Predicate[]::new));
    cq.orderBy(cb.desc(o.get("createdAt")), cb.desc(o.get("id")));

    var q = em.createQuery(cq);
    q.setFirstResult((int) pageable.getOffset());
    q.setMaxResults(pageable.getPageSize());
    var content = q.getResultList();

    // Slice: simple indicador de "hay más"
    boolean hasMore = content.size() == pageable.getPageSize();
    return new SliceImpl<>(content, pageable, hasMore);
  }


  // -------- Full: con Payment en el SELECT, usando LEFT JOIN + ON condicional --------
  public Slice<OrderListFullDTO> searchFull(OrderFilter f, Pageable pageable) {
    var cb = em.getCriteriaBuilder();
    var cq = cb.createQuery(OrderListFullDTO.class);

    Root<Order> o = cq.from(Order.class);
    Join<Order, Customer> c = o.join("customer", JoinType.INNER);
    Join<Order, Payment> p  = o.join("payment", JoinType.LEFT);

    // ON condicional (JPA 2.1+): el filtro de payment se evalúa en el ON solo si viene
    var onConds = new ArrayList<Predicate>();
    if (f.paymentMethod()!=null && !f.paymentMethod().isBlank())
      onConds.add(cb.equal(p.get("method"), f.paymentMethod()));
    if (f.paymentStatus()!=null && !f.paymentStatus().isBlank())
      onConds.add(cb.equal(p.get("status"), f.paymentStatus()));
    if (!onConds.isEmpty()) p.on(onConds.toArray(Predicate[]::new));

    cq.select(cb.construct(OrderListFullDTO.class,
      o.get("id"), o.get("createdAt"), o.get("totalAmount"), o.get("status"),
      c.get("name"), c.get("email"),
      p.get("method"), p.get("status")
    ));

    var preds = new ArrayList<Predicate>();
    if (f.status()!=null) preds.add(cb.equal(o.get("status"), f.status()));
    if (f.statuses()!=null && !f.statuses().isEmpty()) preds.add(o.get("status").in(f.statuses()));
    if (f.from()!=null) preds.add(cb.greaterThanOrEqualTo(o.get("createdAt"), f.from()));
    if (f.to()!=null)   preds.add(cb.lessThan(o.get("createdAt"), f.to()));

    if (f.q()!=null && !f.q().isBlank()) {
      var like = "%"+f.q().toLowerCase()+"%";
      preds.add(cb.or(
        cb.like(cb.lower(cb.coalesce(c.get("name"), "")),  like),
        cb.like(cb.lower(cb.coalesce(c.get("email"), "")), like)
      ));
    }

    cq.where(preds.toArray(Predicate[]::new));
    cq.orderBy(cb.desc(o.get("createdAt")), cb.desc(o.get("id")));

    var q = em.createQuery(cq)
              .setFirstResult((int) pageable.getOffset())
              .setMaxResults(pageable.getPageSize());
    var content = q.getResultList();
    boolean hasMore = content.size() == pageable.getPageSize();
    return new SliceImpl<>(content, pageable, hasMore);


    }
}
