package com.crs.gateway.config.ratelimit;


import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;

import reactor.core.publisher.Mono;

@Configuration
public class RateLimitKeyResolver {
	
	@Bean
	public KeyResolver userKeyResolver() {
		  return exchange ->
          ReactiveSecurityContextHolder.getContext()
              .map(ctx -> ctx.getAuthentication().getName())
              .switchIfEmpty(
                  Mono.just(
                      exchange.getRequest()
                              .getRemoteAddress()
                              .getAddress()
                              .getHostAddress()
                  )
              );
  }
	}


