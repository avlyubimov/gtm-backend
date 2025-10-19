package com.gtm.gtm;

import org.springframework.boot.SpringApplication;

public class TestGtmApplication {

	public static void main(String[] args) {
		SpringApplication.from(GtmApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
