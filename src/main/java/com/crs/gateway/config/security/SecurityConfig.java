package com.crs.gateway.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
	
	@Bean
	public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
		return http
				//Disable CSRF Token which is by default enable for spring boot 
				//application
				.csrf(ServerHttpSecurity.CsrfSpec::disable)
				
				//CORS handle seapratly CORS should be configured explicitly, not defaulted
				.cors(cors->{})
				//Stateless API Gateway 
				.securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
				
				//Route Security Rules
				.authorizeExchange(exchange ->
				   exchange.pathMatchers(
						   "/swagger-ui/**",
						   "/v3/api-docs/**")
				   .permitAll()
				   .pathMatchers("/actuator/health/**").permitAll()
				   .pathMatchers("/actuator/**").hasRole("ADMIN")
				   .pathMatchers("/api/auth/**").permitAll()
				   .anyExchange().authenticated()
				   )
				//JWT validation
				.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()).authenticationEntryPoint(
			              new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED)
				          ))
				.build();
				
	}

}
