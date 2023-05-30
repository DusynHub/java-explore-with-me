package ru.practicum.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

@SpringBootApplication
@EntityScan(basePackages = {"ru.practicum"}, basePackageClasses = {Jsr310JpaConverters.class})
public class StatsServerModule {
	public static void main(String[] args) {
		SpringApplication.run(StatsServerModule.class, args);
	}
}
