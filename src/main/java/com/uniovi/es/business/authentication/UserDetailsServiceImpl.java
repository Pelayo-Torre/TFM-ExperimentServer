package com.uniovi.es.business.authentication;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.uniovi.es.model.Investigator;
import com.uniovi.es.persistence.InvestigatorDAO;
import com.uniovi.es.security.PrincipalUser;

import java.util.*;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private InvestigatorDAO investigatorDAO;
	
	@Override
	public UserDetails loadUserByUsername(String login)
			throws UsernameNotFoundException {
		Investigator investigator = null;	
		
		if(login != null) {
			investigator = investigatorDAO.findByUsername(login.toLowerCase());
		}
		
//		if(investigator == null) {
//			investigator = usersRepository.findByUuid(login);
//		}
		
		if (investigator == null) {
			new UsernameNotFoundException(
					"No se ha encontrado al usuario: " + login);
		}
		
		Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
		grantedAuthorities
				.add(new SimpleGrantedAuthority(investigator.getRole().name()));

		return PrincipalUser.getUser(investigator);
	}

}