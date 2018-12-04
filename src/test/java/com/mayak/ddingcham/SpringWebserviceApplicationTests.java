package com.mayak.ddingcham;

import com.mayak.ddingcham.service.support.ReservationSelector;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Slf4j
public class SpringWebserviceApplicationTests {

    @Autowired
    BeanFactory beanFactory;

    @Autowired
    TestRestTemplate template;

    @Test
    public void contextLoads() {
    }

    @Test
    public void componentLoadTest(){
        ReservationSelector selector = (ReservationSelector)beanFactory.getBean("currentReservation");
        log.debug("loaded component : {}", selector.getClass().getName());
    }

    @Test
    public void check_profile_api(){
        String profile = template.getForObject("/profile", String.class);
        log.debug("current profile : {}", profile);

        assertThat(profile).isNotNull();
    }
}
