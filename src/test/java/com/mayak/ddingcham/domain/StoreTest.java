package com.mayak.ddingcham.domain;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static com.mayak.ddingcham.domain.FixtureUtils.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Java6Assertions.assertThat;

@Slf4j
public class StoreTest {

    private Store store;

    @Before
    public void setUp() {
        store = Store.builder().build();
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
        store.addReservation(LocalDateTime.MAX, LocalDate.now())
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
        store.addReservation(LocalDateTime.MAX, LocalDate.now())
                .with(menuForReservation, defaultMaxCount());
    }

    @Test(expected = NoSuchElementException.class)
    public void addReservation_삭제된_Menu에_대해서_생성할_경우() {
        Menu menuForReservation = deletedMenu();
        store.addMenu(menuForReservation);
        store.addReservation(LocalDateTime.MAX, LocalDate.now())
                .with(menuForReservation, defaultMaxCount());
    }

    @Test(expected = NoSuchElementException.class)
    public void addReservation_없는_Menu에_대해서_생성할_경우() {
        Menu menuForReservation = unDeletedMenu();
        store.addReservation(LocalDateTime.MAX, LocalDate.now())
                .with(menuForReservation, defaultMaxCount());
    }

    @Test
    @SuppressWarnings("deprecation")
    public void addReservation_Store가_닫힌_상태가_될_때_활성화_상태의_Reservation들은_비활성화_상태로() {
        Menu menuForReservation = unDeletedMenu();
        store.addMenu(menuForReservation);
        store.addReservation(LocalDateTime.MAX, LocalDate.now())
                .with(menuForReservation, defaultMaxCount());
        List<Reservation> reservations = store.getActiveReservations();
        log.debug("before close addedReservations : {}", reservations);
        store.close();
        log.debug("after close addedReservations : {}", reservations);
        assertThat(reservations)
                .noneMatch(Reservation::isActivated);
    }

    @Test
    public void addReservation_새로운_Reservation이_등록될_때_해당하는_Menu들의_마지막_사용_여부_업데이트() {
        Menu menuForReservation = notLastUsedMenu();
        store.addMenu(menuForReservation);
        store.addReservation(LocalDateTime.MAX, LocalDate.now())
                .with(menuForReservation, defaultMaxCount());
        assertThat(store.getLastUsedMenus())
                .anyMatch(menu -> menu.isSameMenu(menuForReservation));
    }

    @Test
    public void addReservation_새로운_Reservation이_등록될_때_기존에_마지막_사용되었던_Menu의_상태를_업데이트() {
        Menu lastUsedMenu = lastUsedMenu();
        Menu menuForReservation = notLastUsedMenu();
        store.addMenu(lastUsedMenu);
        store.addMenu(menuForReservation);
        assertThat(store.getLastUsedMenus())
                .anyMatch(menu -> menu.isSameMenu(lastUsedMenu));
        store.addReservation(LocalDateTime.MAX, LocalDate.now())
                .with(menuForReservation, defaultMaxCount());
        assertThat(store.getLastUsedMenus())
                .noneMatch(menu -> menu.isSameMenu(lastUsedMenu));
    }

    @Test
    public void addOrder() {
        addReservation();
        List<Reservation> reservations = store.getActiveReservations();
        Customer customer = new Customer();
        LocalDateTime pickupTime = LocalDateTime.now();

        Order order = store.addOrder(customer, pickupTime)
                .with(reservations.get(0).getId(), 1)
                .purchase();

        log.debug("newOrder : {}", order);

        assertThat(store.findOrdersByPickupDate(pickupTime.toLocalDate())).contains(order);
    }

    @Test(expected = IllegalStateException.class)
    public void addOrder_새로운_Order는_열린_상태의_Store에_대해서만_생성될_수_있다() {
        addReservation();
        store.close();
        store.setTimeToClose(LocalDateTime.MIN);
        store.addOrder(new Customer(), LocalDateTime.now());
    }

    @Test(expected = IllegalStateException.class)
    public void addOrder_활성화된_Reservation에_대해서만_OrderItem이_생성될_수_있다() {
        addReservation();
        List<Reservation> reservations = store.getActiveReservations();
        reservations
                .forEach(reservation -> reservation.setActivated(Reservation.RESERVATION_DEACTIVATED));
        store.addOrder(new Customer(), LocalDateTime.now())
                .with(reservations.get(0).getId(), 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addOrder_새로운_OrderItem은_1개_이상이다() {
        addReservation();
        List<Reservation> reservations = store.getActiveReservations();
        store.addOrder(new Customer(), LocalDateTime.now())
                .with(reservations.get(0).getId(), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addOrder_새로운_OrderItem은_Reservation의_개인별_최대_주문_갯수를_초과할_수_없다() {
        addReservation();
        List<Reservation> reservations = store.getActiveReservations();
        store.addOrder(new Customer(), LocalDateTime.now())
                .with(reservations.get(0).getId(), defaultMaxCount().getPersonalMaxCount() + 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addOrder_새로운_OrderItem은_Reservation의_현재_주문_가능한_갯수를_초과할_수_없다() {
        addReservation();
        List<Reservation> reservations = store.getActiveReservations();
        store.addOrder(new Customer(), LocalDateTime.now())
                .with(reservations.get(0).getId(), defaultMaxCount().getMaxCount() + 1);
    }

    @Test
    public void addOrder_새로_생성한_OrderItem의_갯수만큼_Reservation의_현재_주문_가능한_갯수를_줄인다() {
        addReservation();
        List<Reservation> reservations = store.getActiveReservations();
        store.addOrder(new Customer(), LocalDateTime.now())
                .with(reservations.get(0).getId(), defaultMaxCount().getPersonalMaxCount());
        assertThatThrownBy(() -> store.addOrder(new Customer(), LocalDateTime.now())
                .with(reservations.get(0).getId(), 1))
                .isInstanceOf(IllegalArgumentException.class);
    }

}
