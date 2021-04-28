package com.uniovi.es.business.experimentData.filter;

public interface FilterData {

	/**
	 * Devuelve el identifcador de cada Filtro concreto
	 * @return identifcador
	 */
	public Integer getKey();
	
	/**
	 * Retorna true en caso de que sea válido y false en caso contrario
	 * @param sceneID identifcador de la escena
	 * @param sessionID identificador de la sesión
	 * @return true / false en caso de ser o no correcto
	 */
	public boolean isValid(String sceneID, String sessionID);
	
	/**
	 * Retorna el nombre del filtro
	 * @return el nombre del filtro
	 */
	public String getName();
	
}
