package com.thames.finance_app.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.thames.finance_app.dtos.OperacionRequest;
import com.thames.finance_app.dtos.OperacionResponse;
import com.thames.finance_app.enums.TipoPago;
import com.thames.finance_app.mappers.OperacionMapper;
import com.thames.finance_app.mappers.PagoMapper;
import com.thames.finance_app.models.Operacion;
import com.thames.finance_app.models.Pago;
import com.thames.finance_app.repositories.OperacionRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OperacionService {

    private final OperacionRepository operacionRepository;
    private final CajaService cajaService;
    private final CtaCteService ctaCteService;
    private final PagoService pagoService;
    private final OperacionMapper operacionMapper;
    private final PagoMapper pagoMapper;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @Transactional
    public OperacionResponse crearOperacion(OperacionRequest request) {
        Operacion operacion = operacionMapper.toEntity(request);
        operacion = operacionRepository.save(operacion);

        List<Pago> pagosOrigen = pagoMapper.toPagoList(request.getPagosOrigen(), TipoPago.ORIGEN);
        List<Pago> pagosConversion = pagoMapper.toPagoList(request.getPagosConversion(), TipoPago.CONVERSION);

        operacion.setPagosOrigen(pagosOrigen);
        operacion.setPagosConversion(pagosConversion);
        pagoService.vincularConOperacion(operacion);

        operacion = operacionRepository.save(operacion);

        cajaService.impactoOperacion(operacion);
        ctaCteService.impactoOperacion(operacion);

        if(operacion.getCuentaCorrienteReferido()!=null) {
            ctaCteService.impactoOperacionReferido(operacion);
        }
        return operacionMapper.toResponse(operacion);
    }
    
    @Transactional
    public OperacionResponse actualizarOperacion(Long id, OperacionRequest nueva) {
        Operacion vieja = operacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Operación no encontrada"));
        cajaService.revertirImpactoOperacion(vieja);
        ctaCteService.revertirImpactoOperacion(vieja);
        ctaCteService.revertirImpactoOperacionReferido(vieja);
        return crearOperacion(nueva);
    }

    public OperacionResponse obtenerResponsePorId(Long id) {
        Optional<Operacion> operacion = operacionRepository.findById(id);
        return operacion.map(operacionMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Operación no encontrada"));
    }

    public Operacion obtenerPorId(Long id) {
       Operacion operacion = operacionRepository.findById(id)
        		.orElseThrow(() -> new RuntimeException("Operación no encontrada"));
        return operacion;
    }

    public void eliminarOperacion(Long id) {
    	Operacion operacion = operacionRepository.getReferenceById(id);
    	cajaService.revertirImpactoOperacion(operacion);
    	ctaCteService.revertirImpactoOperacion(operacion);
    	ctaCteService.revertirImpactoOperacionReferido(operacion);
        operacionRepository.deleteById(id);
    }

    public void eliminarOperacionSinRevertir(Long id) {
    	operacionRepository.deleteById(id);
    }

    public Page<OperacionResponse> listarOperaciones(Specification<Operacion> spec, Pageable pageable) {
        return operacionRepository.findAll(spec, pageable).map(operacionMapper::toResponse);
    }



    public LocalDateTime parsearFecha(String fecha) {
        if (fecha == null) {
            return null;
        }

        try {
            // Verificar si la fecha está en formato yyyy-MM-dd
            if (fecha.matches("\\d{4}-\\d{2}-\\d{2}")) {
                // Convertir de yyyy-MM-dd a dd-MM-yyyy
                String[] partes = fecha.split("-");
                fecha = partes[2] + "-" + partes[1] + "-" + partes[0]; // Reordenar a dd-MM-yyyy
            }

            // Parsear la fecha en el formato esperado (dd-MM-yyyy)
            LocalDate localDate = LocalDate.parse(fecha, FORMATTER);
            return LocalDateTime.of(localDate, LocalTime.MIN);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Formato de fecha incorrecto. Usa dd-MM-yyyy o yyyy-MM-dd");
        }
    }

    public boolean existePorCuentaCorriente(Long id) {
    	return operacionRepository.existsByCuentaCorrienteId(id);
    }


}


