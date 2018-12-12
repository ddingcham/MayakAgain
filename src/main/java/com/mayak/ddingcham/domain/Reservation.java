package com.mayak.ddingcham.domain;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.mayak.ddingcham.domain.support.MaxCount;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Data
@EqualsAndHashCode(of = "id")
@ToString
@Slf4j
public class Reservation {

    static final boolean RESERVATION_ACTIVATED = true;
    static final boolean RESERVATION_DEACTIVATED = false;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(cascade = CascadeType.ALL)
    private Menu menu;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "maxCount", column = @Column(nullable = false)),
            @AttributeOverride(name = "personalMaxCount", column = @Column(nullable = false))
    })
    @JsonUnwrapped
    private MaxCount maxCount;

    private LocalDate openDate;

    private int availableCount;

    private boolean activated;

    Reservation(){}

    @Builder
    Reservation(Menu menu, MaxCount maxCount, LocalDate openDate, boolean activated) {
        this.menu = menu;
        this.maxCount = maxCount;
        this.openDate = openDate;
        this.availableCount = maxCount.getMaxCount();
        this.activated = activated;
    }

    public void orderMenu(int count) {
        this.availableCount -= count;
    }

    public Reservation checkPossiblePurchase(int itemCount) {
        if (this.availableCount < itemCount) {
            throw new IllegalArgumentException("itemCount가 너무 많음");
        }
        availableCount -= itemCount;
        return this;
    }

    public boolean isSameId(long reservationId) {
        return getId() == reservationId;
    }
}
