package com.thames.finance_app.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.thames.finance_app.dtos.OperacionRequest;
import com.thames.finance_app.models.Operacion;
import com.thames.finance_app.models.Pago;
import com.thames.finance_app.repositories.PagoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PagoService {

	private final PagoRepository pagoRepository;


	public void agregarPagosOperacion(OperacionRequest request) {


	}

	public void guardarPago(Pago pago) {
		pagoRepository.save(pago);
	}

	public void vincularConOperacion (Pago pago, Operacion operacion) {
		pago.setOperacion(operacion);
		guardarPago(pago);
	}

	public void vincularConOperacion(Operacion operacion) {
    	List<Pago> pagosOrigen = operacion.getPagosOrigen();
    	List<Pago> pagosConversion = operacion.getPagosConversion();

    	for (Pago pago : pagosOrigen) {
    		pago.setOperacion(operacion);
    		guardarPago(pago);
    	}

    	for (Pago pago : pagosConversion) {
    		pago.setOperacion(operacion);
    		guardarPago(pago);

    	}
	}

	public void eliminarPago(Pago pago) {
		pagoRepository.delete(pago);
	}



}
