package com.uniovi.es.business.experimentData.strategy;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropetiesStrategyManager {
	
	private static final Logger logger = LoggerFactory.getLogger(PropetiesStrategyManager.class);
	
	private static final String FILE_NAME_STRATEGYS = "strategys-names.properties";
	private static final String FILE_DESCRIPTION_STRATEGYS = "strategys-descriptions.properties";
	private static final String FILE_ABBREVIATIONS_STRATEGYS = "strategys-abbreviation.properties";
	
	private static PropetiesStrategyManager instance = null;
	
	private Properties namesStrategys;
	
	private Properties descriptionsStrategys;
	
	private Properties abbreviationStrategys;
	
	private PropetiesStrategyManager() {}
	
	public static PropetiesStrategyManager getInstance() {
		if(instance == null) {
			instance = new PropetiesStrategyManager();
		}
		return instance;
	}
	
	public Properties getNameStrategysProperties()  {
		if(namesStrategys == null) {
			namesStrategys = init(FILE_NAME_STRATEGYS);
		}
		return namesStrategys;
	}

	public Properties getDescriptionStrategysProperties() {
		if(descriptionsStrategys == null) {
			descriptionsStrategys = init(FILE_DESCRIPTION_STRATEGYS);
		}
		return descriptionsStrategys;
	}
	
	public Properties getAbbreviationStrategysProperties() {
		if(abbreviationStrategys == null) {
			abbreviationStrategys = init(FILE_ABBREVIATIONS_STRATEGYS);
		}
		return abbreviationStrategys;
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
