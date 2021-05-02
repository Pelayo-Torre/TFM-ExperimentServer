package com.uniovi.es.business.user;

import java.util.List;

import org.springframework.stereotype.Service;

import com.uniovi.es.model.User;

@Service
public interface UserDataService {

	/**
	 * Realiza el cáclulo de datos a partir de la IP del usuario
	 * @param users lista de los usuarios a ser calculados
	 */
	public void calculateIp(List<User> users);
	
}
