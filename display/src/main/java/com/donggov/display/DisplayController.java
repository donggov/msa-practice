package com.donggov.display;

import com.donggov.externals.ProductServiceProxy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/display")
public class DisplayController {

    private final DisplayService displayService;

    private final ProductServiceProxy productServiceProxy;

    public DisplayController(DisplayService displayService, ProductServiceProxy productServiceProxy) {
        this.displayService = displayService;
        this.productServiceProxy = productServiceProxy;
    }


    @GetMapping("/{id}")
    public String getDisplay(@PathVariable long id) {
//        return displayService.getProduct(id);
        return productServiceProxy.getProduct(id);
    }

}
