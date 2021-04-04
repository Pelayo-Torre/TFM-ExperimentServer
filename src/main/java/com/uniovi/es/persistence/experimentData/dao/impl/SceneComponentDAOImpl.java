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
	public List<ComponentData> getComponents(String sceneID, String sessionID, Integer typeId) {
		logger.info("[INICIO] - SceneComponentDAOImpl - getComponentsOfScene");
		logger.info("\t \t Parámetros de entrada: SceneID - " + sceneID + " SessionID - " + sessionID + " TypeID - " + typeId);
		
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
			if(typeId != null)
				sb.append(" AND type_id = ? ");
			
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
			if(typeId != null) {
				count++;
				stmt.setInt(count, typeId);
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
