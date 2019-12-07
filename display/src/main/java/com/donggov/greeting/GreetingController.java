package com.donggov.greeting;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@RefreshScope
public class GreetingController {

    @Value("${application.env}")
    private String env;

    @Value("${greeting}")
    private String greeting;

    @GetMapping("")
    public String getGreeting() {
        return env + ", " + greeting;
    }
}
