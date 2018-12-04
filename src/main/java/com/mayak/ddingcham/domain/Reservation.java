package com.mayak.ddingcham.domain;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.mayak.ddingcham.domain.support.MaxCount;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Builder
@EqualsAndHashCode(of = "id")
@ToString
@Slf4j
public class Reservation implements Serializable {

    private static final boolean ACTIVATED = true;
    private static final boolean DEACTIVATED = false;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Menu menu;

    @ManyToOne
    @ToString.Exclude
    private Store store;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "maxCount", column = @Column(nullable = false)),
            @AttributeOverride(name = "personalMaxCount", column = @Column(nullable = false))
    })
    @JsonUnwrapped //todo DTO 분리?
    private MaxCount maxCount;

    private LocalDate openDate;
    //todo menu deleted 상태

    private int availableCount;

    @JsonGetter("maxLimit") //todo handlebar에서 쓸 일 있으면 getter 로 ?
    public int calculateMaxLimit(){
        return this.availableCount < this.maxCount.getPersonalMaxCount() ? this.availableCount : this.maxCount.getPersonalMaxCount();
    }
    public void regist() {
        this.store.updateLastUsedMenu(menu, maxCount);
    }

    public void orderMenu(int count) {
        this.availableCount -= count;
    }

    public int calculatePrice(int itemCount) {
        return this.menu.calculatePrice(itemCount);
    }

    public void checkPossiblePurchase(int itemCount) {
        if(this.availableCount < itemCount)
            throw new RuntimeException("Cannot buy");
    }
}
