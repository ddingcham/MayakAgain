package com.mayak.ddingcham.service.support;

import com.mayak.ddingcham.domain.Reservation;
import com.mayak.ddingcham.domain.Store;

import java.util.List;

public interface ReservationSelector {
    List<Reservation> select(Store store);
}