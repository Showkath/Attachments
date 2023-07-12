package com.sap.backend.config;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.core.convert.converter.Converter;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.SecurityFilterChain;


import com.sap.cloud.security.xsuaa.XsuaaServiceConfiguration;//latest
//import com.sap.cloud.security.spring.config.XsuaaServiceConfiguration;
//import com.sap.cloud.security.spring.token.authentication.AuthenticationToken;

import com.sap.cloud.security.xsuaa.token.TokenAuthenticationConverter;

@Configuration
// @EnableWebSecurity
@EnableWebSecurity(debug = false)
//@AllArgsConstructor
// TODO "debug" may include sensitive information. Do not use in a production
//@PropertySource(factory = XsuaaServicePropertySourceFactory.class, value = { "" })
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
//NOSONAR
public class SecurityConfig {

	////https://github.com/SAP/cloud-security-services-integration-library/blob/main/samples/spring-security-basic-auth/src/main/java/sample/spring/xsuaa/config/SecurityConfiguration.java
	//Latest
	////https://github.com/SAP/cloud-security-services-integration-library/blob/main/samples/spring-security-hybrid-usage/src/main/java/sample/spring/security/SecurityConfiguration.java
	
		// Note : Use xsuaaServiceConfiguration  getJwtAuthoritiesConverter or [ authConverter + MyCustomHybridTokenAuthenticationConverter ]
			
	//@Autowired
    //Converter<Jwt, AbstractAuthenticationToken> authConverter;//// Required only when Xsuaa is used
	
	

  			
	@Autowired
	XsuaaServiceConfiguration xsuaaServiceConfiguration;

	private static final Logger LOGGER = LoggerFactory.getLogger(SecurityConfig.class);
	
	
 
	
		@Bean
		public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {


			http
				
			.csrf(csrf ->
			csrf.disable())
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))			 
				
				.authorizeHttpRequests(authorizeReq -> authorizeReq
				
				.requestMatchers("/*/ui/*")//   */frontendui/*
				.hasAuthority("display")
				
				.requestMatchers("/v2/api-docs", "/swagger-resources/**", "/swagger-ui.html",
						"/webjars/**", "/error",
						/* Probably not needed */"/swagger.json").permitAll()
						
				   .anyRequest().authenticated())
			
				// Note : Use xsuaaServiceConfiguration  getJwtAuthoritiesConverter or [ authConverter + MyCustomHybridTokenAuthenticationConverter ]
				/*
				.oauth2ResourceServer(oauth2ResourceServer ->
				oauth2ResourceServer.jwt(jwt ->
						jwt.jwtAuthenticationConverter(new MyCustomHybridTokenAuthenticationConverter())));

						//https://github.com/SAP/cloud-security-services-integration-library/blob/main/samples/spring-security-hybrid-usage/src/main/java/sample/spring/security/SecurityConfiguration.java
						// Adjust the converter to represent your use case
                                            // Use MyCustomHybridTokenAuthenticationConverter when IAS and XSUAA is used
                                            // Use MyCustomIasTokenAuthenticationConverter when only IAS is used
											 */
						
				
				.oauth2ResourceServer(oauth2ResourceServer ->
				oauth2ResourceServer.jwt(jwt ->
						jwt.jwtAuthenticationConverter(getJwtAuthoritiesConverter())));
						
				
				return http.build();
		}
		
		
		//https://github.com/SAP/cloud-security-services-integration-library/blob/main/samples/spring-security-xsuaa-usage/src/main/java/sample/spring/xsuaa/SecurityConfiguration.java
	
		
		Converter<Jwt, AbstractAuthenticationToken> getJwtAuthoritiesConverter() {
		
			TokenAuthenticationConverter converter = new TokenAuthenticationConverter(
					xsuaaServiceConfiguration);
			converter.setLocalScopeAsAuthorities(true);
			
			return converter;
		}
		
		
		
		

	///latest 
	/**
     * Workaround for hybrid use case until Cloud Authorization Service is globally available.
     */
	
	 /* 
    class MyCustomHybridTokenAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

        public AbstractAuthenticationToken convert(Jwt jwt) {
            if (jwt.hasClaim(TokenClaims.XSUAA.EXTERNAL_ATTRIBUTE)) {
                return authConverter.convert(jwt);
            }
            return new AuthenticationToken(jwt, deriveAuthoritiesFromGroup(jwt));
        }

        private Collection<GrantedAuthority> deriveAuthoritiesFromGroup(Jwt jwt) {
            Collection<GrantedAuthority> groupAuthorities = new ArrayList<>();
            if (jwt.hasClaim(TokenClaims.GROUPS)) {
                List<String> groups = jwt.getClaimAsStringList(TokenClaims.GROUPS);
                for (String group : groups) {
                    groupAuthorities.add(new SimpleGrantedAuthority(group.replace("IASAUTHZ_", "")));
                }
            }
            return groupAuthorities;
        }
    }
*/

    /**
     * Workaround for IAS only use case until Cloud Authorization Service is globally available.
     */
	
	 /*
    static class MyCustomIasTokenAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

        public AbstractAuthenticationToken convert(Jwt jwt) {
            final List<String> groups = jwt.getClaimAsStringList(TokenClaims.GROUPS);
            final List<GrantedAuthority> groupAuthorities = groups == null ? Collections.emptyList()
                    : groups.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
            return new AuthenticationToken(jwt, groupAuthorities);
        }
    }
	 */
   
	
}
