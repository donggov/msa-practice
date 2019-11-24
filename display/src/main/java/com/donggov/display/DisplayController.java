package com.donggov.display;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/display")
public class DisplayController {

    private final DisplayService displayService;

    public DisplayController(DisplayService displayService) {
        this.displayService = displayService;
    }


    @GetMapping("/{id}")
    public String getDisplay(@PathVariable long id) {
        return displayService.getProduct(id);
    }

}
