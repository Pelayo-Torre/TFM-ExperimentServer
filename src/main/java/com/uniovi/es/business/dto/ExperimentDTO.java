package com.uniovi.es.business.dto;

import java.util.Date;
import java.util.List;


public class ExperimentDTO {
	
	public Long id;
	public String title;
	public String description;
	public String status;
	public Date creationDate;
	
	//Datos demográficos
	public List<DemographicDataDTO> demographicData;
	
	public Long idInvestigator; //ID del investigador creador
	
	public boolean isManagerInvestigatorInSession;
	
	//Datos del investigador creador del experimento
	public String nameInvestigator;
	public String surnameInvestigator;
	public String usernameInvestigator;
	public String mailInvestigator;
	
	@Override
	public String toString() {
		return "ExperimentDTO [id=" + id + ", title=" + title + ", description=" + description + ", status=" + status
				+ ", creationDate=" + creationDate + ", demographicData=" + demographicData + ", idInvestigator="
				+ idInvestigator + ", isManagerInvestigatorInSession=" + isManagerInvestigatorInSession
				+ ", nameInvestigator=" + nameInvestigator + ", surnameInvestigator=" + surnameInvestigator
				+ ", usernameInvestigator=" + usernameInvestigator + ", mailInvestigator=" + mailInvestigator + "]";
	}

}
