package com.thames.finance_app.mappers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.thames.finance_app.dtos.OperacionRequest;
import com.thames.finance_app.dtos.OperacionResponse;
import com.thames.finance_app.dtos.PagoDTO;
import com.thames.finance_app.models.CuentaCorriente;
import com.thames.finance_app.models.Moneda;
import com.thames.finance_app.models.Operacion;
import com.thames.finance_app.repositories.MonedaRepository;
import com.thames.finance_app.services.ClienteService;
import com.thames.finance_app.services.ReferidoService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OperacionMapper {

	private final ClienteService clienteService;
	private final ReferidoService referidoService;
	private final MonedaRepository monedaRepository;
	private final PagoMapper pagoMapper;

    public Operacion toEntity(OperacionRequest request) {
    	CuentaCorriente cuentaCliente = clienteService
				.obtenerPorNombre(request
				.getNombreCliente()).getCuentaCorriente();
    	Moneda monedaOrigen = monedaRepository
    			.findByCodigo(request.getMonedaOrigen())
    			.orElseThrow(() -> new RuntimeException("Moneda no encontrada"));

    	Moneda monedaConversion = monedaRepository
    			.findByCodigo(request.getMonedaConversion())
    			.orElseThrow(() -> new RuntimeException("Moneda no encontrada"));

    	Operacion operacion = Operacion.builder()
                .fechaCreacion(LocalDateTime.now())
                .tipo(request.getTipo())
                .cuentaCorriente(cuentaCliente)
                .monedaOrigen(monedaOrigen)
                .montoOrigen(request.getMontoOrigen())
                .valorTipoCambio(request.getValorTipoCambio())
                .monedaConversion(monedaConversion)
                .montoConversion(request.getMontoOrigen().multiply(request.getValorTipoCambio()))
                .build();

    	if(request.getNombreReferido() !=null) {
        	CuentaCorriente cuentaReferido = Optional.ofNullable(referidoService
					.obtenerPorNombre(request
					.getNombreReferido())
					.getCuentaCorriente()).orElse(null);

        	Moneda monedaReferido = monedaRepository
        			.findByCodigo(request.getMonedaReferido())
        			.orElseThrow(() -> new RuntimeException("Moneda no encontrada"));

        	operacion.setCuentaCorrienteReferido(cuentaReferido);
        	operacion.setMonedaReferido(monedaReferido);
        	operacion.setPuntosReferido(request.getPuntosReferido());
        }

    	return operacion;
    }

    public OperacionResponse toResponse(Operacion operacion) {

    	String nombreCliente = operacion.getCuentaCorriente().getTitular().getNombre();
    	BigDecimal totalEjecutadoOrigen = operacion.getTotalPagosOrigen();
    	BigDecimal totalEjecutadoConversion = operacion.getTotalPagosConversion();

        OperacionResponse operacionResponse = OperacionResponse.builder()
                .id(operacion.getId())
                .fechaCreacion(operacion.getFechaCreacion())
                .fechaActualizacion(operacion.getFechaActualizacion())
                .nombreCliente(nombreCliente)
                .tipo(operacion.getTipo().toString())
                .monedaOrigen(operacion.getMonedaOrigen().getCodigo())
                .montoOrigen(operacion.getMontoOrigen())

                .monedaConversion(operacion.getMonedaConversion().getCodigo())
                .montoConversion(operacion.getMontoConversion())
                .valorTipoCambio(operacion.getValorTipoCambio())
//                .valorTipoCambio(tipoCambioService.obtenerTipoCambio(operacion.getMonedaOrigen(), operacion.getMonedaConversion(),
//						operacion.getTipo() == TipoOperacion.COMPRA ? true : false ))
                .pagosOrigen(pagoMapper.toPagoResponseList(operacion.getPagosOrigen()))
                .totalPagosOrigen(totalEjecutadoOrigen)

                .pagosConversion(pagoMapper.toPagoResponseList(operacion.getPagosConversion()))
                .totalPagosConversion(totalEjecutadoConversion)
                .estado(operacion.getEstado())
                .build();

        if (operacion.getCuentaCorrienteReferido() != null) {
        	String nombreReferido = operacion.getCuentaCorrienteReferido().getTitular().getNombre();
        	operacionResponse.setNombreReferido(nombreReferido);
        	operacionResponse.setPuntosReferido(operacion.getPuntosReferido());
        	operacionResponse.setMonedaReferido(operacion.getMonedaReferido().getCodigo());
        	operacionResponse.setGananciaReferido(operacion.getMontoOrigen());

        }
        return operacionResponse;
    }

    public OperacionRequest toRequest(Operacion operacion) {
        OperacionRequest request = new OperacionRequest();

        // Mapear los campos básicos
        request.setTipo(operacion.getTipo());
        request.setNombreCliente(operacion.getCuentaCorriente().getTitular().getNombre());
        request.setMonedaOrigen(operacion.getMonedaOrigen().getCodigo());
        request.setMontoOrigen(operacion.getMontoOrigen());
        request.setValorTipoCambio(operacion.getValorTipoCambio());
        request.setMonedaConversion(operacion.getMonedaConversion().getCodigo());

        // Mapear los pagos de origen y conversión
        if (operacion.getPagosOrigen() != null) {
            List<PagoDTO> pagosOrigenDTO = operacion.getPagosOrigen().stream()
                .map(pago -> pagoMapper.toDTO(pago)) // Asume que tienes un PagoMapper para convertir Pago a PagoDTO
                .collect(Collectors.toList());
            request.setPagosOrigen(pagosOrigenDTO);
        }

        if (operacion.getPagosConversion() != null) {
            List<PagoDTO> pagosConversionDTO = operacion.getPagosConversion().stream()
                .map(pago -> pagoMapper.toDTO(pago)) // Asume que tienes un PagoMapper para convertir Pago a PagoDTO
                .collect(Collectors.toList());
            request.setPagosConversion(pagosConversionDTO);
        }

        // Mapear los campos relacionados con el referido si existen
        if (operacion.getCuentaCorrienteReferido() != null) {
            request.setNombreReferido(operacion.getCuentaCorrienteReferido().getTitular().getNombre());
            request.setMonedaReferido(operacion.getMonedaReferido().getCodigo());
            request.setPuntosReferido(operacion.getPuntosReferido());
            request.setGananciaReferido(operacion.getMontoOrigen()); // Asume que la ganancia del referido es el monto de origen
        }

        return request;
    }


//    public Operacion updateEntity(Operacion operacion, OperacionRequest request) {
//    	CuentaCorriente cuentaCliente = clienteService
//    									.obtenerPorNombre(request
//    									.getNombreCliente()).getCuentaCorriente();
//
//
//    	Moneda monedaOrigen = monedaRepository
//    			.findByCodigo(request.getMonedaOrigen())
//    			.orElseThrow(() -> new RuntimeException("Moneda no encontrada"));
//
//    	Moneda monedaConversion = monedaRepository
//    			.findByCodigo(request.getMonedaConversion())
//    			.orElseThrow(() -> new RuntimeException("Moneda no encontrada"));
//
//    	operacion.setFechaActualizacion(LocalDateTime.now());
//        operacion.setTipo(request.getTipo());
//        operacion.setMonedaOrigen(monedaOrigen);
//        operacion.setMontoOrigen(request.getMontoOrigen());
//        operacion.setMonedaConversion(monedaConversion);
//        operacion.setMontoConversion(tipoCambioService.convertirMoneda(monedaOrigen, monedaConversion,
//																		request.getMontoOrigen(),
//																		request.getTipo() == TipoOperacion.COMPRA ? true : false ));
////        operacion.setValorTipoCambio(tipoCambioService.obtenerTipoCambio(monedaOrigen, monedaConversion,
////														request.getTipo() == TipoOperacion.COMPRA ? true : false ));
//        operacion.setValorTipoCambio(request.getValorTipoCambio());
//        operacion.setCuentaCorriente(cuentaCliente);
//
//        if(request.getNombreReferido() !=null) {
//
//        	CuentaCorriente cuentaReferido = Optional.ofNullable(referidoService
//					.obtenerPorNombre(request
//					.getNombreReferido())
//					.getCuentaCorriente()).orElse(null);
//
//        	Moneda monedaReferido = monedaRepository
//        			.findByCodigo(request.getMonedaReferido())
//        			.orElseThrow(() -> new RuntimeException("Moneda no encontrada"));
//
//            operacion.setCuentaCorrienteReferido(cuentaReferido);
//            operacion.setPuntosReferido(request.getPuntosReferido());
//            operacion.setMonedaReferido(monedaReferido);
////            operacion.setGananciaReferido(request.getPuntosReferido() != null ?
////            							request.getPuntosReferido()
////            							.multiply(request.getMontoOrigen()) : BigDecimal.ZERO);
//        }
//
//        // Actualizar listas de pagos
//        operacion.getPagosOrigen().clear();
//        operacion.getPagosOrigen().addAll(pagoMapper.toPagoList(request.getPagosOrigen(), TipoPago.ORIGEN));
//        operacion.getPagosConversion().clear();
//        operacion.getPagosConversion().addAll(pagoMapper.toPagoList(request.getPagosConversion(), TipoPago.CONVERSION));
//
//        return operacion;
//    }


}

