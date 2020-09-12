package com.uniovi.es.business.dto;

import java.util.Date;

public class PetitionDTO {
	
	public Long id;
	public Long idInvestigator;
	public String statusPetition;
	public Date shippingDate;
	public Date answerDate;
	public Boolean manager;
	public Boolean creator;
	
	//Datos del experimento asociado a la petición
	public Long idExperiment;
	public String title;
	public String description;
	public String statusExperiment;
	
	//Datos del investigador receptor
	public String name;
	public String surname;
	public String username;
	public String mail;
	
	
	@Override
	public String toString() {
		return "PetitionDTO [id=" + id + ", idInvestigator=" + idInvestigator + ", statusPetition=" + statusPetition
				+ ", shippingDate=" + shippingDate + ", answerDate=" + answerDate + ", manager=" + manager
				+ ", creator=" + creator + ", idExperiment=" + idExperiment + ", title=" + title + ", description="
				+ description + ", statusExperiment=" + statusExperiment + ", name=" + name + ", surname=" + surname
				+ ", username=" + username + ", mail=" + mail + "]";
	}
		
}
