package com.donggov.display;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DisplayService {

    private final RestTemplate productRestTemplate;

    public DisplayService(RestTemplate productRestTemplate) {
        this.productRestTemplate = productRestTemplate;
    }

    public String getProduct(long id) {
        return productRestTemplate.getForObject("/products/" + id, String.class);
    }
    
}
