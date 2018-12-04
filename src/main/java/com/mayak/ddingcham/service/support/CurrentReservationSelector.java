package com.mayak.ddingcham.service.support;

import com.mayak.ddingcham.domain.Reservation;
import com.mayak.ddingcham.domain.ReservationRepository;
import com.mayak.ddingcham.domain.Store;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;

@Component(value = "currentReservation")
public class CurrentReservationSelector implements ReservationSelector{

    @Autowired
    ReservationRepository reservationRepository;

    @Override
    public List<Reservation> select(Store store) {
        //todo Exception 날리기
        List<Reservation> reservations = reservationRepository.findAllByStoreAndOpenDate(store, LocalDate.now());
        isExisted(reservations);
        return reservations;
    }

    private boolean isExisted(List<Reservation> reservations){
        if(reservations == null || reservations.isEmpty())
            throw new EntityNotFoundException("현재 진행 중인 예약이 없습니다.");
        return true;
    }
}