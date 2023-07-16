package com.alexpera.rankerbackend;

import com.alexpera.rankerbackend.config.MongoDBUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RankerBackendApplication {

    public static void main(String[] args) {
        MongoDBUtil.init();
        SpringApplication.run(RankerBackendApplication.class, args);

        // retrieve data from database
    }

}
