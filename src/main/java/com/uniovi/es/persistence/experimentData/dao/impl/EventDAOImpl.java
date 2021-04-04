package com.uniovi.es.persistence.experimentData.dao.impl;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.uniovi.es.model.Event;
import com.uniovi.es.persistence.experimentData.ConnectionProvider;
import com.uniovi.es.persistence.experimentData.dao.EventDAO;

public class EventDAOImpl implements EventDAO{
	
	private static final Logger logger = LoggerFactory.getLogger(EventDAOImpl.class);
	
	@Override
	public Event getInitialEvent(String sceneID, String sessionID, String componentID, Integer eventType) {
		logger.info("[INICIO] - EventDAOImpl - getInitialEvent");
		logger.info("\t \t Parámetros de entrada: SceneID - " + sceneID + " SessionID - " 
		+ sessionID + " EventType - " + eventType + " ComponentID - " + componentID);
		
		Connection con;
		Event event = null;
		try {
			con = ConnectionProvider.getInstance().getConnection();
			
			StringBuilder sb = new StringBuilder();
			sb.append("SELECT * FROM EVENT WHERE 1 = 1 ");
			if(sceneID != null)
				sb.append(" AND scene_id = ? ");
			if(sessionID != null)
				sb.append(" AND user_session_id = ? ");
			if(eventType != null)
				sb.append(" AND event_type = ? ");
			if(componentID != null)
				sb.append(" AND element_id = ? ");
			sb.append(" AND time_stamp IN ");
			
			sb.append(" (SELECT MIN(time_stamp) FROM EVENT WHERE 1=1 ");
			if(sceneID != null)
				sb.append(" 	AND scene_id = ? ");
			if(sessionID != null)
				sb.append(" 	AND user_session_id = ? ");
			if(eventType != null)
				sb.append(" 	AND event_type = ? ");
			if(componentID != null)
				sb.append(" 	AND element_id = ? ");
			sb.append(" ) ");
			
			PreparedStatement stmt = con.prepareStatement(sb.toString());
			int count = 0;
			
			if(sceneID != null) {
				count++;
				stmt.setString(count, sceneID);
			}
			if(sessionID != null) {
				count++;
				stmt.setString(count, sessionID);
			}
			if(eventType != null) {
				count++;
				stmt.setInt(count, eventType);
			}
			if(componentID != null) {
				count++;
				stmt.setString(count, componentID);
			}
			
			if(sceneID != null) {
				count++;
				stmt.setString(count, sceneID);
			}
			if(sessionID != null) {
				count++;
				stmt.setString(count, sessionID);
			}
			if(eventType != null) {
				count++;
				stmt.setInt(count, eventType);
			}
			if(componentID != null) {
				count++;
				stmt.setString(count, componentID);
			}
			
			logger.info("\t \t Query: " + stmt);
			ResultSet result = stmt.executeQuery();
			
			while (result.next()) {
				event = getEvent(result);
			};
						
		} catch (ClassNotFoundException e) {
			logger.error("[ERROR] - ClassNotFoundException " + e.toString());
		} catch (SQLException e) {
			logger.error("[ERROR] - SQLException " + e.toString());
		} catch (IOException e) {
			logger.error("[ERROR] - IOException " + e.toString());
		}
		
		logger.info("[FINAL] - EventDAOImpl - getInitialEvent");
		return event;
	}
	
	@Override
	public Event getFinalEvent(String sceneID, String sessionID, String componentID, Integer eventType) {
		logger.info("[INICIO] - EventDAOImpl - getFinalEvent");
		logger.info("\t \t Parámetros de entrada: SceneID - " + sceneID + " SessionID - " 
		+ sessionID + " EventType - " + eventType + " ComponentID - " + componentID);
		
		Connection con;
		Event event = null;
		try {
			con = ConnectionProvider.getInstance().getConnection();
			
			StringBuilder sb = new StringBuilder();
			sb.append("SELECT * FROM EVENT WHERE 1 = 1 ");
			if(sceneID != null)
				sb.append(" AND scene_id = ? ");
			if(sessionID != null)
				sb.append(" AND user_session_id = ? ");
			if(eventType != null)
				sb.append(" AND event_type = ? ");
			if(componentID != null)
				sb.append(" AND element_id = ? ");
			sb.append(" AND time_stamp IN ");
			
			sb.append(" (SELECT MAX(time_stamp) FROM EVENT WHERE 1=1 ");
			if(sceneID != null)
				sb.append(" 	AND scene_id = ? ");
			if(sessionID != null)
				sb.append(" 	AND user_session_id = ? ");
			if(eventType != null)
				sb.append(" 	AND event_type = ? ");
			if(componentID != null)
				sb.append(" 	AND element_id = ? ");
			sb.append(" ) ");
			
			PreparedStatement stmt = con.prepareStatement(sb.toString());
			int count = 0;
			
			if(sceneID != null) {
				count++;
				stmt.setString(count, sceneID);
			}
			if(sessionID != null) {
				count++;
				stmt.setString(count, sessionID);
			}
			if(eventType != null) {
				count++;
				stmt.setInt(count, eventType);
			}
			if(componentID != null) {
				count++;
				stmt.setString(count, componentID);
			}
			
			if(sceneID != null) {
				count++;
				stmt.setString(count, sceneID);
			}
			if(sessionID != null) {
				count++;
				stmt.setString(count, sessionID);
			}
			if(eventType != null) {
				count++;
				stmt.setInt(count, eventType);
			}
			if(componentID != null) {
				count++;
				stmt.setString(count, componentID);
			}
			
			logger.info("\t \t Query: " + stmt);
			ResultSet result = stmt.executeQuery();
			
			while (result.next()) {
				event = getEvent(result);
			};
						
		} catch (ClassNotFoundException e) {
			logger.error("[ERROR] - ClassNotFoundException " + e.toString());
		} catch (SQLException e) {
			logger.error("[ERROR] - SQLException " + e.toString());
		} catch (IOException e) {
			logger.error("[ERROR] - IOException " + e.toString());
		}
		
		logger.info("[FINAL] - EventDAOImpl - getFinalEvent");
		return event;
	}
	
	@Override
	public List<Event> getEvents(String sceneID, String sessionID, String componentId, Long initial, Long last, Integer keyCodeEvent, Integer...types) {
		logger.info("[INICIO] - EventDAOImpl - getEventsInterval");
		logger.info("\t \t Parámetros de entrada: SceneID - " + sceneID + " SessionID - " + sessionID + 
				" EventTypes - " + types + " ComponentID - " + componentId + " initial - " + initial + " last - " + last
				+ " KeyCodeEvent - " + keyCodeEvent);
		
		Connection con;
		List<Event> events = new ArrayList<Event>();
		
		try {
			con = ConnectionProvider.getInstance().getConnection();
			
			int count = 0; 
			
			StringBuilder sb = new StringBuilder();
			sb.append("SELECT * FROM EVENT WHERE 1 = 1 ");
			if(sceneID != null) 
				sb.append(" AND scene_id = ? ");
			if(sessionID != null)
				sb.append(" AND user_session_id = ? ");
			if(componentId != null)
				sb.append(" AND element_id = ? ");
			if(initial != null)
				sb.append(" AND time_stamp >= ? ");
			if(last != null)
				sb.append(" AND time_stamp <= ? ");
			if(keyCodeEvent != null)
				sb.append(" AND key_code_event = ? ");
			if(types != null && types.length > 0) {
				sb.append(" AND event_type IN ( ");
				if(types.length == 1){
					sb.append(" ?) ");
				}
				else {
					for(int i=0; i<types.length; i++) {
						if(i == 0) {
							sb.append(" ? ");
						}
						else if( i == types.length -1) {
							sb.append(", ?) ");
						}
						else {
							sb.append(", ? ");
						}
					}
				}
			}
			sb.append(" ORDER BY time_stamp ASC ");
			
			PreparedStatement stmt = con.prepareStatement(sb.toString());
			
			if(sceneID != null) {
				count++;
				stmt.setString(count, sceneID);
			}
			if(sessionID != null) {
				count++;
				stmt.setString(count, sessionID);
			}
			if(componentId != null) {
				count++;
				stmt.setString(count, componentId);
			}
			if(initial != null) {
				count++;
				stmt.setLong(count, initial);
			}
			if(last != null) {
				count++;
				stmt.setLong(count, last);
			}
			if(keyCodeEvent != null) {
				count++;
				stmt.setInt(count, keyCodeEvent);
			}
			if(types != null && types.length > 0) {
				for(int i=0; i<types.length; i++) {
					count++;
					stmt.setInt(count, types[i]);
				}
			}
			
			logger.info("\t \t Query: " + stmt);
			ResultSet result = stmt.executeQuery();
			
			while (result.next()) {
				events.add(getEvent(result));
			};
						
		} catch (ClassNotFoundException e) {
			logger.error("[ERROR] - ClassNotFoundException " + e.toString());
		} catch (SQLException e) {
			logger.error("[ERROR] - SQLException " + e.toString());
		} catch (IOException e) {
			logger.error("[ERROR] - IOException " + e.toString());
		}
		
		logger.info("[FINAL] - EventDAOImpl - getEventsInterval");
		return events;
	}
	
	private Event getEvent(ResultSet result) throws SQLException {
		Event event = new Event();
		event.setSceneId(result.getString("scene_id"));
		event.setEventType(result.getInt("event_type"));
		event.setElementId(result.getString("element_id"));
		event.setTimeStamp(result.getLong("time_stamp"));
		event.setX(result.getInt("x"));
		event.setY(result.getInt("y"));
		event.setKeyCodeEvent(result.getInt("key_code_event"));
		event.setKeyValueEvent(result.getString("key_value_event"));
		return event;
	}

}
