package com.uniovi.es.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

import com.uniovi.es.business.authentication.LoginService;


@Component
public class AuthenticationFailureListener implements
		ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

	@Autowired
	private LoginService loginService;

	@Override
	public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent e) {
		String username = e.getAuthentication().getName();
		loginService.fail(username);
	}

}
