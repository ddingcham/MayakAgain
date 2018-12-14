package com.mayak.ddingcham.domain;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface StoreRepository extends CrudRepository<Store, Long>{
    Optional<Store> findByUserId(long userId);
    Optional<Store> findByUser(User user);
    @Query("select distinct store from Store store join fetch store.reservations reservation where reservation.activated = 'true'")
    Store findByIdWithActiveReservation(long id);
}
