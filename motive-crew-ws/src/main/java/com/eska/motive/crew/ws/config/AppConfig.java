package com.eska.motive.crew.ws.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Application configuration class
 * Can be used for future configuration properties
 * 
 * @author A.Juhaini
 */
@Configuration
@PropertySource(value = "application.properties", ignoreResourceNotFound = false)
public class AppConfig {
	
	// EskaCore authentication removed - using password-based authentication only
	// This class is kept for future configuration needs

}
