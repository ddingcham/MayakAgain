package com.mayak.ddingcham.web;

import com.mayak.ddingcham.RestResponse;
import com.mayak.ddingcham.domain.Reservation;
import com.mayak.ddingcham.domain.Store;
import com.mayak.ddingcham.dto.ReservationFormDTO;
import com.mayak.ddingcham.security.AuthorizedStore;
import com.mayak.ddingcham.service.StoreService;
import lombok.extern.slf4j.Slf4j;
import com.mayak.ddingcham.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Slf4j
public class ApiReservationController {

    @Autowired
    private StoreService storeService;

    @Autowired
    private ReservationService reservationService;


    @PostMapping("/stores/{storeId}/reservations")
    public RestResponse<RestResponse.RedirectData> create(@AuthorizedStore(notOpen = true) Store store, @RequestBody ReservationFormDTO reservationDTO) {
        reservationService.createReservation(reservationDTO, store);
        //return "/result/successㄱ"
        return RestResponse.ofRedirectResponse("/owner/reservations/?condition=current", "OK");
    }

    @GetMapping(value = "/stores/{storeId}/reservations", params = "conditions")
    public List<Reservation> list(@RequestParam String conditions, @PathVariable long storeId) {
        return  reservationService.getReservationsByCondition(conditions, storeService.getStoreById(storeId));
    }

}
