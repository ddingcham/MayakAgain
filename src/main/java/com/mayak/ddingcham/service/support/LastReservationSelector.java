package com.mayak.ddingcham.service.support;

import com.mayak.ddingcham.domain.Reservation;
import com.mayak.ddingcham.domain.ReservationRepository;
import com.mayak.ddingcham.domain.Store;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component(value = "lastReservation")
public class LastReservationSelector implements ReservationSelector{

    @Autowired
    ReservationRepository reservationRepository;

    @Override
    public List<Reservation> select(Store store) {
        //todo Exception 날리기 + Refactoring
//        LocalDate lastDate = reservationRepository
//                .findFirstByStoreIdAndOpenDateBeforeOrderByOpenDateDesc(store.getId(), store.getTimeToClose().toLocalDate().minusDays(1))
//                .orElseThrow((() -> new EntityNotFoundException("직전 예약이 없어요.")))
//                .getOpenDate();
//
//        return reservationRepository.findAllByStoreIdAndOpenDate(store.getId(), lastDate);
        return reservationRepository.findAll();
    }

}