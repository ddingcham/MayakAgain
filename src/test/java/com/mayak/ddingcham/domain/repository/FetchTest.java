package com.mayak.ddingcham.domain.repository;

import com.mayak.ddingcham.domain.Menu;
import com.mayak.ddingcham.domain.Store;
import com.mayak.ddingcham.domain.StoreRepository;
import com.mayak.ddingcham.domain.support.MaxCount;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;

import static com.mayak.ddingcham.domain.FixtureUtils.unDeletedMenu;

@RunWith(SpringRunner.class)
@DataJpaTest
@Slf4j
public class FetchTest {

    @Autowired
    StoreRepository storeRepository;
    private Store store;

    @Before
    public void setUp() {
        store = Store.builder()
                .address("address")
                .ownerName("hi")
                .imgURL("img")
                .serviceDescription("")
                .phoneNumber("")
                .postCode("")
                .storeName("")
                .build();
        Menu menu = store.addMenu(unDeletedMenu());
        store.addReservation(LocalDateTime.MAX)
                .with(menu, new MaxCount(10,3));
        store.isOpen();
        storeRepository.save(store);
    }

    @Test
    public void 조건_fetch_테스트() {
        log.debug("store(:OrderAddableStore) with active Reservation : {}", storeRepository.findByIdWithActiveReservation(store.getId()));
    }
}
