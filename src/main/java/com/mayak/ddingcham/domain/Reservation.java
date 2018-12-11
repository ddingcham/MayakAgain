package com.mayak.ddingcham.domain;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.mayak.ddingcham.domain.support.MaxCount;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Builder
@EqualsAndHashCode(of = "id")
@ToString
@Slf4j
public class Reservation {

    static final boolean RESERVATION_ACTIVATED = true;
    static final boolean RESERVATION_DEACTIVATED = false;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
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

    public void orderMenu(int count) {
        this.availableCount -= count;
    }

    public void checkPossiblePurchase(int itemCount) {
        if (this.availableCount < itemCount) {
            throw new IllegalStateException("Cannot buy");
        }
    }

    public boolean isSameId(long reservationId) {
        return getId() == reservationId;
    }
}
