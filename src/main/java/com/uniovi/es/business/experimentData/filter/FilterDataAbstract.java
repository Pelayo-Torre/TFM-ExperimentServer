package com.uniovi.es.business.experimentData.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Clase abstracata que implementa la interfaz FilterData
 * Su principal propósito es el de no repetir código.
 * @author pelayo
 *
 */
public abstract class FilterDataAbstract implements FilterData{
	
	public static final Logger logger = LoggerFactory.getLogger(FilterDataAbstract.class);
	
	private Integer key;
	
	public FilterDataAbstract(Integer key) {
		this.key = key;
	}
	
	@Override
	public Integer getKey() {
		return key;
	}

}
