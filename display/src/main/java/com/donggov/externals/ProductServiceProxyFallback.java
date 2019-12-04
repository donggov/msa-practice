package com.donggov.externals;

import org.springframework.stereotype.Component;

@Component
public class ProductServiceProxyFallback implements ProductServiceProxy {

    @Override
    public String getProduct(long id) {
        return "[This Product is sold out]";
    }

}
