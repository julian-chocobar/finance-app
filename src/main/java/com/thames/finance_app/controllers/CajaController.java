package com.thames.finance_app.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thames.finance_app.services.CajaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/cajas")
@RequiredArgsConstructor
public class CajaController {

	private final CajaService cajaService;
	

	
	
}
