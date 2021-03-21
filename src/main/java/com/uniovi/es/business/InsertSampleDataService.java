package com.uniovi.es.business;

import java.util.Date;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.uniovi.es.model.Investigator;
import com.uniovi.es.model.types.Role;
import com.uniovi.es.persistence.InvestigatorDAO;

@Service
public class InsertSampleDataService {

	@Autowired
	private InvestigatorDAO investigatorDAO;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	//@PostConstruct
	public void init() {		
		Investigator investigator = new Investigator("admin@gmail.com");
		investigator.setName("Administrador");
		investigator.setPassword(bCryptPasswordEncoder.encode("admin**2021-"));
		investigator.setRegistrationDate(new Date());
		investigator.setSurname("Administrador");
		investigator.setToAdministrator();
		
		investigatorDAO.save(investigator);
	}
	
}
