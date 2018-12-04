package com.mayak.ddingcham.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mayak.ddingcham.domain.support.MaxCount;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Builder
@EqualsAndHashCode(of = "id")
@ToString
@Slf4j
public class Menu {

    private static final boolean LAST_USED = true;
    private static final boolean NOT_LAST_USED = false;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int price;

    private String description;

    @Column(nullable = false, length = 400)
    private String imageUrl;

    private boolean deleted;

    @Embedded
    private MaxCount maxCount;

    @ManyToOne(optional = false)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_menu_store"), nullable = false)
    @ToString.Exclude
    @JsonIgnore
    private Store store;

    private boolean lastUsed;

    public boolean isEqualStore(Store store) {
        return this.store.equals(store);
    }

    public void deleteMenu() {
        this.deleted = true;
    }

    public void setUpLastUsedStatus(MaxCount maxCount) {
        this.maxCount = maxCount;
        this.lastUsed = LAST_USED;
    }

    public void dropLastUsedStatus() {
        lastUsed = NOT_LAST_USED;
    }

    public boolean isLastUsed() {
        return lastUsed;
    }

    public int calculatePrice(int itemCount) {
        return this.price * itemCount;
    }

    public boolean hasSameStore(Store store) {
        return this.store.equals(store);
    }
}
