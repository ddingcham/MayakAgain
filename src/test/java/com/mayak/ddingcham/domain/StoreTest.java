package com.mayak.ddingcham.domain;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.SoftAssertions;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;

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

        assertThat(store.hasMenu(addedMenu)).isTrue();
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
    public void store_update() {
        Store newInfo = Store.builder()
                .description("new description")
                .imgURL("newimg")
                .phoneNumber("01012341234")
                .postCode("43210")
                .serviceDescription("update create menu 가게관점")
                .address("new ADDRESS")
                .build();
        store.updateStore(newInfo);
        log.debug("store : {}", store);
        log.debug("newInfo : {}", newInfo);

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
        //When
        List<Reservation> reservations = null;
        LocalDateTime timeToClose = LocalDateTime.now();
        store.activate(timeToClose);
        store.deactivate();
        assertThat(store.isOpen()).isFalse();
    }
}
