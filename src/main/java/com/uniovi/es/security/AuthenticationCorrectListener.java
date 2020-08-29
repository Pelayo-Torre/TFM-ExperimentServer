package com.uniovi.es.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import com.uniovi.es.business.authentication.LoginService;


@Component
public class AuthenticationCorrectListener
		implements ApplicationListener<AuthenticationSuccessEvent> {

	@Autowired
	private LoginService loginService;
	
	@Override
	public void onApplicationEvent(AuthenticationSuccessEvent e) {
		String username = e.getAuthentication().getName();
		loginService.correct(username);
	}

}
