package com.mayak.ddingcham.domain;


import com.mayak.ddingcham.domain.support.MaxCount;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Builder
@EqualsAndHashCode(of = "id")
@ToString
@Slf4j
public class Menu {

    static final boolean MENU_DELETED = true;
    static final boolean MENU_UN_DELETED = false;
    static final boolean MENU_LAST_USED = true;
    static final boolean MENU_NOT_LAST_USED = false;

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
    @Builder.Default
    private String imageUrl = "";

    private boolean deleted;

    @Embedded
    private MaxCount maxCount;

    private boolean lastUsed;

    public void deleteMenu() {
        this.deleted = MENU_DELETED;
    }

    public Menu setUpLastUsedStatus(MaxCount maxCount) {
        this.maxCount = maxCount;
        this.lastUsed = MENU_LAST_USED;
        return this;
    }

    public void dropLastUsedStatus() {
        lastUsed = MENU_NOT_LAST_USED;
    }

    public boolean isSameMenu(Menu other) {
        return isNotEmptyMenu(other) && name.equals(other.name) && price == other.price && description.equals(other.description);
    }

    private boolean isNotEmptyMenu(Menu other) {
        return other.name != null && other.description != null;
    }
}
