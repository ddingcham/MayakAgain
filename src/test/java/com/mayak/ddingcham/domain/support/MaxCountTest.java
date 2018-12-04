package com.mayak.ddingcham.domain.support;

import org.assertj.core.api.SoftAssertions;
import org.junit.Before;
import org.junit.Test;

public class MaxCountTest {

    SoftAssertions softly;

    @Before
    public void setUp() {
        softly = new SoftAssertions();
    }

    @Test
    public void construct_MaxCount() {
        int maxCount = 10;

        softly.assertThatThrownBy(
                () -> MaxCount.builder()
                        .maxCount(maxCount)
                        .personalMaxCount(maxCount + 1)
                        .build()
        )
                .as("maxCount < personalMAxCount")
                .isInstanceOf(IllegalArgumentException.class);

        softly.assertThatThrownBy(
                () -> MaxCount.builder()
                        .maxCount(maxCount)
                        .personalMaxCount(0).build()
        )
                .as("personalCount < 1")
                .isInstanceOf(IllegalArgumentException.class);

        softly.assertAll();
    }
}
