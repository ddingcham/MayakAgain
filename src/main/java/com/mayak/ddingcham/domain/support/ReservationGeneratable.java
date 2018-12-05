package com.mayak.ddingcham.domain.support;

import com.mayak.ddingcham.domain.Menu;

public interface ReservationGeneratable {
    ReservationGeneratable with(Menu menuForReservation, MaxCount maxCount);
}
