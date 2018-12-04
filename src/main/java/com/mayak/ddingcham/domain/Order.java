package com.mayak.ddingcham.domain;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@ToString
@Table(name = "order_table")
public class Order{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "name", column = @Column(nullable = false)),
            @AttributeOverride(name = "phoneNumber", column = @Column(nullable = false))
    })
    @JsonUnwrapped
    private Customer customer;

    @ManyToOne
    private Store store;

    private LocalDateTime createdDate;

    private LocalDateTime pickupTime;

    private int orderTotalPrice;
  
    private Boolean isPickedup;

    @OneToMany(mappedBy = "order", cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Builder
    public Order(Customer customer, Store store, LocalDateTime pickupTime) {
        this.customer = customer;
        this.store = store;
        this.createdDate = LocalDateTime.now();
        this.pickupTime = pickupTime;
        this.orderTotalPrice = 0;
        this.isPickedup = false;
    }

    public void addOrderItem(OrderItem orderItem) {
        this.orderItems.add(orderItem);
        this.orderTotalPrice += orderItem.getItemTotalPrice();
    }

    public void setIsPickedupByOrder(Order inputOrder) {
        if(inputOrder.isPickedup)
            this.isPickedup = false;
        else
            this.isPickedup = true;
    }

    public boolean hasSameStore(Store store) {
        return this.store.equals(store);
    }
}
