package com.uniovi.es.presentation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class InitController {

	@GetMapping
	public String init() {
		return "INICIO DE APLICACIÓN";
	}
	
}
