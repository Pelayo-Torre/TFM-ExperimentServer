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

import com.uniovi.es.model.ComponentData;
import com.uniovi.es.persistence.experimentData.ConnectionProvider;
import com.uniovi.es.persistence.experimentData.dao.SceneComponentDAO;

public class SceneComponentDAOImpl implements SceneComponentDAO{
	
	private static final Logger logger = LoggerFactory.getLogger(SceneComponentDAOImpl.class);

	@Override
	public List<ComponentData> getComponents(String sceneID, String sessionID, String componentIDAssociated, Integer...typeId) {
		logger.info("[INICIO] - SceneComponentDAOImpl - getComponentsOfScene");
		logger.info("\t \t Parámetros de entrada: SceneID - " + sceneID + " SessionID - " + sessionID + " TypeID - " + typeId + 
				" componentIDAssociated - " + componentIDAssociated);
		
		Connection con;
		List<ComponentData> components = new ArrayList<ComponentData>();
		try {
			con = ConnectionProvider.getInstance().getConnection();
			
			StringBuilder sb = new StringBuilder();
			sb.append("SELECT * FROM SCENE_COMPONENT WHERE 1=1 ");
			if(sceneID != null)
				sb.append(" AND scene_id = ? ");
			if(sessionID != null)
				sb.append(" AND user_session_id = ? ");
			if(componentIDAssociated != null)
				sb.append(" AND component_associated = ? ");
			if(typeId != null && typeId.length > 0) {
				sb.append(" AND type_id IN ( ");
				if(typeId.length == 1){
					sb.append(" ?) ");
				}
				else {
					for(int i=0; i<typeId.length; i++) {
						if(i == 0) {
							sb.append(" ? ");
						}
						else if( i == typeId.length -1) {
							sb.append(", ?) ");
						}
						else {
							sb.append(", ? ");
						}
					}
				}
			}
			
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
			if(componentIDAssociated != null) {
				count++;
				stmt.setString(count, componentIDAssociated);
			}
			if(typeId != null && typeId.length > 0) {
				for(int i=0; i<typeId.length; i++) {
					count++;
					stmt.setInt(count, typeId[i]);
				}
			}
			
			logger.info("\t \t Query: " + stmt);
			ResultSet result = stmt.executeQuery();
			
			while (result.next()) {
				components.add(getComponent(result));
			};
						
		} catch (ClassNotFoundException e) {
			logger.error("[ERROR] - ClassNotFoundException " + e.toString());
		} catch (SQLException e) {
			logger.error("[ERROR] - SQLException " + e.toString());
		} catch (IOException e) {
			logger.error("[ERROR] - IOException " + e.toString());
		}
		
		logger.info("[FINAL] - SceneComponentDAOImpl - getComponentsOfScene");
		return components;
	}
	
	@Override
	public List<String> getScenes(Long idExperiment) {
		logger.info("[INICIO] - SceneComponentDAOImpl - getScenes");
		logger.info("\t \t Parámetros de entrada: ID experiment - " + idExperiment);
		
		Connection con;
		List<String> scenes = new ArrayList<String>();
		try {
			con = ConnectionProvider.getInstance().getConnection();
			
			StringBuilder sb = new StringBuilder();
			sb.append("SELECT DISTINCT e.scene_id FROM userdata u, event e WHERE u.session_id = e.user_session_id and u.experiment_id = ? ");
			
			PreparedStatement stmt = con.prepareStatement(sb.toString());
			stmt.setLong(1, idExperiment);
			
			logger.info("\t \t Query: " + stmt);
			ResultSet result = stmt.executeQuery();
			
			while (result.next()) {
				scenes.add(result.getString("scene_id"));
			};
						
		} catch (ClassNotFoundException e) {
			logger.error("[ERROR] - ClassNotFoundException " + e.toString());
		} catch (SQLException e) {
			logger.error("[ERROR] - SQLException " + e.toString());
		} catch (IOException e) {
			logger.error("[ERROR] - IOException " + e.toString());
		}
		
		logger.info("[FINAL] - SceneComponentDAOImpl - getScenes");
		return scenes;
	}

	@Override
	public void insertComponent(ComponentData component) {
		logger.debug("[INICIO] - SceneComponentDAOImpl - insertComponent");
		
		Connection con;
		try {
			con = ConnectionProvider.getInstance().getConnection();
			
			PreparedStatement stmt = con.prepareStatement(
					"insert into scene_component (user_session_Id, scene_Id, time_Stamp, x, y, xf,yf,component_Id,type_id,component_associated) values(?,?,?,?,?,?,?,?,?,?)");

			stmt.setString(1, component.getUser().getSessionId());
			stmt.setString(2, component.getSceneId());
			stmt.setLong(3, component.getTimeStamp());
			stmt.setInt(4, component.getX());
			stmt.setInt(5, component.getY());
			stmt.setInt(6, component.getxF());
			stmt.setInt(7, component.getyF());
			stmt.setString(8, component.getComponentId());
			if(component.getTypeId() != null) {
				stmt.setInt(9, component.getTypeId());
			}
			else {
				stmt.setObject(9, component.getTypeId());
			}
			stmt.setString(10, component.getComponentAssociated());
				
			logger.debug("\tInserting " + component.getUser().getSessionId() + ": " + component);
			
			stmt.executeUpdate();
			con.close();
			
		} catch (ClassNotFoundException e) {
			logger.error("[ERROR] - ClassNotFoundException " + e.toString());
		} catch (SQLException e) {
			logger.error("[ERROR] - SQLException " + e.toString());
		} catch (IOException e) {
			logger.error("[ERROR] - IOException " + e.toString());
		}
		logger.debug("[FINAL] - SceneComponentDAOImpl - insertComponent");
	}
	
	private ComponentData getComponent(ResultSet result) throws SQLException {
		ComponentData component = new ComponentData();
		
		component.setSceneId(result.getString("scene_id"));
		component.setComponentId(result.getString("component_id"));
		component.setTimeStamp(result.getLong("time_stamp"));
		component.setX(result.getInt("x"));
		component.setY(result.getInt("y"));
		component.setxF(result.getInt("xf"));
		component.setyF(result.getInt("yf"));
		component.setComponentAssociated(result.getString("component_associated"));
		component.setTypeId(result.getInt("type_id"));
		
		return component;
	}

}
