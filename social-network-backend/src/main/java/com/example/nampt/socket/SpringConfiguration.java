package com.example.nampt.socket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfiguration {

    @Bean
    public static ApplicationContextProvider contextProvider() {
        return new ApplicationContextProvider();
    }

}
