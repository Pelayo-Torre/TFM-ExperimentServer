package com.uniovi.es.business.user.ip;

import com.uniovi.es.model.User;

public interface IpCalculator {
	
	/**
	 * Calcula los datos del usuario a partir de su IP
	 * @param user el usuario
	 * @return true si la llamada al WS fue correcta o false en caso contrario
	 */
	public boolean calculate(User user);

}
