package com.mayak.ddingcham.dto;

import com.mayak.ddingcham.domain.MaxCount;
import com.mayak.ddingcham.domain.Reservation;
import com.mayak.ddingcham.domain.Store;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @ToString
public class ReservationDTO {
    private long menuId;
    private int maxCount;
    private int personalMaxCount;
    @Builder
    public ReservationDTO(long menuId, int maxCount, int personalMaxCount) {
        this.menuId = menuId;
        this.maxCount = maxCount;
        this.personalMaxCount = personalMaxCount;
    }

    public Reservation toDomain(Store store) {
        MaxCount maxCount = new MaxCount(this.maxCount, personalMaxCount);
        return Reservation.builder()
                .maxCount(maxCount)
                .store(store)
                // todo refactoring & orElse //store.getMenuById(menuId))
                .menu(store.getMenus().stream().filter(x-> x.getId() == menuId).findFirst().get())
                .openDate(LocalDate.now())
                .build();
    }
}
