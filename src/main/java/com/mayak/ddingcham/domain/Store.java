package com.mayak.ddingcham.domain;

import com.mayak.ddingcham.domain.support.MaxCount;
import com.mayak.ddingcham.dto.MenuOutputDTO;
import com.mayak.ddingcham.exception.InvalidStateOnStore;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //todo length 등 다른 조건들
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

    // Todo 제약사항 추가
    private LocalDateTime timeToClose;

    // Todo Cascade issue 다른 옵션도 적용해야 할 수도 있음
    @OneToMany(mappedBy = "store", cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.DETACH})
    @Where(clause = "deleted = false")
    @Builder.Default
    private List<Menu> menus = new ArrayList<>();

    // todo (현재 시각이랑 timeToClose 랑 비교) +(currentReservations 갯수?)해서 오픈상태 동기화 어떻게 해줄지
    @Transient
    @Builder.Default
    private boolean isOpen = false;

    public void addMenu(Menu menu) {
        if (menu == null) {
            throw new IllegalArgumentException(NULL_MENU_MESSAGE);
        }
        if (hasMenu(menu)) {
            throw new IllegalArgumentException(DUPLICATE_MENU_MESSAGE);
        }

        menus.add(menu);
    }

    public boolean hasMenu(Menu menu) {
        return menus.stream()
                .anyMatch(storedMenu -> storedMenu.isSameMenu(menu));
    }

    public boolean isOpen() {
        updateOpenStatus();
        return isOpen;
    }

    @PostPersist
    @PostUpdate
    @PostLoad
    public void updateOpenStatus() {
        if (timeToClose == null || timeToClose.isBefore(LocalDateTime.now())) {
            isOpen = CLOSE;
            return;
        }
        isOpen = OPEN;
    }

    public void deactivate() {
        timeToClose = null;
        isOpen = CLOSE;
    }

    public void activate(LocalDateTime timeToClose) {
        menus.stream().forEach(menu -> menu.dropLastUsedStatus());
        this.timeToClose = timeToClose;
        isOpen = OPEN;
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
        if (!this.isOpen)
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

    public void removeMenu(Menu removedMenu) {
        menus.remove(menus.stream()
                .filter(storedMenu -> storedMenu.isSameMenu(removedMenu))
                .findFirst()
                .orElseThrow(NoSuchElementException::new));
    }
}
