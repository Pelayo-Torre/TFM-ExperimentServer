package com.uniovi.es.persistence.experimentData;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ConnectionProvider {

	private static ConnectionProvider instance = null;
	
	private String user;
	
	private String password;
	
	private String driver;
	
	private String connectionString;
	
	private String file = "application.properties";
	
	private static final Logger logger = LoggerFactory.getLogger(ConnectionProvider.class);


	public static ConnectionProvider getInstance() throws IOException {
		if (instance == null) {
			instance = new ConnectionProvider();
			instance.init();
		}
		return instance;
	}

	private void init() throws IOException {
		final InputStream stream =
		           this.getClass().getClassLoader().getResourceAsStream(file);
		Properties p = new Properties();
		if (stream != null) {
			p.load(stream);
		} else {
			throw new FileNotFoundException("property file db.properties not found in the classpath");
		}
		
		connectionString = p.getProperty("spring.datasource.url");
		user = p.getProperty("spring.datasource.username");
		password = p.getProperty("spring.datasource.password");
		driver = p.getProperty("spring.datasource.driverClassName");

		logger.info("Read the connection string: " + connectionString);
	}
	
	public synchronized Connection getConnection() throws ClassNotFoundException, SQLException
	{
		Class.forName(driver);
		Connection con = DriverManager.getConnection(connectionString, user, password);
		return con;
	}
	
	/**
	 * Recibe un fichero donde se encuentra la configuración de base de datos
	 * @param file fichero
	 * @throws IOException 
	 */
	public void changeConnection(String file) throws IOException {
		this.file = file;
		init();
	}
}
