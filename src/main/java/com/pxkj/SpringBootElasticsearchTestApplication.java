package com.pxkj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class SpringBootElasticsearchTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootElasticsearchTestApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate(){
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate;
    }

}
