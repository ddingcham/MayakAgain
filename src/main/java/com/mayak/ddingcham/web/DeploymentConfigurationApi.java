package com.mayak.ddingcham.web;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.annotation.HttpConstraint;
import java.util.Arrays;

@RestController
@AllArgsConstructor
@Slf4j
public class DeploymentConfigurationApi {

    private Environment environment;

    @GetMapping(value = "/profile")
    public String getProfile(){
        log.debug("profiles : {}", environment.getActiveProfiles());
        return Arrays.stream(environment.getActiveProfiles())
                .findFirst()
                .orElse("default profile");
    }
}
