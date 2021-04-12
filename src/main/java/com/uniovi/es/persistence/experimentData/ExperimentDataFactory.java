package com.uniovi.es.persistence.experimentData;

import com.uniovi.es.persistence.experimentData.dao.EventDAO;
import com.uniovi.es.persistence.experimentData.dao.SceneComponentDAO;
import com.uniovi.es.persistence.experimentData.dao.impl.EventDAOImpl;
import com.uniovi.es.persistence.experimentData.dao.impl.SceneComponentDAOImpl;

public class ExperimentDataFactory {
	
	public static EventDAO getEventDAO() {
		return new EventDAOImpl();
	}
	
	public static SceneComponentDAO getSceneComponentDAO() {
		return new SceneComponentDAOImpl();
	}
	

}
