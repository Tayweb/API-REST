package com.br.apirest.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.br.apirest.service.ImplementacaoUserDetailsService;

@Configuration
@EnableWebSecurity
public class WebConfigSecurity extends WebSecurityConfigurerAdapter {

	@Autowired
	private ImplementacaoUserDetailsService implementacaoUserDetailsService;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// Ativando a proteção contra usuario que não estão validados por tokem
		http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())

				// Ativando a restrição a Url
				.disable().authorizeRequests().antMatchers("/").permitAll().antMatchers("/index").permitAll()

				.antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
				
				// URL de Logout - redireciona após o usuario deslogar do sistema
				.anyRequest().authenticated().and().logout().logoutSuccessUrl("/index")
				

				// Mapeia URL de Logout e insvalida o usuario
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))

				// Filtra requisições de Login para autenticação
				.and()
				.addFilterBefore(new JWTLoginFilter("/login", authenticationManager()),
						UsernamePasswordAuthenticationFilter.class)

				// Filtra demais requisições para verificar a presenção do TOKEN JWT no HEADER HTTP

				.addFilterBefore(new JWTApiAutenticacaoFilter(), UsernamePasswordAuthenticationFilter.class);

	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {

		// Service que irá consultar o usuario no banco de dados
		auth.userDetailsService(implementacaoUserDetailsService).passwordEncoder(new BCryptPasswordEncoder());
	}
}
