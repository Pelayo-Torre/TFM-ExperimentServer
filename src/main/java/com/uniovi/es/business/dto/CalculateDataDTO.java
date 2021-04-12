package com.uniovi.es.business.dto;

import java.util.List;

public class CalculateDataDTO {
	
	public List<Integer> strategys;
	public List<String> users;
	public String sceneID;
	public Long idExperiment;
	
	@Override
	public String toString() {
		return "CalculateDataDTO [strategys=" + strategys + ", users=" + users + ", sceneID=" + sceneID
				+ ", idExperiment=" + idExperiment + "]";
	}
	
}
