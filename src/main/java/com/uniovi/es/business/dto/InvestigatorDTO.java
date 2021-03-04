package com.uniovi.es.business.dto;

import java.util.Date;

public class InvestigatorDTO {
	
	public Long id;
	public String name;
	public String surname;
	public String mail;
	public String password;
	public String role;
	public Date registrationDate;
	
	public Boolean creator;
	public Boolean manager;
	
	//indica si el investigador tiene pedida la solicitud de cuenta
	public Boolean requestPending;
	
	@Override
	public String toString() {
		return "InvestigatorDTO [id=" + id + ", name=" + name + ", surname=" + surname + ", mail=" + mail
				+ ", role=" + role + ", creator=" + creator + ", manager=" + manager + "]";
	}
	
}
