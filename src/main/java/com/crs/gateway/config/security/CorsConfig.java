package com.crs.gateway.config.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.http.CorsBeanDefinitionParser;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.server.ServerWebExchange;

@Configuration
public class CorsConfig {
	
	@Bean
	public CorsConfigurationSource corsConfigurationSource()
	{
		CorsConfiguration config=new CorsConfiguration();
		
		config.setAllowedOrigins(List.of(
				"http://localhost:3000",  //React
				"http://localhost:5173" , //vite
				"http://localhost:4200"  //Angular
				)
				);
		
		config.setAllowedMethods(List.of(
				"GET","POST","PUT","DELETE","OPTIONS"));
		
		config.setAllowedHeaders(List.of("*"));
		config.setAllowCredentials(true);
		
		UrlBasedCorsConfigurationSource source=
				new UrlBasedCorsConfigurationSource();
		 source.registerCorsConfiguration("/**", config);
		 
		 return source;
			
	}

}
//Flutter mobile apps ignore CORS
//Web clients covered
//Secure defaults