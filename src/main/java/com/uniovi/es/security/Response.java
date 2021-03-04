package com.uniovi.es.security;


import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;


public class Response {

	private String type = "Bearer";
	private String username;
	private String token;
	private Collection<? extends GrantedAuthority> auths;
	
	public Response(String username, String token,
			Collection<? extends GrantedAuthority> auths) {
		this.token = token;
		this.username = username;
		this.auths = auths;
	}

	public String getAccessToken() {
		return token;
	}

	public void setAccessToken(String accessToken) {
		this.token = accessToken;
	}

	public String getTokenType() {
		return type;
	}

	public void setTokenType(String tokenType) {
		this.type = tokenType;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Collection<? extends GrantedAuthority> getAuthorities() {
		return auths;
	}


}
