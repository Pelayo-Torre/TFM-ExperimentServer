package com.uniovi.es.persistence.experimentData;

import com.uniovi.es.persistence.experimentData.dao.EventDAO;
import com.uniovi.es.persistence.experimentData.dao.SceneComponentDAO;
import com.uniovi.es.persistence.experimentData.dao.UserDAO;
import com.uniovi.es.persistence.experimentData.dao.impl.EventDAOImpl;
import com.uniovi.es.persistence.experimentData.dao.impl.SceneComponentDAOImpl;
import com.uniovi.es.persistence.experimentData.dao.impl.UserDAOImpl;

public class ExperimentDataFactory {
	
	public static EventDAO getEventDAO() {
		return new EventDAOImpl();
	}
	
	public static SceneComponentDAO getSceneComponentDAO() {
		return new SceneComponentDAOImpl();
	}
	
	public static UserDAO getUserDAO() {
		return new UserDAOImpl();
	}

}
