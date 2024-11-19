package com.sprarta.sproutmarket.config;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExceptionTestController {
    @GetMapping("/test")
    protected void testEndPoint() {
        throw new RuntimeException("테스트");
    }
}
