package com.donggov.display;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class DisplayService {

    private final RestTemplate productRestTemplate;

    public DisplayService(RestTemplate productRestTemplate) {
        this.productRestTemplate = productRestTemplate;
    }

    @HystrixCommand(fallbackMethod = "getProductFallback")
    public String getProduct(long id) {
        return productRestTemplate.getForObject("/products/" + id, String.class);
    }

    public String getProductFallback(long id, Throwable t) {
        log.error("getProductFallback", t);
        return "[This Product is sold out]";
    }
    
}
