package com.donggov.greeting;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@RefreshScope
@Slf4j
public class GreetingController {

    @Value("${application.env}")
    private String env;
//
    @Value("${greeting}")
    private String greeting;

    @GetMapping("")
    public String getGreeting() {
        log.debug(">>>>> Application env : {}", env);
        return greeting;
    }
}
