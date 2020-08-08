package com.uniovi.es.business.dto.assembler;

import com.uniovi.es.business.dto.ExperimentDTO;
import com.uniovi.es.model.Experiment;
import com.uniovi.es.model.StatusExperiment;

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
		experiment.setStatus(StatusExperiment.valueOf(dto.status));
	}

}
