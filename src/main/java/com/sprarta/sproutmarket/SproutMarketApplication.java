package com.sprarta.sproutmarket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableAsync
@EnableScheduling
@EnableJpaAuditing
@EnableCaching
@SpringBootApplication
public class SproutMarketApplication {

    public static void main(String[] args) {
        SpringApplication.run(SproutMarketApplication.class, args);
    }

}
