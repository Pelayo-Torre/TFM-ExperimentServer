package com.uniovi.es.business.dto;

public class NoteDTO {

	public Long id;
	public String title;
	public String description;
	
	@Override
	public String toString() {
		return "NoteDTO [id=" + id + ", title=" + title + ", description=" + description + "]";
	}
	
}
