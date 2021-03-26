package com.uniovi.es.business.dto;

import java.util.List;

public class CalculateDataDTO {
	
	public List<Integer> strategys;
	public String sessionID;
	public String sceneID;
	
	@Override
	public String toString() {
		return "CalculateDataDTO [strategys=" + strategys + ", sessionID=" + sessionID + ", sceneID=" + sceneID + "]";
	}
}
