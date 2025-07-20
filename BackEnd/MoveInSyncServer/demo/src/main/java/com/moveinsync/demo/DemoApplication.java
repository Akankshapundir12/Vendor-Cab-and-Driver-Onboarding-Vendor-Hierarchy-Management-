package com.moveinsync.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		// Load users into cache on application startup
		CsvUserDatabase.initialize();
		SpringApplication.run(DemoApplication.class, args);
	}

}
