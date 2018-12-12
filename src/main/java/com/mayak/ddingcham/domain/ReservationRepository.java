package com.mayak.ddingcham.domain;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends CrudRepository<Reservation, Long> {
    List<Reservation> findAll();
//    List<Reservation> findAllByStoreId(long storeId);
//    List<Reservation> findAllByStoreIdAndOpenDate(long storeId, LocalDate openDate);
//    Optional<Reservation> findFirstByStoreIdAndOpenDateBeforeOrderByOpenDateDesc(long storeId, LocalDate now);
}
