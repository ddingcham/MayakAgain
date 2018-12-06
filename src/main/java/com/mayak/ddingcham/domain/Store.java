package com.mayak.ddingcham.domain;

import com.mayak.ddingcham.domain.support.MaxCount;
import com.mayak.ddingcham.dto.MenuOutputDTO;
import com.mayak.ddingcham.exception.InvalidStateOnStore;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Where;

import javax.persistence.*;
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

    @OneToMany(mappedBy = "store", cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.DETACH})
    @Where(clause = "deleted = false")
    @Builder.Default
    private List<Menu> menus = new ArrayList<>();

    public boolean isOpen() {
        return updateOpenStatus();
    }

    @PostPersist
    @PostUpdate
    @PostLoad
    @SuppressWarnings("deprecation")
    public boolean updateOpenStatus() {
        if (timeToClose == null || timeToClose.isBefore(LocalDateTime.now())) {
            close();
            return CLOSE;
        }
        return OPEN;
    }

    public void deactivate() {
        timeToClose = null;
    }

    public void activate(LocalDateTime timeToClose) {
        menus.stream().forEach(Menu::dropLastUsedStatus);
        this.timeToClose = timeToClose;
    }

    public List<MenuOutputDTO> getMenuOutputDTOList() {
        List<MenuOutputDTO> menuDTOs = new ArrayList<>();
        this.menus.stream().forEach(e -> menuDTOs.add(MenuOutputDTO.createUsedMenuOutputDTO(e)));
        return menuDTOs;
    }

    public List<MenuOutputDTO> getUsedMenuOutputDTOList() {
        List<MenuOutputDTO> menuDTOs = new ArrayList<>();
        this.menus.stream().filter(Menu::isLastUsed).forEach(e -> menuDTOs.add(MenuOutputDTO.createUsedMenuOutputDTO(e)));
        return menuDTOs;
    }

    public void updateLastUsedMenu(Menu menu, MaxCount maxCount) {
        if (isOpen() == CLOSE)
            throw new InvalidStateOnStore("Cannot update menu status on closed store");
        this.menus.stream().filter(x -> x.equals(menu)).findAny().orElseThrow(() -> new InvalidStateOnStore("Cannot find menu on store"))
                .setUpLastUsedStatus(maxCount);
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

    public void addMenu(Menu menu) {
        if (menu == null) {
            throw new IllegalArgumentException(NULL_MENU_MESSAGE);
        }
        if (hasMenuNotDeleted(menu)) {
            throw new IllegalArgumentException(DUPLICATE_MENU_MESSAGE);
        }

        menus.add(menu);
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

    public ReservationRegister addReservation() {
        if (isOpen() == OPEN) {
            throw new IllegalStateException(INVALID_STATE_TO_ADD_RESERVATION);
        }
        menus.stream()
                .filter(Menu::isLastUsed)
                .forEach(Menu::dropLastUsedStatus);
        return new ReservationRegister();
    }

    public List<Reservation> getActiveReservations() {
        return menus.stream()
                .map(Menu::getActiveReservation)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
      * @deprecated (테스트 상황을 조성하기 위해 만듬 -> test double로 대체 예정)
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

    class ReservationRegister {
        private ReservationRegister() {
        }

        public ReservationRegister with(Menu menuForReservation, MaxCount maxCount) {
            searchMenuNotDeleted(menuForReservation)
                    .orElseThrow(NoSuchElementException::new)
                    .addReservation(maxCount);
            return this;
        }
    }
}
