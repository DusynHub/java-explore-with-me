package ru.practicum.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication()
@EntityScan(basePackages = {"ru", "stats-module/stats-client-module"})
public class StatsServerModule {
	public static void main(String[] args) {
		SpringApplication.run(StatsServerModule.class, args);
	}
}
