package com.uniovi.es.business.administration;

import java.util.List;

import com.uniovi.es.business.dto.RequestDTO;
import com.uniovi.es.exceptions.AdministrationException;
import com.uniovi.es.exceptions.ForbiddenException;
import com.uniovi.es.utils.Identifier;

public interface AdministrationService {
	
	public void register(RequestDTO dto) throws AdministrationException, ForbiddenException;
	
	public void accept(Identifier id) throws AdministrationException, ForbiddenException;
	
	public void reject(Identifier id) throws AdministrationException, ForbiddenException;
	
	public List<RequestDTO> getPendingRequests() throws AdministrationException, ForbiddenException;
	
	public RequestDTO getDetail(Long id) throws AdministrationException;

}
