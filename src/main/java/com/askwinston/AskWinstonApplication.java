package com.askwinston;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class})
@ServletComponentScan
public class AskWinstonApplication {

    public static void main(String[] args) {
        SpringApplication.run(AskWinstonApplication.class, args);
    }

}
