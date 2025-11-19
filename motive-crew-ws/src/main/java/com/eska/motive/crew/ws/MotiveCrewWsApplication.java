package com.eska.motive.crew.ws;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author A.Juhaini
 */

@SpringBootApplication(scanBasePackages = {"com.eska" })
public class MotiveCrewWsApplication {

	public static void main(String[] args) {
		SpringApplication.run(MotiveCrewWsApplication.class, args);
	}

}
