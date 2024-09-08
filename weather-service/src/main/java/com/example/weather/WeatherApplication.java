package com.example.weather;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(
				title = "Weather Service API",
				description = "Weather Service", version = "1.0.0",
				contact = @Contact(
						name = "marine",
						email = "marinaejoy1@yandex.ru",
						url = "https://github.com/kenyantea/"
				)
		)
)
public class WeatherApplication {
	public static void main(String[] args) {
		SpringApplication.run(WeatherApplication.class, args);
	}
}
