package com.uniovi.es.business.dto;

public class NoteDTO {

	public Long id;
	public Long idExperiment;
	public String title;
	public String description;
	
	@Override
	public String toString() {
		return "NoteDTO [id=" + id + ", idExperiment=" + idExperiment + ", title=" + title + ", description="
				+ description + "]";
	}
	
	
	
}
