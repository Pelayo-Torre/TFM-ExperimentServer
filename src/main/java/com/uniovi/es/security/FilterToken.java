package com.uniovi.es.security;


import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.uniovi.es.business.authentication.UserDetailsServiceImpl;


public class FilterToken extends OncePerRequestFilter {

	@Autowired
	private UserDetailsServiceImpl detailsService;

	@Autowired
	private TokenGenerator generatorToken;

	@Override
	protected void doFilterInternal(HttpServletRequest request,
			HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		try {

			String token = getToken(request);
			if (token != null && generatorToken.validateToken(token)) {
				String login = generatorToken.getUserByToken(token);
				UserDetails userDetails = detailsService
						.loadUserByUsername(login);
				UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());

				auth.setDetails(new WebAuthenticationDetailsSource()
						.buildDetails(request));

				SecurityContextHolder.getContext().setAuthentication(auth);
			}
		} catch (Exception exception) {
			logger.error("Error al validar al usuario: {}", exception);
		}

		filterChain.doFilter(request, response);
	}

	private String getToken(HttpServletRequest r) {
		String auth = r.getHeader("Authorization");

		if (auth != null && auth.startsWith("Bearer ")) {
			String replace = auth.replace("Bearer ", "");
			return replace;
		}

		return null;
	}

	private static final Logger logger = LoggerFactory
			.getLogger(FilterToken.class);

}
