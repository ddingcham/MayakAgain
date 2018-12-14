package com.mayak.ddingcham.domain;

import com.mayak.ddingcham.domain.support.MaxCount;

import java.time.LocalDateTime;

public class FixtureUtils {
    public static Store unClosedStore() {
        return Store.builder()
                .timeToClose(LocalDateTime.MAX)
                .build();
    }

    public static Menu deletedMenu() {
        return Menu.builder()
                .name("deletedMenu")
                .deleted(Menu.MENU_DELETED)
                .build();
    }

    public static Menu unDeletedMenu() {
        return Menu.builder()
                .name("unDeletedMenu")
                .description("unDeletedMenu")
                .price(1)
                .imageUrl("/path")
                .build();
    }

    public static Menu lastUsedMenu() {
        return Menu.builder()
                .name("lastUsedMenu")
                .deleted(Menu.MENU_UN_DELETED)
                .lastUsed(Menu.MENU_LAST_USED)
                .build();
    }

    public static Menu notLastUsedMenu() {
        return Menu.builder()
                .name("notLastUsedMenu")
                .deleted(Menu.MENU_UN_DELETED)
                .lastUsed(Menu.MENU_NOT_LAST_USED)
                .build();
    }

    public static MaxCount defaultMaxCount() {
        return MaxCount.builder()
                .maxCount(1)
                .personalMaxCount(1)
                .build();
    }
}
