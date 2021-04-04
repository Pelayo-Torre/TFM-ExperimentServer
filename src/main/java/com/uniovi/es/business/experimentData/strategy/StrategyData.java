package com.uniovi.es.business.experimentData.strategy;

public interface StrategyData {

	/**
	 * Devuelve el resultado de la ejecución de la estrategia concreta que la implemente
	 * @param sceneID identifcador de la escena
	 * @param sessionID identificador de la sesión
	 * @return el resultado obtenido una vez ejecutada la escena
	 */
	public Object calculate(String sceneID, String sessionID);
	
	/**
	 * Devuelve el identifcador de cada Strategia concreta
	 * @return identifcador
	 */
	public Integer getKey();
	
	/**
	 * Devuelve la clave del fichero properties "strategys-names.properties"
	 * que guarda el nombre de la estrategia
	 * @return clave del fichero properties de nombres
	 */
	public String getPropertyName();
	
	/**
	 * Devuelve la clave del fichero properties "strategys-abbreviations.properties" 
	 * que guarda la abreviatura del nombre de la estrategia
	 * @return clave del fichero properties de abreviaturas
	 */
	public String getPropertyAbbreviation();
	
}
