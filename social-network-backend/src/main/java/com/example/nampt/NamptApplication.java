package com.example.nampt;

import com.example.nampt.repository.FriendRepository;
import com.example.nampt.repository.UserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class NamptApplication implements WebMvcConfigurer {


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/upload/**")
                .addResourceLocations("file:////" + System.getProperty("user.dir") + "/src/main/upload/");
    }

    public static void main(String[] args) {
        SpringApplication.run(NamptApplication.class, args);
    }

}
