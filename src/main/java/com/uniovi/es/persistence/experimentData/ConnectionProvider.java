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
import org.springframework.beans.factory.annotation.Value;


public class ConnectionProvider {

	private static ConnectionProvider instance = null;
	
	@Value("${spring.datasource.username}")
	private String user;
	
	@Value("${spring.datasource.password}")
	private String password;
	
	@Value("${spring.datasource.driverClassName}")
	private String driver;
	
	@Value("${spring.datasource.url}")
	private String connectionString;
	
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
		           this.getClass().getClassLoader().getResourceAsStream("application.properties");
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
}
