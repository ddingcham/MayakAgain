package com.mayak.ddingcham.service;

import com.mayak.ddingcham.domain.Reservation;
import com.mayak.ddingcham.domain.ReservationRepository;
import com.mayak.ddingcham.domain.Store;
import com.mayak.ddingcham.domain.StoreRepository;
import com.mayak.ddingcham.dto.ReservationFormDTO;
import com.mayak.ddingcham.service.support.ReservationSelector;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private BeanFactory beanFactory;

    private static final String SELECTOR_POST_FIX = "Reservation";

    @Transactional
    public Iterable<Reservation> createReservation(ReservationFormDTO reservationFormDTO, Store store){
        //if(store.isOpen()) throw new RuntimeException("이미 진행 중인 예약 정보가 존재 합니다.");
        LocalDateTime timeToClose = reservationFormDTO.generateTimeToClose();
        List<Reservation> reservations = reservationFormDTO.generateReservations(store);

        store.activate(timeToClose);
//        reservations.forEach(reservation -> reservation.regist());
        return reservationRepository.saveAll(reservations);
    }

    public Map<Long, Reservation> getTodayReservations(long storeId) {
        //todo : store의 오픈, 마감시간과 Reservation의 날짜 확인 필요
        return convertReservationListToMap(reservationRepository.findAllByStoreId(storeId));
    }

    private Map<Long, Reservation> convertReservationListToMap(List<Reservation> reservations) {
        return reservations.stream().collect(Collectors.toMap(Reservation::getId, reservation -> reservation));
    }

    public List<Reservation> getReservationsByCondition(String condition, Store store) {
        //todo : BeanException 처리
        ReservationSelector selector = (ReservationSelector)beanFactory.getBean(condition + SELECTOR_POST_FIX);
        return selector.select(store);
    }

    public LocalDate getLastDay(Store store) {
        return reservationRepository
                .findFirstByStoreAndOpenDateBeforeOrderByOpenDateDesc(store, store.getTimeToClose().toLocalDate().minusDays(1))
                .orElseThrow(() -> new EntityNotFoundException("Last Day가 존재하지 않는다."))
                .getOpenDate();
    }
}
