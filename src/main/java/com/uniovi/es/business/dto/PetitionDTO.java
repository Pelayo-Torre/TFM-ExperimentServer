package com.uniovi.es.business.dto;

import java.util.Date;

public class PetitionDTO {
	
	public Long id;
	public Long idInvestigator;
	public Long idExperiment;
	public String statusPetition;
	public Date shippingDate;
	public Date answerDate;
	public String description;
	public Boolean manager;
	public Boolean creator;
	
	@Override
	public String toString() {
		return "PetitionDTO [id=" + id + ", idInvestigator=" + idInvestigator + ", idExperiment=" + idExperiment
				+ ", StatusPetition=" + statusPetition + ", shippingDate=" + shippingDate + ", answerDate=" + answerDate
				+ ", description=" + description + ", manager=" + manager + ", creator=" + creator + "]";
	}
	
	

}
