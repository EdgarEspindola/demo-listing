package com.example.demo_listing.repo;

import com.example.demo_listing.api.OrderFilter;
import com.example.demo_listing.api.OrderListFullDTO;
import com.example.demo_listing.api.OrderListLightDTO;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import java.util.List;

@Repository
public class OrderQueryDslRepository {
    private final JPAQueryFactory qf;

  public OrderQueryDslRepository(EntityManager em) { this.qf = new JPAQueryFactory(em); }

  // ---------- Light: sin Payment (join condicional a Payment solo si hay filtro) ----------
  public Slice<OrderListLightDTO> searchLight(OrderFilter f, Pageable pageable) { 
    QOrder o = QOrder.order;
    QCustomer c = QCustomer.customer;
    QPayment p = QPayment.payment;

    BooleanBuilder where = new BooleanBuilder();

    if (f.status()!=null) where.and(o.status.eq(f.status()));
    if (f.statuses()!=null && !f.statuses().isEmpty()) where.and(o.status.in(f.statuses()));
    if (f.from()!=null) where.and(o.createdAt.goe(f.from()));
    if (f.to()!=null)   where.and(o.createdAt.lt(f.to()));
    if (f.minAmount()!=null) where.and(o.totalAmount.goe(f.minAmount()));
    if (f.maxAmount()!=null) where.and(o.totalAmount.loe(f.maxAmount()));
    if (f.customerEmail()!=null && !f.customerEmail().isBlank())
      where.and(c.email.lower().like("%"+f.customerEmail().toLowerCase()+"%"));
    if (f.q()!=null && !f.q().isBlank())
      where.and(c.name.lower().like("%"+f.q().toLowerCase()+"%")
               .or(c.email.lower().like("%"+f.q().toLowerCase()+"%")));

    var query = qf
      .select(Projections.constructor(OrderListLightDTO.class,
        o.id, o.createdAt, o.totalAmount, o.status,
        c.name, c.email
      ))
      .from(o)
      .join(o.customer, c);

    // JOIN condicional a Payment si hay filtros por payment*
    if ((f.paymentMethod()!=null && !f.paymentMethod().isBlank())
     || (f.paymentStatus()!=null && !f.paymentStatus().isBlank())) {
      query.leftJoin(o.payment, p);
      if (f.paymentMethod()!=null && !f.paymentMethod().isBlank()) where.and(p.method.eq(f.paymentMethod()));
      if (f.paymentStatus()!=null && !f.paymentStatus().isBlank()) where.and(p.status.eq(f.paymentStatus()));
    }

    List<OrderListLightDTO> rows = query
      .where(where)
      .orderBy(o.createdAt.desc(), o.id.desc())
      .offset(pageable.getOffset())
      .limit(pageable.getPageSize())
      .fetch();

    boolean hasMore = rows.size() == pageable.getPageSize();
    return new SliceImpl<>(rows, pageable, hasMore);
  }

  // ---------- Full: con Payment en SELECT ----------
  public Slice<OrderListFullDTO> searchFull(OrderFilter f, Pageable pageable) {
    QOrder o = QOrder.order;
    QCustomer c = QCustomer.customer;
    QPayment p = QPayment.payment;

    BooleanBuilder where = new BooleanBuilder();
    if (f.status()!=null) where.and(o.status.eq(f.status()));
    if (f.from()!=null)   where.and(o.createdAt.goe(f.from()));
    if (f.to()!=null)     where.and(o.createdAt.lt(f.to()));
    if (f.q()!=null && !f.q().isBlank())
      where.and(c.name.lower().like("%"+f.q().toLowerCase()+"%")
               .or(c.email.lower().like("%"+f.q().toLowerCase()+"%")));
    if (f.paymentMethod()!=null && !f.paymentMethod().isBlank()) where.and(p.method.eq(f.paymentMethod()));
    if (f.paymentStatus()!=null && !f.paymentStatus().isBlank()) where.and(p.status.eq(f.paymentStatus()));

    List<OrderListFullDTO> rows = qf
      .select(Projections.constructor(OrderListFullDTO.class,
        o.id, o.createdAt, o.totalAmount, o.status,
        c.name, c.email, p.method, p.status
      ))
      .from(o)
      .join(o.customer, c)
      .leftJoin(o.payment, p)
      .where(where)
      .orderBy(o.createdAt.desc(), o.id.desc())
      .offset(pageable.getOffset())
      .limit(pageable.getPageSize())
      .fetch();

    boolean hasMore = rows.size() == pageable.getPageSize();
    return new SliceImpl<>(rows, pageable, hasMore);

  }

}
