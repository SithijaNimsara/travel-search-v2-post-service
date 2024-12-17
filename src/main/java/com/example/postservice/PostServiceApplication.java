package com.example.postservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class PostServiceApplication {

    private static final Logger logger = LoggerFactory.getLogger(PostServiceApplication.class);

    public static void main(String[] args) {
        logger.info("PostServiceApplication --------- v3 -------- ");
        SpringApplication.run(PostServiceApplication.class, args);
        logger.info("PostServiceApplication --------- v3 -------- ");
    }

}
