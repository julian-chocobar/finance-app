document.addEventListener('DOMContentLoaded', function() {
    // Inicializar tooltips de Bootstrap
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });

    // Función para formatear fecha y hora
    function formatDateTime(date) {
        const pad = (num) => String(num).padStart(2, '0');
        const d = new Date(date);
        return `${pad(d.getDate())}-${pad(d.getMonth() + 1)}-${d.getFullYear()} ${pad(d.getHours())}:${pad(d.getMinutes())}`;
    }

    // Función para obtener fecha y hora actual en formato ISO
    function getCurrentDateTime() {
        const now = new Date();
        return now.toISOString().slice(0, 16); // Formato: YYYY-MM-DDThh:mm
    }

    // Variables globales para elementos del formulario
    const monedaOrigenInput = document.getElementById('monedaOrigen');
    const monedaConversionInput = document.getElementById('monedaConversion');
    const tipoOperacionSelect = document.getElementById('tipo');
    const tipoCambioInput = document.getElementById('valorTipoCambio');
    const montoOrigenInput = document.getElementById('montoOrigen');
    const montoCalculadoInput = document.getElementById('montoCalculado');
    const puntosReferidoInput = document.getElementById('puntosReferido');
    const gananciaReferidoInput = document.getElementById('gananciaReferido');

    // Función para obtener y actualizar el tipo de cambio
    function actualizarTipoCambio() {
        // Validar monedas iguales
        if (monedaOrigenInput.value === monedaConversionInput.value && monedaOrigenInput.value !== '' && monedaConversionInput.value !== '') {
            tipoCambioInput.value = '';
            tipoCambioInput.placeholder = 'Error: Monedas iguales';
            tipoCambioInput.classList.add('is-invalid');
            return;
        } else {
            tipoCambioInput.placeholder = 'Esperando...';
            tipoCambioInput.classList.remove('is-invalid');
        }

        // Solo consultar si tenemos todos los datos necesarios
        if (monedaOrigenInput.value && monedaConversionInput.value && tipoOperacionSelect.value) {
            console.log(`Solicitando tipo de cambio para ${monedaOrigenInput.value} → ${monedaConversionInput.value} (${tipoOperacionSelect.value})`);

            // Solo actualizar si el campo no está siendo editado manualmente
            if (!tipoCambioInput.dataset.editadoManualmente) {
                fetch(`/tipoCambio/obtener?monedaOrigen=${monedaOrigenInput.value}&monedaConversion=${monedaConversionInput.value}&esCompra=${tipoOperacionSelect.value === 'COMPRA'}`)
                    .then(response => {
                        if (!response.ok) {
                            throw new Error('No se pudo obtener el tipo de cambio');
                        }
                        return response.json();
                    })
                    .then(data => {
                        tipoCambioInput.value = data.valor;
                        tipoCambioInput.classList.remove('is-invalid');
                        // Recalcular monto de conversión
                        calcularMontoConversion();
                    })
                    .catch(error => {
                        console.error('Error:', error);
                        tipoCambioInput.placeholder = 'Error al obtener tipo de cambio';
                        tipoCambioInput.classList.add('is-invalid');
                    });
            }
        }
    }

    // Eventos para actualizar tipo de cambio
    if (monedaOrigenInput && monedaConversionInput && tipoOperacionSelect) {
        monedaOrigenInput.addEventListener('change', actualizarTipoCambio);
        monedaConversionInput.addEventListener('change', actualizarTipoCambio);
        tipoOperacionSelect.addEventListener('change', actualizarTipoCambio);
        
        // Marcar cuando el usuario edita manualmente el tipo de cambio
        tipoCambioInput.addEventListener('input', function(e) {
            e.target.dataset.editadoManualmente = true;
            e.target.classList.remove('is-invalid');
        });
        
        // Actualizar al cargar la página
        actualizarTipoCambio();
    }

    // Función para calcular el monto de conversión
    function calcularMontoConversion() {
        const montoOrigen = parseFloat(montoOrigenInput.value) || 0;
        const tipoCambio = parseFloat(tipoCambioInput.value) || 0;
        const montoCalculado = montoOrigen * tipoCambio;
        montoCalculadoInput.value = montoCalculado.toFixed(2);
    }

    // Eventos para calcular el monto de conversión
    if (montoOrigenInput && tipoCambioInput) {
        montoOrigenInput.addEventListener('input', calcularMontoConversion);
        tipoCambioInput.addEventListener('input', calcularMontoConversion);
        
        // Calcular al cargar la página
        calcularMontoConversion();
    }

    // Función para calcular la ganancia del referido
    function calcularGananciaReferido() {
        const puntosReferido = parseFloat(puntosReferidoInput.value) || 0;
        const montoOrigen = parseFloat(montoOrigenInput.value) || 0;
        const gananciaCalculada = puntosReferido * montoOrigen;
        
        // Solo actualizar si el campo no está siendo editado manualmente
        if (!gananciaReferidoInput.dataset.editadoManualmente) {
            gananciaReferidoInput.value = gananciaCalculada.toFixed(2);
        }
    }

    // Eventos para calcular la ganancia del referido
    if (puntosReferidoInput) {
        puntosReferidoInput.addEventListener('input', calcularGananciaReferido);
        montoOrigenInput.addEventListener('input', calcularGananciaReferido);
        
        // Marcar cuando el usuario edita manualmente la ganancia
        gananciaReferidoInput.addEventListener('input', function(e) {
            e.target.dataset.editadoManualmente = true;
        });
        
        // Calcular al cargar la página
        calcularGananciaReferido();
    }

    // Función para actualizar el total de pagos origen
    function actualizarTotalPagosOrigen() {
        const inputs = document.querySelectorAll('.pago-origen-valor');
        let total = 0;
        
        inputs.forEach(input => {
            total += parseFloat(input.value) || 0;
        });
        
        document.getElementById('totalPagosOrigen').textContent = total.toFixed(2);
    }

    // Función para actualizar el total de pagos conversión
    function actualizarTotalPagosConversion() {
        const inputs = document.querySelectorAll('.pago-conversion-valor');
        let total = 0;
        
        inputs.forEach(input => {
            total += parseFloat(input.value) || 0;
        });
        
        document.getElementById('totalPagosConversion').textContent = total.toFixed(2);
    }

    // Eventos para actualizar totales cuando cambian los valores
    document.addEventListener('input', function(e) {
        if (e.target.classList.contains('pago-origen-valor')) {
            actualizarTotalPagosOrigen();
        } else if (e.target.classList.contains('pago-conversion-valor')) {
            actualizarTotalPagosConversion();
        }
    });

    // Inicializar totales al cargar la página
    if (document.getElementById('totalPagosOrigen')) {
        actualizarTotalPagosOrigen();
    }
    
    if (document.getElementById('totalPagosConversion')) {
        actualizarTotalPagosConversion();
    }

    // Función para agregar un nuevo pago origen
    const btnAgregarPagoOrigen = document.getElementById('agregarPagoOrigen');
    if (btnAgregarPagoOrigen) {
        btnAgregarPagoOrigen.addEventListener('click', function() {
            const tabla = document.getElementById('tablaPagosOrigen').getElementsByTagName('tbody')[0];
            const filaVacia = tabla.querySelector('tr td[colspan="4"]');
            if (filaVacia) {
                filaVacia.parentNode.remove();
            }
            
            const numFilas = tabla.rows.length;
            const nuevaFila = tabla.insertRow();
            
            nuevaFila.innerHTML = `
                <td>
                    <input type="datetime-local" class="form-control" name="pagosOrigen[${numFilas}].fecha" required>
                </td>
                <td>
                    <select class="form-select" name="pagosOrigen[${numFilas}].tipoEntrega" required>
                        <option value="">Seleccione...</option>
                        <option value="TRANSFERENCIA">Transferencia</option>
                        <option value="OFICINA">Oficina</option>
                        <option value="DELIVERY">Delivery</option>
                        <option value="BANCO">Banco</option>
                    </select>
                </td>
                <td>
                    <input type="number" step="0.01" class="form-control pago-origen-valor" name="pagosOrigen[${numFilas}].valor" value="0.00" required>
                </td>
                <td>
                    <button type="button" class="btn btn-sm btn-danger eliminar-pago-origen">
                        <i class="fas fa-trash"></i>
                    </button>
                </td>
            `;
            
            // Establecer la fecha y hora actual por defecto
            const fechaInput = nuevaFila.querySelector('input[type="datetime-local"]');
            fechaInput.value = getCurrentDateTime();
            
            actualizarTotalPagosOrigen();
        });
    }

    // Función para agregar un nuevo pago conversión
    const btnAgregarPagoConversion = document.getElementById('agregarPagoConversion');
    if (btnAgregarPagoConversion) {
        btnAgregarPagoConversion.addEventListener('click', function() {
            const tabla = document.getElementById('tablaPagosConversion').getElementsByTagName('tbody')[0];
            const filaVacia = tabla.querySelector('tr td[colspan="4"]');
            if (filaVacia) {
                filaVacia.parentNode.remove();
            }
            
            const numFilas = tabla.rows.length;
            const nuevaFila = tabla.insertRow();
            
            nuevaFila.innerHTML = `
                <td>
                    <input type="datetime-local" class="form-control" name="pagosConversion[${numFilas}].fecha" required>
                </td>
                <td>
                    <select class="form-select" name="pagosConversion[${numFilas}].tipoEntrega" required>
                        <option value="">Seleccione...</option>
                        <option value="TRANSFERENCIA">Transferencia</option>
                        <option value="OFICINA">Oficina</option>
                        <option value="DELIVERY">Delivery</option>
                        <option value="BANCO">Banco</option>
                    </select>
                </td>
                <td>
                    <input type="number" step="0.01" class="form-control pago-conversion-valor" name="pagosConversion[${numFilas}].valor" value="0.00" required>
                </td>
                <td>
                    <button type="button" class="btn btn-sm btn-danger eliminar-pago-conversion">
                        <i class="fas fa-trash"></i>
                    </button>
                </td>
            `;
            
            // Establecer la fecha y hora actual por defecto
            const fechaInput = nuevaFila.querySelector('input[type="datetime-local"]');
            fechaInput.value = getCurrentDateTime();
            
            actualizarTotalPagosConversion();
        });
    }

    // Evento para eliminar pagos origen
    document.addEventListener('click', function(e) {
        if (e.target.classList.contains('eliminar-pago-origen') || 
            e.target.closest('.eliminar-pago-origen')) {
            
            const boton = e.target.classList.contains('eliminar-pago-origen') ? 
                          e.target : e.target.closest('.eliminar-pago-origen');
            const fila = boton.closest('tr');
            const tabla = fila.closest('tbody');
            
            fila.remove();
            
            // Si no quedan filas, agregar mensaje de "No hay pagos"
            if (tabla.rows.length === 0) {
                const nuevaFila = tabla.insertRow();
                nuevaFila.innerHTML = '<td colspan="4" class="text-center">No hay pagos registrados</td>';
            }
            
            // Reindexar los nombres de los campos
            const filas = tabla.rows;
            for (let i = 0; i < filas.length; i++) {
                if (!filas[i].querySelector('td[colspan="4"]')) {
                    const inputs = filas[i].querySelectorAll('input, select');
                    inputs.forEach(input => {
                        const name = input.getAttribute('name');
                        if (name) {
                            const newName = name.replace(/\[\d+\]/, `[${i}]`);
                            input.setAttribute('name', newName);
                        }
                    });
                }
            }
            
            actualizarTotalPagosOrigen();
        }
    });

    // Evento para eliminar pagos conversión
    document.addEventListener('click', function(e) {
        if (e.target.classList.contains('eliminar-pago-conversion') || 
            e.target.closest('.eliminar-pago-conversion')) {
            
            const boton = e.target.classList.contains('eliminar-pago-conversion') ? 
                          e.target : e.target.closest('.eliminar-pago-conversion');
            const fila = boton.closest('tr');
            const tabla = fila.closest('tbody');
            
            fila.remove();
            
            // Si no quedan filas, agregar mensaje de "No hay pagos"
            if (tabla.rows.length === 0) {
                const nuevaFila = tabla.insertRow();
                nuevaFila.innerHTML = '<td colspan="4" class="text-center">No hay pagos registrados</td>';
            }
            
            // Reindexar los nombres de los campos
            const filas = tabla.rows;
            for (let i = 0; i < filas.length; i++) {
                if (!filas[i].querySelector('td[colspan="4"]')) {
                    const inputs = filas[i].querySelectorAll('input, select');
                    inputs.forEach(input => {
                        const name = input.getAttribute('name');
                        if (name) {
                            const newName = name.replace(/\[\d+\]/, `[${i}]`);
                            input.setAttribute('name', newName);
                        }
                    });
                }
            }
            
            actualizarTotalPagosConversion();
        }
    });

    // Función para confirmar eliminación de operación
    window.confirmarEliminar = function(id) {
        const modal = document.getElementById('eliminarModal');
        const bsModal = new bootstrap.Modal(modal);
        const form = document.getElementById('formEliminar');
        
        form.action = form.action.replace('{id}', id);
        bsModal.show();
    };
});

document.addEventListener("DOMContentLoaded", function () {
    document.querySelectorAll(".btnEliminar").forEach(button => {
        button.addEventListener("click", function () {
            let id = this.getAttribute("data-id"); // Obtiene el ID de la operación
            let form = document.getElementById("formEliminar");
            form.setAttribute("action", `/operaciones/${id}/eliminar`); // Actualiza la acción del formulario
        });
    });
});