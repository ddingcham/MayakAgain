package com.mayak.ddingcham.domain;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.SoftAssertions;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Java6Assertions.assertThat;

@Slf4j
public class StoreTest {

    private Store store;
    private SoftAssertions softly = new SoftAssertions();

    @Before
    public void setUp() {
        store = Store.builder().build();
        store.addMenu(Menu.builder().build());
    }


    @Test
    public void addMenu() {
        Menu addedMenu = Menu.builder()
                .name("addedMenu")
                .build();
        store.addMenu(addedMenu);

        assertThat(store.hasMenuNotDeleted(addedMenu)).isTrue();
    }

    @Test(expected = IllegalArgumentException.class)
    public void addMenu_중복() {
        Menu addedMenu = Menu.builder()
                .name("addedMenu")
                .build();
        store.addMenu(addedMenu);
        store.addMenu(addedMenu);
    }

    @Test
    public void removeMenu() {
        Menu removedMenu = Menu.builder()
                .id(1L)
                .name("removedMenu")
                .build();
        store.addMenu(removedMenu);
        store.removeMenu(removedMenu);
        assertThat(store.hasMenuNotDeleted(removedMenu)).isFalse();
    }

    @Test(expected = NoSuchElementException.class)
    public void removeMenu_없는메뉴() {
        Menu removedMenu = Menu.builder()
                .name("removedMenu")
                .build();
        store.removeMenu(removedMenu);
    }

    @Test(expected = NoSuchElementException.class)
    public void removeMenu_삭제된메뉴_재삭제() {
        Menu removedMenu = Menu.builder()
                .name("removedMenu")
                .build();
        store.addMenu(removedMenu);
        store.removeMenu(removedMenu);
        store.removeMenu(removedMenu);
    }

    @Test
    public void store_update() {
        log.debug("store : {}", store);
        Store newInfo = Store.builder()
                .description("new description")
                .imgURL("newimg")
                .phoneNumber("01012341234")
                .postCode("43210")
                .serviceDescription("update create menu 가게관점")
                .address("new ADDRESS")
                .build();
        store.updateStore(newInfo);
        log.debug("updated store : {}", store);

        softly.assertThat(store.getDescription()).as("메뉴변경").isEqualTo(newInfo.getDescription());
        softly.assertThat(store.getImgURL()).isEqualTo(newInfo.getImgURL());
        softly.assertThat(store.getPhoneNumber()).isEqualTo(newInfo.getPhoneNumber());
        softly.assertThat(store.getPostCode()).isEqualTo(newInfo.getPostCode());
        softly.assertThat(store.getServiceDescription()).isEqualTo(newInfo.getServiceDescription());
        softly.assertThat(store.getAddress()).isEqualTo(newInfo.getAddress());
        softly.assertAll();
    }

    @Test
    public void store_deactivate() {
        LocalDateTime timeToClose = LocalDateTime.now();
        store.activate(timeToClose);
        store.deactivate();
        assertThat(store.isOpen()).isFalse();
    }
}
