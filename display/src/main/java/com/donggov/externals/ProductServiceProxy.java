package com.donggov.externals;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product", fallbackFactory = ProductServiceProxyFallbackFactory.class)
public interface ProductServiceProxy {

    @GetMapping("/products/{id}")
    public String getProduct(@PathVariable long id);

}
