function mostrarModalOperacion(modo, id = null, tipo = "", nombreCliente = "", monedaOrigen = "", montoOrigen = "",
    monedaConversion = "", valorTipoCambio = "", nombreReferido = "",
    puntosReferido = "", gananciaReferido = "", monedaReferido = "",
    pagosOrigen = [], pagosConversion = []) {

    const form = document.getElementById("operacionForm");

    if (!form) {
        console.error("Formulario de operación no encontrado");
        return;
    }

    // Configurar la acción del formulario según el modo
    if (modo === "crear") {
        form.action = "/operaciones";
        form.method = "post";
    } else if (modo === "editar" && id) {
        form.action = "/operaciones/editar/" + id;
        form.method = "post";
    }

    // Limpiar los contenedores de pagos existentes
    document.getElementById("pagosOrigenContainer").innerHTML = "";
    document.getElementById("pagosConversionContainer").innerHTML = "";

    // Establecer los valores de los campos del formulario
    document.getElementById("nombreCliente").value = nombreCliente;
    document.getElementById("tipo").value = tipo;
    document.getElementById("monedaOrigen").value = monedaOrigen;
    document.getElementById("montoOrigen").value = montoOrigen;
    document.getElementById("monedaConversion").value = monedaConversion;
    document.getElementById("valorTipoCambio").value = valorTipoCambio;

    document.getElementById("nombreReferido").value = (nombreReferido === "null") ? "" : nombreReferido;
    document.getElementById("puntosReferido").value = (puntosReferido === "null") ? "" : puntosReferido;
    document.getElementById("gananciaReferido").value = (gananciaReferido === "null") ? "" : gananciaReferido;
    document.getElementById("monedaReferido").value = (monedaReferido === "null") ? "" : monedaReferido;

    // Cargar pagos de origen en el modal
    pagosOrigen.forEach((pago, index) => {
        agregarPago("pagosOrigenContainer", "pagosOrigen", pago.fecha, pago.tipoEntrega, pago.valor, index);
    });

    // Cargar pagos de conversión en el modal
    pagosConversion.forEach((pago, index) => {
        agregarPago("pagosConversionContainer", "pagosConversion", pago.fecha, pago.tipoEntrega, pago.valor, index);
    });

    // Mostrar el modal
    document.getElementById("modal-overlay").style.display = "block";
}


// Función para agregar pagos a la lista editable dentro del modal
function agregarPago(containerId, inputName, fecha = "", tipoEntrega = "", valor = "", index = null) {
    const container = document.getElementById(containerId);
    if (!container) {
        console.error("Contenedor de pagos no encontrado: " + containerId);
        return;
    }

    const pagoIndex = index !== null ? index : container.children.length;
    const div = document.createElement("div");
    div.classList.add("pago-fila");
    div.innerHTML = `
        <input type="date" name="${inputName}[${pagoIndex}].fecha" value="${fecha}" required>
        <input type="text" name="${inputName}[${pagoIndex}].tipoEntrega" value="${tipoEntrega}" placeholder="Tipo Entrega" required>
        <input type="number" name="${inputName}[${pagoIndex}].valor" value="${valor}" placeholder="Valor" required step="0.01">
        <button type="button" onclick="this.parentElement.remove()">❌</button>
    `;

    container.appendChild(div);
}

// Función para agregar un nuevo pago vacío (usado en botón "Agregar Pago")
function nuevoPago(containerId, inputName) {
    agregarPago(containerId, inputName);
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


		    function agregarPagoOrigen() {
		    const container = document.getElementById('pagosOrigenContainer');
		    const pagoHTML = `
		        <div class="pago">
		            <select name="tipoEntregaOrigen">
		                <option value="TRANSFERENCIA">Transferencia</option>
		                <option value="OFICINA">Oficina</option>
		                <option value="DELIVERY">Delivery</option>
		                <option value="BANCO">Banco</option>
		            </select>
		            <input type="number" name="valorOrigen" step="0.01" placeholder="Valor" required>
		            <button type="button" onclick="eliminarPago(this)">Eliminar</button>
		        </div>
		    `;
		    container.insertAdjacentHTML('beforeend', pagoHTML);
		}

		function agregarPagoConversion() {
		    const container = document.getElementById('pagosConversionContainer');
		    const pagoHTML = `
		        <div class="pago">
		            <select name="tipoEntregaMonto">
		                <option value="TRANSFERENCIA">Transferencia</option>
		                <option value="OFICINA">Oficina</option>
		                <option value="DELIVERY">Delivery</option>
		                <option value="BANCO">Banco</option>
		            </select>
		            <input type="number" name="valorMonto" step="0.01" placeholder="Valor" required>
		            <button type="button" onclick="eliminarPago(this)">Eliminar</button>
		        </div>
		    `;
		    container.insertAdjacentHTML('beforeend', pagoHTML);
		}

    function eliminarPago(button) {
        const pagoDiv = button.parentElement;
        pagoDiv.remove();
    }
    
     function togglePagos(id) {
        const element = document.getElementById(id);
        if (element.style.display === "none") {
            element.style.display = "block";
        } else {
            element.style.display = "none";
        }
    }
    
    