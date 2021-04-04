package com.uniovi.es.persistence.experimentData.dao.impl;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.uniovi.es.persistence.experimentData.ConnectionProvider;
import com.uniovi.es.persistence.experimentData.dao.UserDAO;

public class UserDAOImpl implements UserDAO{
	
	private static final Logger logger = LoggerFactory.getLogger(UserDAOImpl.class);

	@Override
	public Long getExperimentOfUser(String sessionID) {
		logger.info("[INICIO] - EventDAOImpl - getExperimentOfUser");
		logger.info("\t \t Parámetros de entrada: SessionID - " + sessionID);
		
		Connection con;
		Long idExperiment = null;
		
		try {
			con = ConnectionProvider.getInstance().getConnection();
			
			StringBuilder sb = new StringBuilder();
			sb.append("SELECT * FROM USERDATA WHERE ");
			sb.append(" session_id = ? ");
			
			PreparedStatement stmt = con.prepareStatement(sb.toString());
			
			stmt.setString(1, sessionID);
			
			logger.info("\t \t Query: " + stmt);
			ResultSet result = stmt.executeQuery();
			
			while (result.next()) {
				idExperiment = result.getLong("experiment_id");
			};
						
		} catch (ClassNotFoundException e) {
			logger.error("[ERROR] - ClassNotFoundException " + e.toString());
		} catch (SQLException e) {
			logger.error("[ERROR] - SQLException " + e.toString());
		} catch (IOException e) {
			logger.error("[ERROR] - IOException " + e.toString());
		}
		
		logger.info("[FINAL] - EventDAOImpl - getExperimentOfUser");
		return idExperiment;
	}

}
