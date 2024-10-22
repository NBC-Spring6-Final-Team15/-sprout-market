package com.sprarta.sproutmarket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class SproutMarketApplication {

    public static void main(String[] args) {
        SpringApplication.run(SproutMarketApplication.class, args);
    }

}
