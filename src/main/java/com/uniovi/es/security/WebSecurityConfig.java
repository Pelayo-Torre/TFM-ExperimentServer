package com.uniovi.es.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.uniovi.es.business.authentication.UserDetailsServiceImpl;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        prePostEnabled = true
)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public FilterToken authenticationJwtTokenFilter() {
        return new FilterToken();
    }
    
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
    	return new BCryptPasswordEncoder();
    }
	
	@Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Autowired
    private EntryPoint uhler;
    
    @Autowired
    private UserDetailsServiceImpl detailsService;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
    
    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder
                .userDetailsService(detailsService)
                .passwordEncoder(passwordEncoder());
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable().
                authorizeRequests()
                .antMatchers(
                		"/css/**", 
                		"/img/**", 
                		"/script/**", 
                		"/login/**"
                	).permitAll()
				.antMatchers(
						"/investigator/**", 
						"/experiment/**", 
						"/binnacle/**",
						"/petition/**")
					.hasAnyAuthority("INVESTIGATOR_EVALUATION", "INVESTIGATOR_VALIDATED")
//				.antMatchers(
//						"/association/validate/name/**",
//						"/association/edit/**",
//						"/association/detail",
//						"/user/application/**",
//						"/user/association/force/key",
//						"/backup/admin/restore")
//					.hasAuthority("ROLE_ADMIN_ASSOCIATION")
//				.antMatchers(
//						"/association/image")
//					.hasAnyAuthority("ROLE_ADMIN_ASSOCIATION","ROLE_USER_APPLICATION")
//				.antMatchers(
//						"/association/**",
//						"/user/admin/**",
//						"/backup/restore")
//					.hasAuthority("ROLE_ADMIN_PLATFORM")
//				.antMatchers(
//							"/backup")
//					.hasAnyAuthority("ROLE_ADMIN_ASSOCIATION","ROLE_ADMIN_PLATFORM")
//				.antMatchers(
//						"/user/edit/**",
//						"/user/change/password",
//						"/user/validate/**")
//					.hasAnyAuthority("ROLE_ADMIN_PLATFORM","ROLE_ADMIN_ASSOCIATION","ROLE_USER_APPLICATION")
				.antMatchers("/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .exceptionHandling().authenticationEntryPoint(uhler).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        
        //Se coge el token del usuario en sesión
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
    }
}
