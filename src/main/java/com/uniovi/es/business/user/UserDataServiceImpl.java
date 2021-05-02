package com.uniovi.es.business.user;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.uniovi.es.business.user.ip.IpCalculatorManager;
import com.uniovi.es.model.User;

public class UserDataServiceImpl implements UserDataService{
	
	private static final Logger logger = LoggerFactory.getLogger(UserDataServiceImpl.class);

	@Override
	public void calculateIp(List<User> users) {
		logger.info("[INICIO] USER DATA SERVICE -- calculateIp");
		
		//Se empiezan calculando los datos de los distintos servicios web. Si uno falla, se pasa al siguiente.
		users.forEach((user) -> {
			IpCalculatorManager.getInstance().getIpCalculators().stream()
				.anyMatch(ip -> ip.calculate(user) == true);
		});
				
		logger.info("[FINAL] USER DATA SERVICE -- calculateIp");
	}

}
