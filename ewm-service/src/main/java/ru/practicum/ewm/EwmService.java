package ru.practicum.ewm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;


@SpringBootApplication
@ComponentScan(basePackages = {"ru"})
@EnableWebMvc
public class EwmService {
	public static void main(String[] args) {
		SpringApplication.run(EwmService.class, args);
	}
}
