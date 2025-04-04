package com.thames.finance_app.controllers;

import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.http.ResponseEntity;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import com.thames.finance_app.dtos.MonedaDTO;
import com.thames.finance_app.dtos.TipoCambioDTO;
import com.thames.finance_app.models.Moneda;
import com.thames.finance_app.services.MonedaService;
import com.thames.finance_app.services.TipoCambioService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/monedas")
@RequiredArgsConstructor
public class MonedaController {

	private final MonedaService monedaService;
	private final TipoCambioService tipoCambioService;
		
    @GetMapping("/{id}")
    public String verMoneda(@PathVariable Long id, Model model) {
        MonedaDTO moneda = monedaService.obtenerDTOPorId(id);
        model.addAttribute("moneda", moneda);
        return "monedas/ver";
    }

    @GetMapping
    public String listarTodasLasMonedas( Pageable pageable, Model model) {
        List<MonedaDTO> monedas = monedaService.listarTodasLasMonedas();
        Page<TipoCambioDTO> tiposCambio = tipoCambioService.listar(pageable);
        model.addAttribute("monedas", monedas);
        model.addAttribute("tiposCambio", tiposCambio);
        return "monedas/lista";
    }
    
    @GetMapping("/crear")
    public String mostrarFormularioCrear(Model model) {
    	MonedaDTO moneda = new MonedaDTO();
    	model.addAttribute("moneda", moneda);
    	return "monedas/crear"; 	
    }
	
    @PostMapping("/guardar")
    public String crearMoneda(@ModelAttribute MonedaDTO monedaDTO) {
        monedaService.crearMoneda(monedaDTO);
        return "redirect:/monedas";
    }
    
    @GetMapping("/{id}/editar")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
    	MonedaDTO moneda = monedaService.obtenerDTOPorId(id);
    	model.addAttribute("moneda", moneda);
    	return "monedas/editar";    	
    }
    
    @PostMapping("/{id}/actualizar")
    public String actualizarMoneda(@PathVariable Long id, @ModelAttribute MonedaDTO monedaDTO) {
        monedaService.actualizarMoneda(id, monedaDTO);
        return "redirect:/monedas";
    }
    
    @PostMapping("/eliminar/{codigo}")
    public String eliminarMoneda(@PathVariable String codigo) {
        monedaService.eliminarMonedaPorCodigo(codigo);
        return "redirect:/monedas";
    }
     
//-------------------------------------------------------------------------------    

    @GetMapping("/tipoCambio/crear")
	public String mostrarFormularioCrearTipoCambio(Model model) {
        List<MonedaDTO> monedas = monedaService.listarTodasLasMonedas();
        TipoCambioDTO tipoCambio = new TipoCambioDTO();
        model.addAttribute("tipoCambio", tipoCambio);
        model.addAttribute("monedas", monedas);
        return "monedas/tipoCambio/crear";
    }

    @PostMapping("/tipoCambio/guardar")
    public String crearTipoCambio(@ModelAttribute TipoCambioDTO tipoCambioDTO) {
        tipoCambioService.crearTipoCambio(tipoCambioDTO);
        return "redirect:/monedas";
    }

    @GetMapping("/tipoCambio/{id}/editar")
    public String mostrarFormularioEditarTipoCambio(@PathVariable Long id, Model model) {
        List<MonedaDTO> monedas = monedaService.listarTodasLasMonedas();
        TipoCambioDTO tipoCambio = tipoCambioService.obtenerDTOPorId(id);
        model.addAttribute("tipoCambio", tipoCambio);
        model.addAttribute("monedas", monedas);
        return "monedas/tipoCambio/editar";
    }

    @PostMapping("/tipoCambio/{id}/actualizar")
    public String actualizarTipoCambio(@PathVariable Long id, @ModelAttribute TipoCambioDTO tipoCambioDTO) {
        tipoCambioService.actualizarTipoCambio(tipoCambioDTO, id);
        return "redirect:/monedas";
    }

    @PostMapping("/tipoCambio/eliminar/{id}")
    public String eliminarTipoCambio(@PathVariable Long id) {
        tipoCambioService.eliminarTipoCambio(id);
        return "redirect:/monedas";
    }

	@GetMapping("/tipoCambio/obtener")
	public ResponseEntity<Map<String, BigDecimal>> obtenerTipoCambio(
	        @RequestParam String monedaOrigen,
	        @RequestParam String monedaConversion,
	        @RequestParam boolean esCompra) {

		Moneda origen = monedaService.buscarPorCodigo(monedaOrigen);
		Moneda conversion = monedaService.buscarPorCodigo(monedaConversion);

	    BigDecimal tipoCambio = tipoCambioService.obtenerTipoCambio(origen, conversion, esCompra);
	    Map<String, BigDecimal> response = Collections.singletonMap("valor", tipoCambio);

	    return ResponseEntity.ok(response);
	}
	

	//-------------------------------------------------------------------------------    
  
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
