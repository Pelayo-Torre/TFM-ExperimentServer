package com.uniovi.es.business.dto;

import java.util.Date;

public class ExperimentDTO {
	
	public Long id;
	public String title;
	public String description;
	public String status;
	public Date creationDate;
	
	public Long idInvestigator; //ID del investigador creador

	@Override
	public String toString() {
		return "ExperimentDTO [id=" + id + ", title=" + title + ", description=" + description + ", status=" + status
				+ ", creationDate=" + creationDate + ", idInvestigator=" + idInvestigator + "]";
	}

	
}
