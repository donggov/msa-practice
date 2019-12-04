package com.donggov.externals;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ProductServiceProxyFallbackFactory implements FallbackFactory<ProductServiceProxy> {

    @Override
    public ProductServiceProxy create(Throwable cause) {
        log.error("Error", cause);
        return id -> "[This Product is sold out]";
    }

}
