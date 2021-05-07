package com.uniovi.es.business.user.ip.calculators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.uniovi.es.business.dto.ip.IpApiDTO;
import com.uniovi.es.business.user.ip.IpCalculator;
import com.uniovi.es.model.User;
import com.uniovi.es.persistence.UserDAO;

/**
 * Servicio Web de cálculo de datos a partir de una IP: 
 * URL: https://ip-api.com/#93.156.100.199
 * @author pelay
 *
 */
@Component
public class IpApi implements IpCalculator{
	
	private static final Logger logger = LoggerFactory.getLogger(IpApi.class);
	
	@Value("${ip.api}")
	private String url;
	
	@Autowired
	private UserDAO userDAO;

	@Override
	public boolean calculate(User user) {
		
		try {
			String uri = url + user.getRemoteAddress();
			RestTemplate restTemplate = new RestTemplate();
		    IpApiDTO result = restTemplate.getForObject(uri, IpApiDTO.class);
		    
		    if(result == null) {
		    	logger.info("\t \t La respuesta del WS genera un bean nulo");
		    	return false;
		    }
		    
		    user.setCity(result.getCity());
		    user.setContinent(result.getContinent());
		    user.setCountry(result.getCountry());
		    user.setRegionname(result.getRegionName());
		    user.setProcessed(true);
		    
		    //Actualizamos los datos del usuario
		    userDAO.save(user);
		    
		    return true;
			
		} catch (Exception e) {
			logger.error("[ERROR] - IpApi - " + e.toString());
			return false;
		}
	}

}
