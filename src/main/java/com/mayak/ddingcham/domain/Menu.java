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
    @Builder.Default
    private String name = "메뉴";

    @Column(nullable = false)
    @Builder.Default
    private int price = 0;

    @Builder.Default
    private String description = "none";

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

    public boolean isSameMenu(Menu other) {
        return isNotEmptyMenu(other) && name.equals(other.name) && price == other.price && description.equals(other.description);
    }

    private boolean isNotEmptyMenu(Menu other) {
        return other.name != null && other.description != null;
    }
}
