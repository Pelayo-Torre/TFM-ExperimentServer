package com.uniovi.es.persistence.experimentData.dao;

import java.util.List;

import com.uniovi.es.model.Event;

public interface EventDAO {
	
	//public Event getInitialEventOfScene(String sceneID, String sessionID);
	
	public Event getFinalEvent(String sceneID, String sessionID, String componentID, Integer eventType);
	
	//public Event getInitialEventOfScene(String sceneID, String sessionID, Integer eventType);
	
	public Event getInitialEvent(String sceneID, String sessionID, String componentID, Integer eventType);
	
	//public Event getFinalEventOfScene(String sceneID, String sessionID, Integer eventType);
	
	//public List<Event> getEventsOfScene(String sceneID, String sessionID);
	
	//public List<Event> getEventsOfScene(String sceneID, String sessionID, Integer eventType);
	
	//public List<Event> getEventOfSceneInConponents(String sceneID, String sessionID, String componentID);
		
	public List<Event> getEvents(String sceneID, String sessionID, String componentId, Long initial, Long last, Integer keyCodeEvent, Integer...types);
	
	//public List<Event> getEventsByTypes(String sceneID, String sessionID, String componentID, Integer...types);

}
