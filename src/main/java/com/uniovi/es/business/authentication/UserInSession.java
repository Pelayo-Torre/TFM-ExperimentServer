package com.uniovi.es.business.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.uniovi.es.model.Investigator;
import com.uniovi.es.persistence.InvestigatorDAO;

@Component
public class UserInSession {
	
	@Autowired
	private InvestigatorDAO investigatorDAO;

	/**
	 * Devuelve el investigador en sesión
	 * 
	 * @return el investigator
	 */
	public Investigator getInvestigator() {
		Authentication auth = SecurityContextHolder.getContext()
				.getAuthentication();
		Investigator investigator = investigatorDAO.findByUsername(auth.getName().toLowerCase());
		return investigator;
	}

}
