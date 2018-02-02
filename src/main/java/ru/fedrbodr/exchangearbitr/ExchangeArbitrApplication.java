package ru.fedrbodr.exchangearbitr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication
public class ExchangeArbitrApplication extends SpringBootServletInitializer {
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(ExchangeArbitrApplication.class);
	}

	public static void main(String[] args) throws InterruptedException {
		SpringApplication.run(ExchangeArbitrApplication.class, args);
	}
}
