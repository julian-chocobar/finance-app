package com.thames.finance_app.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.WebDataBinder;
import java.beans.PropertyEditorSupport;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import com.thames.finance_app.dtos.TitularRequest;
import com.thames.finance_app.dtos.TitularResponse;
import com.thames.finance_app.services.ClienteService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ModelAttribute;


import lombok.RequiredArgsConstructor;


@Controller
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClienteController {

	private final ClienteService clienteService;

	@GetMapping
	public String listarClientes(@RequestParam(required = false) String nombre, Pageable pageable, Model model) {
		Page<TitularResponse> clientes = (nombre == null || nombre.isEmpty())
				? clienteService.obtenerTodos(pageable)
				: clienteService.buscarPorNombre(nombre, pageable);
		model.addAttribute("clientes", clientes);
		model.addAttribute("nombreFiltro", nombre);
		return "clientes/lista";
	}

	@GetMapping("/ver/{id}")
	public String verCliente(@PathVariable Long id, Model model){
		TitularResponse cliente = clienteService.obtenerPorID(id);
		model.addAttribute("cliente", cliente);
		return "clientes/ver";
	}

	@GetMapping("/crear")
    public String mostrarFormularioCrear(Model model) {
        TitularRequest cliente = new TitularRequest();
        model.addAttribute("cliente", cliente);
        return "clientes/crear";
    }

	@PostMapping("/guardar")
	public String crearCliente(@ModelAttribute TitularRequest clienteRequest) {
	    clienteService.crear(clienteRequest);
	    return "redirect:/clientes"; 
	}

	@GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        TitularResponse cliente = clienteService.obtenerPorID(id);
        model.addAttribute("cliente", cliente);
        return "clientes/editar";
    }

	@PostMapping("/actualizar/{id}")
	public String actuslizarCliente(@PathVariable Long id, @ModelAttribute TitularRequest clienteRequest) {
	    clienteService.actualizar(id, clienteRequest);
	    return "redirect:/clientes";
	}

	@PostMapping("/eliminar/{id}")
	public String eliminarCliente(@PathVariable Long id) {
	    clienteService.eliminar(id);
	    return "redirect:/clientes";
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
