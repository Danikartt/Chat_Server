package com.example.servidor_final;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collections;

@SpringBootApplication
public class 	ServidorFinalApplication {
	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(ServidorFinalApplication.class);
		app.setDefaultProperties(Collections.singletonMap("server.port", "8081"));
		app.run(args);
	}

}
