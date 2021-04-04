package com.uniovi.es.business.experimentData.strategy;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class StrategyDataAbstract implements StrategyData{
	
	public static final Logger logger = LoggerFactory.getLogger(StrategyDataAbstract.class);
	
	private Integer key;
	
	public StrategyDataAbstract(Integer key) {
		this.key = key;
	}
	
	@Override
	public Integer getKey() {
		return key;
	}
	
	/**
	 * Elimina un caracter en una posición de la cadena que se le pasa por parámetro
	 * @param str cadena de texto
	 * @param p posición del caracter a eliminar
	 * @return la nueva cadena sin el caracter eliminado
	 */
	public String charRemoveAt(String str, int position) {  
		if(position == str.length()-1)
			return str.substring(0, position);
		return str.substring(0, position) + str.substring(position + 1);  
    }  
	
	
	
}
