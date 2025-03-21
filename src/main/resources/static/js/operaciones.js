function mostrarModalOperacion(modo, id = null, tipo = "", nombreCliente = "", monedaOrigen = "", montoOrigen = "", 
										monedaConversion = "", valorTipoCambio = "", nombreReferido = "", 
										puntosReferido = "", gananciaReferido = "", monedaReferido = "") {
	const form = document.getElementById("operacionForm");
	if (!form) {
		console.error("Formulario de operación no encontrado");
		return;
	}
	if (modo === "crear") {
		form.action = "/operaciones"; // URL para crear una nueva operación
		form.method = "post";

	} else if (modo === "editar" && id) {
		form.action = "/operaciones/editar/" + id; // URL con el ID en la ruta
		form.method = "post";
	}				
	document.getElementById("nombreCliente").value = nombreCliente;		       		 
	document.getElementById("tipo").value = tipo;		        
	document.getElementById("monedaOrigen").value = monedaOrigen;		        
	document.getElementById("montoOrigen").value = montoOrigen;		        
	document.getElementById("monedaConversion").value = monedaConversion;		        
	document.getElementById("valorTipoCambio").value = valorTipoCambio;
	
	document.getElementById("nombreReferido").value = (nombreReferido === "null") ? "" : nombreReferido;
	document.getElementById("puntosReferido").value = (puntosReferido === "null") ? "" : puntosReferido;
	document.getElementById("gananciaReferido").value = (gananciaReferido === "null") ? "" : gananciaReferido;
	document.getElementById("monedaReferido").value =(monedaReferido === "null") ? "" :monedaReferido;					       
	// Mostrar el modal
	document.getElementById("modal-overlay").style.display = "block";
}

							    
		    function confirmarEliminacionOperacion(operacionId) {
		        const confirmar = confirm("¿Estás seguro de que deseas eliminar esta operación?");
		        if (confirmar) {
		            fetch(`/operaciones/${operacionId}`, {
		                method: 'DELETE',
		                headers: { 'Content-Type': 'application/json' },
		            })
		            .then(response => {
		                if (response.ok) {
		                    window.location.reload();
		                } else {
		                    alert("Hubo un error al eliminar la operación.");
		                }
		            })
		            .catch(error => {
		                console.error("Error:", error);
		                alert("Hubo un error al eliminar la operación.");
		            });
		        }
		    }

			
		
		function toggleSidebar() {
		    const sidebar = document.getElementById("sidebar");
		    sidebar.style.left = sidebar.style.left === "0px" ? "-250px" : "0px";
		}

		function ocultarModal() {
		    document.getElementById("modal-overlay").style.display = "none";
		}

		function toggleReferido() {
		    const checkbox = document.getElementById("tieneReferido");
		    const referidoFields = document.getElementById("referidoFields");
		    if (checkbox.checked) {
		        referidoFields.style.display = "block";
		    } else {
		        referidoFields.style.display = "none";
		        document.getElementById("nombreReferido").value = "";
		        document.getElementById("puntosReferido").value = "";
		        document.getElementById("gananciaReferido").value = "";
		        document.getElementById("monedaReferido").value = "";
		    }
		}
		
		function formatearNumero(input) {
		    let value = input.value.replace(/,/g, '');
		    if (!isNaN(value) && value !== "") {
		        input.value = parseFloat(value).toLocaleString('en-US', {
		            minimumFractionDigits: 2,
		            maximumFractionDigits: 2
		        });
		    }
		}
			
			
		function actualizarTipoCambio() {
			const monedaOrigen = document.getElementById("monedaOrigen").value;
			const monedaConversion = document.getElementById("monedaConversion").value;
			const tipoOperacion = document.getElementById("tipo").value;
			const tipoCambioInput = document.getElementById("valorTipoCambio");

			 if (monedaOrigen === monedaConversion && monedaOrigen !== "" && monedaConversion !== "") {
			        tipoCambioInput.value = "";
			        tipoCambioInput.placeholder = "Error: Monedas iguales";
			        tipoCambioInput.style.border = "2px solid red";
			        return;
			    } else {
			        tipoCambioInput.placeholder = "Esperando...";
			        tipoCambioInput.style.border = "";
			    }

			 if (monedaOrigen && monedaConversion && tipoOperacion) {
			        console.log(`Solicitando tipo de cambio para ${monedaOrigen} → ${monedaConversion} (Compra: ${tipoOperacion === 'COMPRA'})`);

			 	fetch(`/tipoCambio/obtener?monedaOrigen=${monedaOrigen}&monedaConversion=${monedaConversion}&esCompra=${tipoOperacion === 'COMPRA'}`)
			    	.then(response => {
			                if (!response.ok) {
			                    throw new Error("No se pudo obtener el tipo de cambio");
			                }
			                return response.json();
			            })
			       .then(data => {
			                console.log("Tipo de Cambio recibido:", data.valor);
			                tipoCambioInput.value = data.valor;
			            })
			       .catch(error => {
			                console.error("Error obteniendo tipo de cambio:", error);
			                tipoCambioInput.value = "";
			                tipoCambioInput.placeholder = "Error al obtener TC";
			            });
			    }
		}
		

