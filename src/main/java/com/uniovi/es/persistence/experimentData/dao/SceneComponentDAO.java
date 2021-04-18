package com.uniovi.es.persistence.experimentData.dao;

import java.util.List;

import com.uniovi.es.model.ComponentData;

public interface SceneComponentDAO {
	
	public List<ComponentData> getComponents(String sceneID, String sessionID, String componentIDAssociated, Integer...typeId); 
	
	public List<String> getScenes(Long idExperiment);
	
	public void insertComponent(ComponentData component);
	
}
