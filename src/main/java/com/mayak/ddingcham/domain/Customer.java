package com.mayak.ddingcham.domain;


import com.mayak.ddingcham.converter.NumberConverter;
import com.fasterxml.jackson.annotation.JsonGetter;
import lombok.*;

import javax.persistence.Embeddable;

@Embeddable
@Data
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Builder
@ToString
public class Customer {

    private String name;
    private String phoneNumber;

    @JsonGetter("phoneNumber")
    public String getFormattedPhoneNumber(){
        return NumberConverter.formatPhoneNumber(phoneNumber);
    }
}
