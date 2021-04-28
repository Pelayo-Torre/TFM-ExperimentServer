package com.uniovi.es.business.experimentData.filter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PropertiesFilterManager {
	
private static final Logger logger = LoggerFactory.getLogger(PropertiesFilterManager.class);
	
	private static final String FILE_NAME_FILTERS = "filters-names.properties";
	
	private static PropertiesFilterManager instance = null;
	
	private Properties namesFilters;
	
	private PropertiesFilterManager() {}
	
	public static PropertiesFilterManager getInstance() {
		if(instance == null) {
			instance = new PropertiesFilterManager();
		}
		return instance;
	}
	
	public Properties getNameStrategysProperties()  {
		if(namesFilters == null) {
			namesFilters = init(FILE_NAME_FILTERS);
		}
		return namesFilters;
	}
	
	private Properties init(String file) {
		final InputStream stream =
		           this.getClass().getClassLoader().getResourceAsStream(file);
		Properties p = new Properties();
		
		try {
			if (stream != null) {
				p.load(stream);
			} else {
				throw new FileNotFoundException("property file " + file + " not found in the classpath");
			}
		} catch (IOException e) {
			logger.error("[ERROR] NO SE HA ENCONTRADO EL FICHERO DE PROPIEDADES: " + file);
		}
		
		logger.info("Read the connection properties: " + p);
		
		return p;
	}

}
