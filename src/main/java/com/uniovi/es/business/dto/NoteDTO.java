package com.uniovi.es.business.dto;

import java.util.Date;

public class NoteDTO {

	public Long id;
	public Long idExperiment;
	public String title;
	public String description;
	public Date creationDate;
	
	
	@Override
	public String toString() {
		return "NoteDTO [id=" + id + ", idExperiment=" + idExperiment + ", title=" + title + ", description="
				+ description + ", creationDate=" + creationDate + "]";
	}
	
}
