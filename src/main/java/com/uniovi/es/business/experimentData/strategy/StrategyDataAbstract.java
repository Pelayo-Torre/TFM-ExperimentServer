package com.uniovi.es.business.experimentData.strategy;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase abstracata que implementa la interfaz StrategyData
 * Su principal propósito es el de no repetir código.
 * @author pelayo
 *
 */
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
		if(position == str.length())
			return str.substring(0, position-1);
		return str.substring(0, position) + str.substring(position + 1);  
    }  
	
	/**
	 * Inserta un caracter en una posición especificada 
	 * @param character caracter que va a ser insertado
	 * @param str cadena donde se insertará el caracter
	 * @param position Posición de la cadena donde se insertará
	 * @return La cadena con la inserción realizada
	 */
	public String charInsertAt(String character, String cadena, int position) {
		if(position == cadena.length()-1) {
			return cadena + character;
		}
		else if(position == 0) {
			return character + cadena;
		}
		else {
			return cadena.substring(0, position) + character + cadena.substring(position);
		}
	}
	
	/**
	 * Calcula la distancia entre dos puntos (coordenadas) x,y
	 * @param x1 coordenada X del primer punto
	 * @param y1 coordenada Y del primer punto
	 * @param x2 coordenada X del segundo punto
	 * @param y2 coordenada Y del primer punto
	 * @return la distancia entre ambos puntos
	 */
	public Double distance(Integer x1, Integer y1, Integer x2, Integer y2) {
		return Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
	}
}
