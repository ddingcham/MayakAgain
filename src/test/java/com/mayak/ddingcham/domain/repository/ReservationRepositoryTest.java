package com.mayak.ddingcham.domain.repository;

import com.mayak.ddingcham.domain.*;
import com.mayak.ddingcham.domain.support.MaxCount;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@Slf4j
public class ReservationRepositoryTest {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private StoreRepository storeRepository;

    private Store defaultStore;
    private Menu defaultMenu;
    private List<Reservation> targetReservations;
    private static final LocalDate PAST_DATE = LocalDate.ofEpochDay(0L);


    @Before
    public void setUp() {
        prepareDefaultStore();
        prepareDefaultMenus();
        preparePastReservationsByStore();
    }

    @Test
    public void setUpFixture() {
    }

    @Test
    public void list_current_reservations_있을때() {
        Reservation reservation = setUpActiveReservation();
        List<Reservation> actualReservations = reservationRepository.findAllByStoreIdAndOpenDate(defaultStore.getId(), LocalDate.now());
        assertThat(actualReservations)
                .isNotEmpty()
                .contains(reservation)
                .allMatch(Reservation::isActivated);
    }

    @Test
    public void list_current_reservations_없을때() {
        List<Reservation> actualReservations = reservationRepository.findAllByStoreIdAndOpenDate(defaultStore.getId(), LocalDate.now());
        assertThat(actualReservations).isEmpty();
    }

    private Reservation setUpActiveReservation() {
        defaultStore.addReservation(LocalDateTime.now().plusDays(1L), LocalDate.now())
                .with(defaultMenu, defaultMaxCount());
        storeRepository.save(defaultStore);
        return defaultStore.getActiveReservations().get(0);
    }


    @Test
    public void list_last_reservations_하루전() {
        long termOfPastDays = 1L;
        setUpLastUsedReservation(termOfPastDays);

        LocalDate lastDate = reservationRepository
                .findFirstByStoreIdAndOpenDateBeforeOrderByOpenDateDesc(defaultStore.getId(), LocalDate.now())
                .get()
                .getOpenDate();

        log.debug("lastDate : {}", lastDate);

        List<Reservation> actualReservations = reservationRepository.findAllByStoreIdAndOpenDate(defaultStore.getId(), lastDate);

        assertThat(actualReservations).isNotEmpty();
    }

    @Test
    public void list_last_reservations_여러날전() {
        long termOfPastDays = 10L;
        setUpLastUsedReservation(termOfPastDays);

        LocalDate lastDate = reservationRepository
                .findFirstByStoreIdAndOpenDateBeforeOrderByOpenDateDesc(defaultStore.getId(), LocalDate.now())
                .get()
                .getOpenDate();

        log.debug("lastDate : {}", lastDate);

        List<Reservation> actualReservations = reservationRepository.findAllByStoreIdAndOpenDate(defaultStore.getId(), lastDate);

        assertThat(actualReservations).isNotEmpty();
    }

    private Reservation setUpLastUsedReservation(long termOfPastDays) {
        LocalDateTime baseTime = LocalDateTime.now().minusDays(termOfPastDays);
        defaultStore.addReservation(baseTime, baseTime.toLocalDate().minusDays(1L))
                .with(defaultMenu, defaultMaxCount());
        storeRepository.save(defaultStore);
        Reservation lastUsedReservation = defaultStore.getActiveReservations().get(0);
        setUpActiveReservation();
        return lastUsedReservation;
    }

    private void prepareDefaultStore() {
        defaultStore = Store.builder()
                .description("DESC")
                .imgURL("img")
                .ownerName("OWNER")
                .phoneNumber("1234512345")
                .postCode("12345")
                .serviceDescription("reservation 조회 테스트용")
                .storeName("defaultStore")
                .address("ADDRESS")
                .build();
        storeRepository.save(defaultStore);
    }

    private void prepareDefaultMenus() {
        defaultMenu = FixtureUtils.unDeletedMenu();
        defaultStore.addMenu(defaultMenu);
        defaultStore = storeRepository.save(defaultStore);
    }

    private void preparePastReservationsByStore() {
        LocalDateTime baseTime;
        for(long year=1L;year<5L;year++){
            baseTime = LocalDateTime.MIN.plusYears(year);
            defaultStore.addReservation(baseTime.plusDays(1L), baseTime.toLocalDate())
                    .with(defaultMenu, defaultMaxCount());
            storeRepository.save(defaultStore);
        }
    }

    private Reservation generateTestReservation(LocalDate openDate) {
        return Reservation.builder()
                .maxCount(defaultMaxCount())
                .menu(defaultMenu)
                .openDate(openDate)
                .build();
    }

    private MaxCount defaultMaxCount() {
        return new MaxCount(2, 1);
    }
}
