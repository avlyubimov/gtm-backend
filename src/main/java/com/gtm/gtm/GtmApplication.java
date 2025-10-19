package com.gtm.gtm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GtmApplication {

	public static void main(String[] args) {
		SpringApplication.run(GtmApplication.class, args);
	}

}
