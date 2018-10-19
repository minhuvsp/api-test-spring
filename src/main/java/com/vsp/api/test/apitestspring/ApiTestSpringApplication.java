package com.vsp.api.test.apitestspring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import com.vsp.api.productbusupdate.config.CommonConfig;

@SpringBootApplication
@Import({CommonConfig.class})
public class ApiTestSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiTestSpringApplication.class, args);
	}
}
