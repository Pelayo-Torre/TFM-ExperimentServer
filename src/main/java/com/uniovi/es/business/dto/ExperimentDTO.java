package com.uniovi.es.business.dto;

public class ExperimentDTO {
	
	public Long id;
	public String title;
	public String description;
	public String status;
	
	
	@Override
	public String toString() {
		return "ExperimentDTO [id=" + id + ", title=" + title + ", description=" + description + ", status=" + status
				+ "]";
	}
	
	

}
