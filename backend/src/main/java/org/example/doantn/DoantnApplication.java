package org.example.doantn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.Collections;

@SpringBootApplication
public class DoantnApplication {

    public static void main(String[] args) {
        SpringApplication.run(DoantnApplication.class, args);
    }


}
