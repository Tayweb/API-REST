package com.br.apirest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EntityScan(basePackages = { "com.br.apirest.model" })
@ComponentScan(basePackages = { "com.*" })
@EnableJpaRepositories(basePackages = { "com.br.apirest.repository" })
@EnableTransactionManagement
@EnableWebMvc
@RestController
@EnableAutoConfiguration
@EnableCaching
public class ApiRestApplication implements WebMvcConfigurer{

	public static void main(String[] args) {
		SpringApplication.run(ApiRestApplication.class, args);
		//System.out.println(new BCryptPasswordEncoder().encode("admin"));
	}
	
	@Override
	public void addCorsMappings(CorsRegistry registry) { //Mapeia os controllers e os End Point de forma global
		registry.addMapping("/usuarios/**").allowedMethods("POST", "GET").allowedOrigins("localhost:8080");
		
		//Liberando apenas post para o usuario/site
	}
	

}
