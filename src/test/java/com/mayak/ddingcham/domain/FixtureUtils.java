package com.mayak.ddingcham.domain;

import com.mayak.ddingcham.domain.support.MaxCount;

import java.time.LocalDateTime;

public class FixtureUtils {
    static Store unClosedStore() {
        return Store.builder()
                .timeToClose(LocalDateTime.MAX)
                .build();
    }

    static Menu deletedMenu() {
        return Menu.builder()
                .name("deletedMenu")
                .deleted(Menu.MENU_DELETED)
                .build();
    }

    static Menu unDeletedMenu() {
        return Menu.builder()
                .name("unDeletedMenu")
                .build();
    }

    static Menu lastUsedMenu() {
        return Menu.builder()
                .name("lastUsedMenu")
                .deleted(Menu.MENU_UN_DELETED)
                .lastUsed(Menu.MENU_LAST_USED)
                .build();
    }

    static Menu notLastUsedMenu() {
        return Menu.builder()
                .name("notLastUsedMenu")
                .deleted(Menu.MENU_UN_DELETED)
                .build();
    }

    static MaxCount defaultMaxCount() {
        return MaxCount.builder()
                .maxCount(1)
                .personalMaxCount(1)
                .build();
    }
}
