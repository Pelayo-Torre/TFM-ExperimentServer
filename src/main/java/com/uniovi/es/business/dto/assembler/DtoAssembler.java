package com.uniovi.es.business.dto.assembler;

import java.util.ArrayList;
import java.util.List;

import com.uniovi.es.business.dto.DemographicDataDTO;
import com.uniovi.es.business.dto.DemographicDataTypeDTO;
import com.uniovi.es.business.dto.ExperimentDTO;
import com.uniovi.es.business.dto.InvestigatorDTO;
import com.uniovi.es.business.dto.NoteDTO;
import com.uniovi.es.business.dto.PetitionDTO;
import com.uniovi.es.business.dto.RequestDTO;
import com.uniovi.es.business.dto.SceneDTO;
import com.uniovi.es.business.dto.UserDTO;
import com.uniovi.es.model.DemographicData;
import com.uniovi.es.model.Experiment;
import com.uniovi.es.model.Investigator;
import com.uniovi.es.model.Note;
import com.uniovi.es.model.Petition;
import com.uniovi.es.model.PetitionNotRegistered;
import com.uniovi.es.model.Request;
import com.uniovi.es.model.User;
import com.uniovi.es.model.types.DemographicDataType;

public class DtoAssembler {
	
	public static ExperimentDTO toDTO(Experiment experiment, Investigator investigator, Boolean rol) {
		ExperimentDTO dto = new ExperimentDTO();
		
		if(experiment != null) {
			dto.id = experiment.getId();
			dto.title = experiment.getTitle();
			dto.description = experiment.getDescription();
			dto.status = experiment.getStatus().name();
			dto.creationDate = experiment.getCreationDate();
			
			if(rol != null)
				dto.isManagerInvestigatorInSession = rol;
			
			if(experiment.getDemographicData() != null && experiment.getDemographicData().size() > 0) {
				dto.demographicData = new ArrayList<DemographicDataDTO>();
				for(DemographicData dd : experiment.getDemographicData()) {
					dto.demographicData.add(toDTO(dd));
				}
			}
		}
				
		if(investigator != null) {
			dto.nameInvestigator = investigator.getName();
			dto.surnameInvestigator = investigator.getSurname();
			dto.mailInvestigator = investigator.getMail();
			dto.idInvestigator = investigator.getId();
		}
		
		return dto;
	}
	
	private static DemographicDataDTO toDTO(DemographicData dd) {
		DemographicDataDTO dto = new DemographicDataDTO();
		
		dto.name = dd.getName();
		dto.type = dd.getType().name();
		
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
		dto.role = investigator.getRole().name();
		dto.registrationDate = investigator.getRegistrationDate();
				
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
		investigator.setPassword(dto.password);
	}
	
	public static void fillDataUpdate(Investigator investigator, InvestigatorDTO dto) {
		
		investigator.setName(dto.name);
		investigator.setSurname(dto.surname);
		investigator.setMail(dto.mail);
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
	
	public static RequestDTO toDto(Request request) {
		RequestDTO dto = new RequestDTO();
		
		dto.id = request.getId();
		dto.idInvestigator = request.getInvestigator().getId();
		dto.shippingDate = request.getShippingDate();
		dto.answerDate = request.getAnswerDate();
		dto.status = request.getStatus().name();
		dto.name = request.getInvestigator().getName();
		dto.surname = request.getInvestigator().getSurname();
		dto.mail = request.getInvestigator().getMail();
		
		return dto;
	}
	
	public static List<RequestDTO> toListRequests(List<Request> requests){
		List<RequestDTO> requestsDTO = new ArrayList<RequestDTO>();
		
		for(Request request : requests) {
			requestsDTO.add(toDto(request));
		}
		
		return requestsDTO;
	}
	
	public static void fillData(Request request, RequestDTO dto) {
		request.setAnswerDate(dto.answerDate);
		request.setShippingDate(dto.shippingDate);
	}
	
	public static void fillData(Petition petition, PetitionNotRegistered p) {
		petition.setIdInvestigatorSend(p.getIdInvestigatorSend());
		petition.setManager(p.getManager());
		petition.setShippingDate(p.getShippingDate());
		
	}
	
	public static List<DemographicDataTypeDTO> toListDemographicDataType(List<DemographicDataType> list){
		List<DemographicDataTypeDTO> listDtos = new ArrayList<DemographicDataTypeDTO>();
		
		for(DemographicDataType type : list) {
			DemographicDataTypeDTO dto = new DemographicDataTypeDTO();
			dto.type = type.name();
			listDtos.add(dto);
		}
		
		return listDtos;
	}
	
	public static UserDTO toDTO(User user) {
		UserDTO dto = new UserDTO();
		
		dto.browserEngine = user.getBrowserEngine();
		dto.browserLanguage = user.getBrowserLanguage();
		dto.browserName = user.getBrowserName();
		dto.browserPlatform = user.getBrowserPlatform();
		dto.browserOnline = user.getBrowserOnline();
		dto.browserVersion1a = user.getBrowserVersion1a();
		dto.browserVersion1b = user.getBrowserVersion1b();
		dto.dataCookies1 = user.getDataCookies1();
		dto.dataCookies2 = user.getDataCookies2();
		dto.dataCookiesEnabled = user.getDataCookiesEnabled();
		dto.dataStorage = user.getDataStorage();
		dto.idExperiment = user.getExperiment().getId();
		dto.javaEnabled = user.getJavaEnabled();
		dto.locale = user.getLocale();
		dto.pageon = user.getPageon();
		dto.previousSites = user.getPreviousSites();
		dto.referrer = user.getReferrer();
		dto.remoteAddress = user.getRemoteAddress();
		dto.remoteHost = user.getRemoteHost();
		dto.remotePort = user.getRemotePort();
		dto.scrColorDepth = user.getScrColorDepth();
		dto.scrPixelDepth = user.getScrPixelDepth();
		dto.sessionId = user.getSessionId();
		dto.sizeAvailH = user.getSizeAvailH();
		dto.sizeAvailW = user.getSizeAvailW();
		dto.sizeDocH = user.getSizeDocH();
		dto.sizeDocW = user.getSizeDocW();
		dto.sizeInH = user.getSizeInH();
		dto.sizeInW = user.getSizeInW();
		dto.sizeScreenH = user.getSizeScreenH();
		dto.sizeScreenW = user.getSizeScreenW();
		dto.timeOpened = user.getTimeOpened();
		dto.timeStamp = user.getTimeStamp();
		dto.timezone = user.getTimezone();
		
		return dto;
	}
	
	public static List<UserDTO> toListUserDTO(List<User> list){
		List<UserDTO> listDtos = new ArrayList<UserDTO>();
		
		for(User user : list) {
			listDtos.add(toDTO(user));
		}
		
		return listDtos;
	}
	
	public static List<SceneDTO> toListSceneDTO(List<String> list){
		List<SceneDTO> listDtos = new ArrayList<SceneDTO>();
		
		for(String scene : list) {
			listDtos.add(toDTO(scene));
		}
		
		return listDtos;
	}

	private static SceneDTO toDTO(String scene) {
		SceneDTO dto = new SceneDTO();
		
		dto.scene = scene;
		
		return dto;
	}

}
