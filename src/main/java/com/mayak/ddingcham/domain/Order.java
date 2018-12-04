package com.mayak.ddingcham.domain;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Builder
@EqualsAndHashCode(of = "id")
@ToString
@Slf4j
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
