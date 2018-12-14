package com.mayak.ddingcham.domain;

import com.mayak.ddingcham.domain.support.MaxCount;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Builder
@EqualsAndHashCode(of = "id")
@ToString
@Slf4j
public class Store {

    private static final boolean OPEN = true;
    private static final boolean CLOSE = false;
    private static final String DUPLICATE_MENU_MESSAGE = "똑같은 메뉴 정보가 이미 존재";
    private static final String NULL_MENU_MESSAGE = "메뉴정보는 NULL이면 안됨";
    private static final String INVALID_STATE_TO_ADD_RESERVATION = "가게가 닫힌 상태일 때만 예약 추가가 가능합니다.";
    private static final String DEFAULT_FOREIGN_KEY = "store_id";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 40)
    private String storeName;

    @Column(nullable = false, length = 60)
    private String serviceDescription;

    @Column(nullable = false, length = 10)
    private String ownerName;

    @Column(nullable = false, length = 400)
    private String imgURL;

    @Column(nullable = false, length = 5)
    private String postCode;

    @Column(nullable = false, length = 100)
    private String address;

    @Column(length = 40)
    private String addressDetail;

    @Column(nullable = false, length = 11)
    private String phoneNumber;

    @Column(length = 600)
    private String description;

    @OneToOne
    private User user;

    private LocalDateTime timeToClose;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = DEFAULT_FOREIGN_KEY)
    @Builder.Default
    private Set<Menu> menus = new LinkedHashSet<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = DEFAULT_FOREIGN_KEY)
    @Builder.Default
    private Set<Reservation> reservations = new LinkedHashSet<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = DEFAULT_FOREIGN_KEY)
    @Builder.Default
    private Set<Order> orders = new LinkedHashSet<>();

    public boolean isOpen() {
        return updateOpenStatus();
    }

    public boolean updateOpenStatus() {
        if (timeToClose == null || timeToClose.isBefore(LocalDateTime.now())) {
            close();
            return CLOSE;
        }
        return OPEN;
    }

    public void activate(LocalDateTime timeToClose) {
        menus.stream().forEach(Menu::dropLastUsedStatus);
        this.timeToClose = timeToClose;
    }

    public boolean hasSameOwner(User other) {
        return this.user.equals(other);
    }

    public Store updateStore(Store store) {
        this.serviceDescription = store.serviceDescription;
        this.postCode = store.postCode;
        this.address = store.address;
        this.addressDetail = store.addressDetail;
        this.phoneNumber = store.phoneNumber;
        this.description = store.description;
        this.imgURL = store.imgURL;
        return this;
    }

    public Menu addMenu(Menu menu) {
        if (menu == null) {
            throw new IllegalArgumentException(NULL_MENU_MESSAGE);
        }
        if (hasMenuNotDeleted(menu)) {
            throw new IllegalArgumentException(DUPLICATE_MENU_MESSAGE);
        }
        menus.add(menu);
        return menu;
    }

    public void removeMenu(Menu removedMenu) {
        searchMenuNotDeleted(removedMenu)
                .orElseThrow(NoSuchElementException::new)
                .deleteMenu();
    }

    public boolean hasMenuNotDeleted(Menu menu) {
        return searchMenuNotDeleted(menu)
                .isPresent();
    }

    private Optional<Menu> searchMenuNotDeleted(Menu removedMenu) {
        return menus.stream()
                .filter(storedMenu -> storedMenu.isSameMenu(removedMenu) && !storedMenu.isDeleted())
                .findAny();
    }

    public ReservationRegister addReservation(LocalDateTime timeToClose) {
        if (isOpen() == OPEN) {
            throw new IllegalStateException(INVALID_STATE_TO_ADD_RESERVATION);
        }
        menus.stream()
                .filter(Menu::isLastUsed)
                .forEach(Menu::dropLastUsedStatus);
        setTimeToClose(timeToClose);
        return new ReservationRegister();
    }

    public List<Reservation> getActiveReservations() {
        return reservations.stream()
                .filter(Reservation::isActivated)
                .collect(Collectors.toList());
    }

    /**
     * @deprecated (테스트 상황을 조성하기 위해 만듬 - > test double로 대체 예정)
     */
    @Deprecated
    void close() {
        timeToClose = LocalDateTime.now();
        getActiveReservations()
                .forEach(reservation -> reservation.setActivated(Reservation.RESERVATION_DEACTIVATED));
    }

    public List<Menu> getLastUsedMenus() {
        return menus.stream()
                .filter(Menu::isLastUsed)
                .collect(Collectors.toList());
    }

    public OrderRegister addOrder(Customer customer, LocalDateTime pickupTime) {
        if(isOpen() == CLOSE){
            throw new IllegalStateException();
        }
        return new OrderRegister(customer, pickupTime);
    }

    public List<Order> findOrdersByPickupDate(LocalDate pickupDate) {
        return orders.stream()
                .filter(order -> order.matchedPickupDate(pickupDate))
                .collect(Collectors.toList());
    }

    public class OrderRegister {
        private Order order;

        private OrderRegister(Customer customer, LocalDateTime pickupTime) {
            order = Order.builder()
                    .customer(customer)
                    .pickupTime(pickupTime)
                    .build();
        }

        public OrderRegister with(long reservationId, int itemCount) {
            if(itemCount < 1) throw new IllegalArgumentException();
            Reservation reservation = findReservationById(reservationId);
            if(reservation.isActivated()) {
                order.addOrderItem(OrderItem.builder()
                        .reservation(reservation.checkPossiblePurchase(itemCount))
                        .itemCount(itemCount)
                        .build());
                return this;
            }
            throw new IllegalStateException();
        }

        private Reservation findReservationById(long reservationId) {
            return reservations.stream()
                    .filter(reservation -> reservation.isSameId(reservationId))
                    .findAny()
                    .orElseThrow(NoSuchElementException::new);
        }

        public Order purchase() {
            orders.add(order);
            return order;
        }
    }

    public class ReservationRegister {
        private ReservationRegister() {
        }

        public ReservationRegister with(Menu menuForReservation, MaxCount maxCount) {
            reservations.add(Reservation.builder()
                    .openDate(LocalDate.now())
                    .activated(Reservation.RESERVATION_ACTIVATED)
                    .maxCount(maxCount)
                    .menu(searchMenuNotDeleted(menuForReservation)
                            .orElseThrow(NoSuchElementException::new)
                            .setUpLastUsedStatus(maxCount))
                    .build());
            return this;
        }
    }
}
