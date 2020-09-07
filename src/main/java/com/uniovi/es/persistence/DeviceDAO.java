package com.uniovi.es.persistence;

import org.springframework.data.repository.CrudRepository;

import com.uniovi.es.model.types.Device;

public interface DeviceDAO extends CrudRepository <Device, Long> {

	
	
}
