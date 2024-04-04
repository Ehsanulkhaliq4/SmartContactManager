package com.smart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class MyConfig {
	
	@Bean
	public UserDetailsService getUserDetailsService() {
		
		return new UserDetailServiceImpl();
		
	}
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authenticationProvider=new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(this.getUserDetailsService());
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		return authenticationProvider;
	}
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http ) throws Exception {
		http.authorizeHttpRequests()
		.requestMatchers("/admin/**")
		.hasRole("ADMIN")
        .requestMatchers("/user/**")
        .hasRole("USER")
        .requestMatchers("/**")
        .permitAll()
        .and()
        .formLogin()
        .loginPage("/signin")
        .loginProcessingUrl("/dologin")
        .defaultSuccessUrl("/user/index")
        .failureUrl("/login-fail")
        .and()
        .csrf()
        .disable();
		
		DefaultSecurityFilterChain build = http.build();
		return build;
	}
	@Bean
public AuthenticationManager athenticationManagerBean(AuthenticationConfiguration configuration ) throws Exception {
		return configuration.getAuthenticationManager();
	}
}




























