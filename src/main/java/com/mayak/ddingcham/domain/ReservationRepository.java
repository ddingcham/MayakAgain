package com.mayak.ddingcham.domain;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends CrudRepository<Reservation, Long> {
    List<Reservation> findAll();

    @Query(value = "select * from reservation where open_date = :open_date and store_id = :store_id", nativeQuery = true)
    List<Reservation> findAllByStoreIdAndOpenDate(@Param("store_id") long storeId, @Param("open_date") LocalDate openDate);

    @Query(value = "select * from reservation where open_date < :open_date and store_id = :store_id order by open_date desc limit 1", nativeQuery = true)
    Optional<Reservation> findFirstByStoreIdAndOpenDateBeforeOrderByOpenDateDesc(@Param("store_id") long storeId, @Param("open_date") LocalDate openDate);
}
