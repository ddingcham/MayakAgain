package com.mayak.ddingcham.domain;

import com.mayak.ddingcham.domain.support.MaxCount;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.SoftAssertions;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Java6Assertions.assertThat;

@Slf4j
public class StoreTest {

    private Store store;
    private SoftAssertions softly;

    @Before
    public void setUp() {
        store = Store.builder().build();
        softly = new SoftAssertions();
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
    public void addReservation() {
        Menu menuForReservation = unDeletedMenu();
        store.addMenu(menuForReservation);
        store.addReservation()
                .with(menuForReservation, defaultMaxCount());
        List<Reservation> reservations = store.getActiveReservations();
        log.debug("addedReservations : {}", reservations);
        assertThat(reservations)
                .allMatch(Reservation::isActivated);
    }

    @Test(expected = IllegalStateException.class)
    public void addReservation_Store가_열린_상태인_경우() {
        store = unClosedStore();
        Menu menuForReservation = unDeletedMenu();
        store.addMenu(menuForReservation);
        store.addReservation()
                .with(menuForReservation, defaultMaxCount());
    }

    public void test(){
        store = unClosedStore();
    }

    @Test(expected = NoSuchElementException.class)
    public void addReservation_삭제된_Menu에_대해서_생성할_경우() {
        Menu menuForReservation = deletedMenu();
        store.addMenu(menuForReservation);
        store.addReservation()
                .with(menuForReservation, defaultMaxCount());
    }

    @Test(expected = NoSuchElementException.class)
    public void addReservation_없는_Menu에_대해서_생성할_경우() {
        Menu menuForReservation = unDeletedMenu();
        store.addReservation()
                .with(menuForReservation, defaultMaxCount());
    }

    private Store unClosedStore() {
        return Store.builder()
                .timeToClose(LocalDateTime.MAX)
                .build();
    }

    private Menu deletedMenu() {
        return Menu.builder()
                .name("deletedMenu")
                .deleted(Menu.MENU_DELETED)
                .build();
    }

    private Menu unDeletedMenu() {
        return Menu.builder()
                .name("unDeletedMenu")
                .build();
    }

    private Menu lastUsedMenu() {
        return Menu.builder()
                .name("lastUsedMenu")
                .deleted(Menu.MENU_UN_DELETED)
                .lastUsed(Menu.MENU_LAST_USED)
                .build();
    }

    private Menu notLastUsedMenu() {
        return Menu.builder()
                .name("notLastUsedMenu")
                .deleted(Menu.MENU_UN_DELETED)
                .build();
    }

    private MaxCount defaultMaxCount(){
        return MaxCount.builder()
                .maxCount(1)
                .personalMaxCount(1)
                .build();
    }

    @Test
    @SuppressWarnings("deprecation")
    public void Store가_닫힌_상태가_될_때_활성화_상태의_Reservation들은_비활성화_상태로() {
        Menu menuForReservation = unDeletedMenu();
        store.addMenu(menuForReservation);
        store.addReservation()
                .with(menuForReservation, defaultMaxCount());
        List<Reservation> reservations = store.getActiveReservations();
        log.debug("before close addedReservations : {}", reservations);
        store.close();
        log.debug("after close addedReservations : {}", reservations);
        assertThat(reservations)
                .noneMatch(Reservation::isActivated);
    }

    @Test
    public void 새로운_Reservation이_등록될_때_해당하는_Menu들의_마지막_사용_여부_업데이트() {
        Menu menuForReservation = notLastUsedMenu();
        store.addMenu(menuForReservation);
        store.addReservation()
                .with(menuForReservation, defaultMaxCount());
        assertThat(store.getLastUsedMenus())
                .anyMatch(menu -> menu.isSameMenu(menuForReservation));
    }

    @Test
    public void 새로운_Reservation이_등록될_때_기존에_마지막_사용되었던_Menu의_상태를_업데이트() {
        Menu lastUsedMenu = lastUsedMenu();
        Menu menuForReservation = notLastUsedMenu();
        store.addMenu(lastUsedMenu);
        store.addMenu(menuForReservation);
        assertThat(store.getLastUsedMenus())
                .anyMatch(menu -> menu.isSameMenu(lastUsedMenu));
        store.addReservation()
                .with(menuForReservation, defaultMaxCount());
        assertThat(store.getLastUsedMenus())
                .noneMatch(menu -> menu.isSameMenu(lastUsedMenu));
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
    public void addOrder() {
        Customer customer;
        LocalDateTime pickupTime;

        Order order = store.addOrder(customer, pickupTime)
                .with(reservationId, itemCount)
                .with(reservationId, itemCount)
                .with(reservationId, itemCount)
                .purchase();

        assertThat(store.findOrdersOnActiveReservations()).contains(order);
    }

    @Test
    public void addOrder_새로운_Order는_열린_상태의_Store에_대해서만_생성될_수_있다() {

    }

    @Test
    public void addOrder_새로운_OrderItem은_Order가_생성될_때_생성할_수_있다() {

    }

    @Test
    public void 활성화된_Reservation에_대해서만_OrderItem이_생성될_수_있다() {

    }

    @Test
    public void 새로운_OrderItem은_Reservation의_개인별_최대_주문_갯수를_초과할_수_없다() {

    }

    @Test
    public void 새로운_OrderItem은_Reservation의_현재_주문_가능한_갯수를_초과할_수_없다() {

    }

    @Test
    public void 새로_생성한_OrderItem의_갯수만큼_Reservation의_현재_주문_가능한_갯수를_줄인다() {

    }
}
