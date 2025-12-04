package com.devassignment.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class DevassignmentApplication {

	public static void main(String[] args) {
		SpringApplication.run(DevassignmentApplication.class, args);
	}

}
