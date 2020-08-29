package com.uniovi.es.security;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class EntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request,
			HttpServletResponse response, AuthenticationException e)
			throws IOException, ServletException {

		logger.error("Acceso denegado: {}", e.getMessage());
		response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
				"Se ha producido un fallo en el acceso a la app.");
	}
	
	private static final Logger logger = LoggerFactory
			.getLogger(EntryPoint.class);

}
