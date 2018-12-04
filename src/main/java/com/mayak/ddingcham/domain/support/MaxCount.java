package com.mayak.ddingcham.domain.support;

import lombok.*;

import javax.persistence.Embeddable;

@Embeddable
@Data
@ToString
public class MaxCount {

    private Integer maxCount;

    private Integer personalMaxCount;

    @Builder
    public MaxCount(int maxCount, int personalMaxCount) {
        if (personalMaxCount < 1 || maxCount < personalMaxCount) {
            throw new IllegalArgumentException("illegal maxCount & personalMaxCount");
        }
        this.maxCount = maxCount;
        this.personalMaxCount = personalMaxCount;
    }
}
