package com.thames.finance_app.controllers;

import java.beans.PropertyEditorSupport;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

import com.thames.finance_app.dtos.TitularRequest;
import com.thames.finance_app.dtos.TitularResponse;
import com.thames.finance_app.services.ReferidoService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/referidos")
@RequiredArgsConstructor
public class ReferidoController {

	private final ReferidoService referidoService;

    @GetMapping
    public String listarReferidos(@RequestParam(required = false) String nombre, Pageable pageable, Model model) {
        Page<TitularResponse> referidos = (nombre == null || nombre.isEmpty())
                ? referidoService.obtenerTodos(pageable)
                : referidoService.obtenerPorNombre(nombre, pageable);
        model.addAttribute("referidos", referidos);
        model.addAttribute("nombreFiltro", nombre);
        return "referidos/lista";
    }

	@GetMapping("/ver/{id}")
	public String verReferido(@PathVariable Long id, Model model) {
		TitularResponse referido = referidoService.obtenerPorID(id);
		model.addAttribute("referido", referido);
		return "referidos/ver";
	}

	@GetMapping("/crear")
    public String mostrarFormularioCrear(Model model) {
        TitularRequest referido = new TitularRequest();
        model.addAttribute("referido", referido);
        return "referidos/crear";
    }

	@PostMapping("/guardar")
	public String crearReferido(@ModelAttribute TitularRequest referidoRequest) {
	    referidoService.crearReferido(referidoRequest);
	    return "redirect:/referidos"; 
	}

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        TitularResponse referido = referidoService.obtenerPorID(id);
        model.addAttribute("referido", referido);
        return "referidos/editar";
    }

	@PostMapping("/actualizar/{id}")
	public String actualizarReferido(@PathVariable Long id, @ModelAttribute TitularRequest referidoRequest) {
	    referidoService.actualizarReferido(id, referidoRequest);
	    return "redirect:/referidos";
	}

	@PostMapping("/eliminar/{id}")
	public String eliminarReferido(@PathVariable Long id) {
	    referidoService.eliminarReferido(id);
	    return "redirect:/referidos";
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
