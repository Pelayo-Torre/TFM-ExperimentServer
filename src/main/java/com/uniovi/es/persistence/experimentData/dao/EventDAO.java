package com.uniovi.es.persistence.experimentData.dao;

import java.util.List;

import com.uniovi.es.model.Event;

public interface EventDAO {
		
	public Event getFinalEvent(String sceneID, String sessionID, String componentID, Integer eventType, Integer keyCodeEvent);
		
	public Event getInitialEvent(String sceneID, String sessionID, String componentID, Integer eventType, Integer keyCodeEvent);
	
	public Event getInitialEventOfUser(String sceneID, String sessionID);
	
	public List<Event> getEvents(String sceneID, String sessionID, String componentId, Long initial, Long last, Integer keyCodeEvent, Integer...types);
	
	public Event getInitialEventOfSelectionObject(String sceneID, String sessionID, String associatedComponent, Integer eventType, Integer keyCodeEvent);

	public List<Event> getEventOfSelectionObject(String sceneID, String sessionID, String associatedComponent, Integer eventType, Integer keyCodeEvent);

	public void insertEvent(Event event);
}
