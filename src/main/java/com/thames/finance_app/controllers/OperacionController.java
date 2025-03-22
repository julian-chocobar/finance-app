package com.thames.finance_app.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import com.thames.finance_app.dtos.OperacionRequest;
import com.thames.finance_app.dtos.OperacionResponse;
import com.thames.finance_app.dtos.PagoDTO;
import com.thames.finance_app.enums.TipoOperacion;
import com.thames.finance_app.models.Moneda;
import com.thames.finance_app.models.Operacion;
import com.thames.finance_app.services.MonedaService;
import com.thames.finance_app.services.OperacionService;
import com.thames.finance_app.specifications.OperacionSpecification;

import lombok.RequiredArgsConstructor;

import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;


@Controller
@RequestMapping("/operaciones")
@RequiredArgsConstructor
public class OperacionController {

    private final OperacionService operacionService;
    private final MonedaService monedaService;
    
    @GetMapping
    public String listarOperaciones(
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin,
            @RequestParam(required = false) String montoOrigen,
            @RequestParam(required = false) String monedaOrigen,
            @RequestParam(required = false) String montoConversion,
            @RequestParam(required = false) String monedaConversion,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String clienteNombre,
            Pageable pageable,
            Model model) {

        // Parseo de parámetros
        LocalDateTime fechaInicioParsed = operacionService.parsearFecha(fechaInicio);
        LocalDateTime fechaFinParsed = operacionService.parsearFecha(fechaFin);
        BigDecimal montoOrigenParsed = (montoOrigen != null) ? new BigDecimal(montoOrigen) : null;
        BigDecimal montoConversionParsed = (montoConversion != null) ? new BigDecimal(montoConversion) : null;

        TipoOperacion tipoParsed = (tipo != null) ? TipoOperacion.valueOf(tipo) : null;

        // Buscar la moneda si está presente
        Moneda monedaOrigenParsed = null;
        if (monedaOrigen != null && !monedaOrigen.isEmpty()) {
        	monedaOrigenParsed = monedaService.buscarPorCodigo(monedaOrigen);
        }
        
        Moneda monedaConversionParsed = null;
        if (monedaConversion != null && !monedaConversion.isEmpty()) {
        	monedaConversionParsed = monedaService.buscarPorCodigo(monedaConversion);
        }

        // Crear la Specification usando OperacionSpecification
        Specification<Operacion> spec = OperacionSpecification.filtrarPorParametros(
                fechaInicioParsed,
                fechaFinParsed,
                montoOrigenParsed,
                monedaOrigenParsed,
                montoConversionParsed,
                monedaConversionParsed,
                tipoParsed,
                clienteNombre
        );

        // Llamar al servicio con la Specification y el Pageable
        Page<OperacionResponse> operaciones = operacionService.listarOperaciones(spec, pageable);

        // Agregar los resultados al modelo
        model.addAttribute("operaciones", operaciones);
        model.addAttribute("fechaInicio", fechaInicio);
        model.addAttribute("fechaFin", fechaFin);
        model.addAttribute("montoOrigen", montoOrigen);
        model.addAttribute("monedaOrigen", monedaOrigen);       
        model.addAttribute("montoConversion", montoConversion);
        model.addAttribute("monedaConversion", monedaConversion);        
        model.addAttribute("tipo", tipo);
        model.addAttribute("clienteNombre", clienteNombre);

        // Devolver el nombre de la vista
        return "operaciones/lista";
    }
   
  
    @PostMapping
    public String crearOperacion(@ModelAttribute OperacionRequest request) {
        operacionService.crearOperacion(request);
        return "redirect:/operaciones/crear";
    }

    @GetMapping("/{id}")
    public ResponseEntity<OperacionResponse> obtenerOperacion(@PathVariable Long id) {
        OperacionResponse response = operacionService.obtenerOperacion(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/editar/{id}")
    public String actualizarOperacion(@PathVariable Long id, @ModelAttribute OperacionRequest request) {
        operacionService.actualizarOperacion(id, request);
        return "redirect:/operaciones/editar";
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarOperacion(@PathVariable Long id) {
        operacionService.eliminarOperacion(id);
        return ResponseEntity.noContent().build();
    }
    
//    @GetMapping()
//    public ResponseEntity<Page<OperacionResponse>> listarOperaciones(
//            @RequestParam(required = false) String fechaInicio,
//            @RequestParam(required = false) String fechaFin,
//            @RequestParam(required = false) String monto,
//            @RequestParam(required = false) String moneda,
//            @RequestParam(required = false) String tipo,
//            @RequestParam(required = false) Long clienteId,
//            Pageable pageable) {
//
//        // Parseo de parámetros
//        LocalDateTime fechaInicioParsed = operacionService.parsearFecha(fechaInicio);
//        LocalDateTime fechaFinParsed = operacionService.parsearFecha(fechaFin);
//        BigDecimal montoParsed = (monto != null) ? new BigDecimal(monto) : null;
//        TipoOperacion tipoParsed = (tipo != null) ? TipoOperacion.valueOf(tipo) : null;
//
//        // Buscar la moneda si está presente
//        Moneda monedaParsed = null;
//        if (moneda != null && !moneda.isEmpty()) {
//            monedaParsed = monedaService.buscarPorNombre(moneda);        }
//
//        // Crear la Specification usando OperacionSpecification
//        Specification<Operacion> spec = OperacionSpecification.filtrarPorParametros(
//                fechaInicioParsed,
//                fechaFinParsed,
//                montoParsed,
//                monedaParsed,
//                tipoParsed,
//                clienteId
//        );
//
//        // Llamar al servicio con la Specification y el Pageable
//        Page<OperacionResponse> operaciones = operacionService.listarOperaciones(spec, pageable);
//
//        return ResponseEntity.ok(operaciones);
//    }

    @PatchMapping("/{id}/monto-origen")
    public ResponseEntity<OperacionResponse> cambiarMontoOrigen(@PathVariable Long id, @RequestBody OperacionRequest request) {
        OperacionResponse response = operacionService.cambiarMontoOrigen(id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/cliente")
    public ResponseEntity<OperacionResponse> cambiarCliente(@PathVariable Long id, @RequestBody OperacionRequest request) {
        OperacionResponse response = operacionService.cambiarCliente(id, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/pagos/origen")
    public ResponseEntity<OperacionResponse> agregarPagoOrigen(@PathVariable Long id, @RequestBody PagoDTO pagoRequest) {
        OperacionResponse response = operacionService.agregarPagoOrigen(id, pagoRequest);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/pagos/origen")
    public ResponseEntity<OperacionResponse> quitarPagoOrigen(@PathVariable Long id, @RequestBody PagoDTO pagoRequest) {
        OperacionResponse response = operacionService.quitarPagoOrigen(id, pagoRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/pagos/conversion")
    public ResponseEntity<OperacionResponse> agregarPagoConversion(@PathVariable Long id, @RequestBody PagoDTO pagoRequest) {
        OperacionResponse response = operacionService.agregarPagoConversion(id, pagoRequest);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/pagos/conversion")
    public ResponseEntity<OperacionResponse> quitarPagoConversion(@PathVariable Long id, @RequestBody PagoDTO pagoRequest) {
        OperacionResponse response = operacionService.quitarPagoConversion(id, pagoRequest);
        return ResponseEntity.ok(response);
    }
    
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
        binder.registerCustomEditor(String.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                if (text == null || text.trim().isEmpty() || "null".equalsIgnoreCase(text.trim())) {
                    setValue(null);
                } else {
                    setValue(text);
                }
            }
        });
    }
}

