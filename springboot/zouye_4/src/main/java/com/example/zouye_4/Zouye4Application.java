package com.example.zouye_4;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;

import java.util.HashSet;
import java.util.Set;


@SpringBootApplication(exclude = {SecurityAutoConfiguration.class })
@MapperScan("com.example.zouye_4")
public class Zouye4Application {

    public static void main(String[] args) {
        SpringApplication.run(Zouye4Application.class, args);
    }

}
