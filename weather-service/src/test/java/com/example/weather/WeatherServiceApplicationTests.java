package com.example.weather;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Epic("Application test")
class WeatherServiceApplicationTests {

	@Test
	@Description("application starts")
	void contextLoads() {
	}

}
