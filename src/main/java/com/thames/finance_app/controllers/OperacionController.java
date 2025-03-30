package com.thames.finance_app.controllers;

import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.thames.finance_app.dtos.OperacionRequest;
import com.thames.finance_app.dtos.OperacionResponse;
import com.thames.finance_app.enums.TipoOperacion;
import com.thames.finance_app.mappers.OperacionMapper;
import com.thames.finance_app.models.Moneda;
import com.thames.finance_app.models.Operacion;
import com.thames.finance_app.services.MonedaService;
import com.thames.finance_app.services.OperacionService;
import com.thames.finance_app.specifications.OperacionSpecification;

import lombok.RequiredArgsConstructor;


@Controller
@RequestMapping("/operaciones")
@RequiredArgsConstructor
public class OperacionController {

    private final OperacionService operacionService;
    private final OperacionMapper operacionMapper;
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
        Specification<Operacion> spec = OperacionSpecification
        		.filtrarPorParametros(
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

    @GetMapping("/crear")
    public String mostrarFormularioCrear(Model model) {
        OperacionRequest operacion = new OperacionRequest();
        model.addAttribute("operacion", operacion);
        return "operaciones/crear";
    }

    @PostMapping("/guardar")
    public String crearOperacion(@ModelAttribute OperacionRequest request) {
        operacionService.crearOperacion(request);
        return "redirect:/operaciones";
    }

    @GetMapping("/{id}")
    public String verOperacion(@PathVariable Long id, Model model) {
        OperacionResponse operacionResponse = operacionService.obtenerResponsePorId(id);
        model.addAttribute("operacion", operacionResponse);
        return "operaciones/ver";
    }

    @GetMapping("/{id}/editar")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
    	 // 1️⃣ Buscar la operación por ID en el servicio
        Operacion operacion = operacionService.obtenerPorId(id);

        // 2️⃣ Convertir la operación a un objeto OperacionRequest
        OperacionRequest operacionRequest = operacionMapper.toRequest(operacion);

        // 3️⃣ Agregar datos adicionales si se necesitan en el formulario
        List<String> tiposOperacion = List.of("COMPRA", "VENTA"); // Simulación de lista de tipos
        model.addAttribute("tiposOperacion", tiposOperacion);
        model.addAttribute("operacionId", operacion.getId());
        model.addAttribute("operacion", operacionRequest);
        return "operaciones/editar";
    }

    @PostMapping("/{id}/actualizar")
    public String actualizarOperacion(@PathVariable Long id, @ModelAttribute OperacionRequest request) {
        operacionService.actualizarOperacion(id, request);
        return "redirect:/operaciones";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminarOperacion(@PathVariable Long id) {
        operacionService.eliminarOperacion(id);
        return "redirect:/operaciones";
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

