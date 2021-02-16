package com.uniovi.es.business.dto.assembler;

import java.util.ArrayList;
import java.util.List;

import com.uniovi.es.business.dto.DeviceDTO;
import com.uniovi.es.business.dto.ExperimentDTO;
import com.uniovi.es.business.dto.InvestigatorDTO;
import com.uniovi.es.business.dto.NoteDTO;
import com.uniovi.es.business.dto.PetitionDTO;
import com.uniovi.es.model.Experiment;
import com.uniovi.es.model.Investigator;
import com.uniovi.es.model.Note;
import com.uniovi.es.model.Petition;
import com.uniovi.es.model.types.Device;

public class DtoAssembler {
	
	public static ExperimentDTO toDTO(Experiment experiment, Investigator investigator, Boolean rol) {
		ExperimentDTO dto = new ExperimentDTO();
		
		if(experiment != null) {
			dto.id = experiment.getId();
			dto.title = experiment.getTitle();
			dto.description = experiment.getDescription();
			dto.status = experiment.getStatus().name();
			dto.creationDate = experiment.getCreationDate();
			
			//Demographic data
			dto.birthDate = experiment.getDemographicData().getBirthdate();
			dto.gender = experiment.getDemographicData().getGender().name();
			dto.idDevice = experiment.getDemographicData().getIdDevice();
			dto.laterality = experiment.getDemographicData().getLaterality().name();
			if(rol != null)
				dto.isManagerInvestigatorInSession = rol;
		}
				
		if(investigator != null) {
			dto.nameInvestigator = investigator.getName();
			dto.surnameInvestigator = investigator.getSurname();
			dto.mailInvestigator = investigator.getMail();
			dto.usernameInvestigator = investigator.getUsername();
			dto.idInvestigator = investigator.getId();
		}
		
		return dto;
	}
	
	public static List<ExperimentDTO> toList(List<Experiment> experiments){
		List<ExperimentDTO> list = new ArrayList<ExperimentDTO>();
		
		for(Experiment experiment: experiments) {
			list.add(toDTO(experiment, null, null));
		}
		
		return list;
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
		dto.mail = investigator.getMail();
		dto.username = investigator.getUsername();
		dto.role = investigator.getRole().name();
				
		return dto;
	}
	
	public static List<InvestigatorDTO> toListInvestigators(List<Investigator> investigators){
		List<InvestigatorDTO> list = new ArrayList<InvestigatorDTO>();

		for(Investigator investigator: investigators) {
			list.add(toDTO(investigator));
		}
		
		return list;
	}
	
	public static List<InvestigatorDTO> toListInvestigatorsOfPetitions(List<Petition> petitions){
		List<InvestigatorDTO> list = new ArrayList<InvestigatorDTO>();
		
		for(Petition petition: petitions) {
			InvestigatorDTO dto = toDTO(petition.getInvestigator());
			dto.creator = petition.getCreator();
			dto.manager = petition.getManager();
			list.add(dto);
		}
		
		return list;
	}
	
	public static void fillData(Investigator investigator, InvestigatorDTO dto) {
		
		investigator.setName(dto.name);
		investigator.setSurname(dto.surname);
		investigator.setMail(dto.mail);
		investigator.setUsername(dto.username);
		investigator.setPassword(dto.password);
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
		
		dto.idExperiment = petition.getExperiment().getId();
		dto.title = petition.getExperiment().getTitle();
		dto.description = petition.getExperiment().getDescription();
		dto.statusExperiment = petition.getExperiment().getStatus().name();
		
		dto.name = petition.getInvestigator().getName();
		dto.surname = petition.getInvestigator().getSurname();
		dto.mail = petition.getInvestigator().getMail();
		dto.username = petition.getInvestigator().getUsername();		
		
		return dto;
	}
	
	public static List<PetitionDTO> toListPetitions(List<Petition> petitions){
		List<PetitionDTO> list = new ArrayList<PetitionDTO>();
		
		for(Petition petition: petitions) {
			list.add(toDTO(petition));
		}
		
		return list;
	}
	
	public static void fillData(Petition petition, PetitionDTO dto) {
		
		petition.setManager(dto.manager);
		petition.setDescription(dto.description);
	}
	
	public static NoteDTO toDto(Note note) {
		NoteDTO dto = new NoteDTO();
		
		dto.id = note.getId();
		dto.title = note.getTitle();
		dto.description = note.getDescrition();
		dto.creationDate = note.getCreationDate();
		
		return dto;
	}
	
	public static List<NoteDTO> toListNotes(List<Note> notes){
		List<NoteDTO> list = new ArrayList<NoteDTO>();
		
		for(Note note: notes) {
			list.add(toDto(note));
		}
		
		return list;
	}
	
	public static void fillData(Note note, NoteDTO dto) {
		
		note.setTitle(dto.title);
		note.setDescrition(dto.description);
	}
	
	public static DeviceDTO toDTO(Device device) {
		DeviceDTO dto = new DeviceDTO();
		
		dto.id = device.getId();
		dto.name = device.getName();
		
		return dto;
	}
	
	public static List<DeviceDTO> toListDevices(List<Device> devices){
		List<DeviceDTO> devicesDTO = new ArrayList<DeviceDTO>();
		
		for(Device device : devices) {
			devicesDTO.add(toDTO(device));
		}
		
		return devicesDTO;
	}

}
