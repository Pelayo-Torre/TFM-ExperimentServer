package com.uniovi.es.security;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.uniovi.es.model.Investigator;

public class PrincipalUser implements UserDetails {

	private static final long serialVersionUID = 1L;

	private Collection<? extends GrantedAuthority> auths;

	private Long id;
	private String username;
	@JsonIgnore
	private String password;

	public PrincipalUser(Long id, String username, String password,
			Collection<? extends GrantedAuthority> authorities) {
		this.id = id;
		this.password = password;
		this.auths = authorities;
		this.username = username;
	}

	public static PrincipalUser getUser(Investigator investigator) {
		List<GrantedAuthority> authorities = new ArrayList<>();
		SimpleGrantedAuthority a = new SimpleGrantedAuthority(investigator.getRole().name());
		
		authorities.add(a);
		PrincipalUser pu = new PrincipalUser(investigator.getId(), investigator.getMail(),
				investigator.getPassword(), authorities);
		
		return pu;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return auths;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}
	
	public Long getId() {
		return id;
	}
	
	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
			
		}
		if (object == null || getClass() != object.getClass()) {
			return false;
		}

		PrincipalUser user = (PrincipalUser) object;
		return Objects.equals(id, user.id);
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
