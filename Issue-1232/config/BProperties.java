package com.sap.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import lombok.Data;


@Data
@Component
//@Configuration
@ConfigurationProperties(prefix = "config")
public class BackendProperties {
	
	
	@Value("${config.delete}")
	boolean delete;
	@Value("${config.corePoolSize}")
	int corePoolSize;
	@Value("${config.maxPoolSize}")
	int maxPoolSize;
	
	
}