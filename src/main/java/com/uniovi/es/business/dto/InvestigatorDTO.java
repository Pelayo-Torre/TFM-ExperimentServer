package com.uniovi.es.business.dto;

public class InvestigatorDTO {
	
	public Long id;
	public String name;
	public String surname;
	public String email;
	public String phone;
	public String username;
	
	public Boolean creator;
	public Boolean manager;
	
	@Override
	public String toString() {
		return "InvestigatorDTO [id=" + id + ", name=" + name + ", surname=" + surname + ", email=" + email + ", phone="
				+ phone + ", username=" + username + "]";
	}
	
}
