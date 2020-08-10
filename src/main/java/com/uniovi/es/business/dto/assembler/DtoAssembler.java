package com.uniovi.es.business.dto.assembler;

import com.uniovi.es.business.dto.ExperimentDTO;
import com.uniovi.es.business.dto.InvestigatorDTO;
import com.uniovi.es.business.dto.PetitionDTO;
import com.uniovi.es.model.Experiment;
import com.uniovi.es.model.Investigator;
import com.uniovi.es.model.Petition;

public class DtoAssembler {
	
	public static ExperimentDTO toDTO(Experiment experiment) {
		ExperimentDTO dto = new ExperimentDTO();
		
		dto.id = experiment.getId();
		dto.title = experiment.getTitle();
		dto.description = experiment.getDescription();
		dto.status = experiment.getStatus().name();
		
		return dto;
	}
	
	public static void fillData(Experiment experiment, ExperimentDTO dto) {
		
		experiment.setTitle(dto.title);
		experiment.setDescription(dto.description);
	}
	
	public static InvestigatorDTO toDTO(Investigator investigator) {
		InvestigatorDTO dto = new InvestigatorDTO();
		
		dto.id = investigator.getId();
		dto.name = investigator.getName();
		dto.surname = investigator.getSurname();
		dto.email = investigator.getMail();
				
		return dto;
	}
	
	public static void fillData(Investigator investigator, InvestigatorDTO dto) {
		
		investigator.setName(dto.name);
		investigator.setSurname(dto.surname);
		investigator.setMail(dto.email);
	}
	
	public static PetitionDTO toDTO(Petition petition) {
		PetitionDTO dto = new PetitionDTO();
		
		dto.id = petition.getId();
		dto.description = petition.getDescription();
		dto.answerDate = petition.getAnswerDate();
		dto.shippingDate = petition.getShippingDate();
		dto.statusPetition = petition.getStatus().name();
		dto.manager = petition.getManager();
		dto.creator = petition.getCreator();
		
		return dto;
	}
	
	public static void fillData(Petition petition, PetitionDTO dto) {
		
		petition.setManager(dto.manager);
		petition.setDescription(dto.description);
	}

}
