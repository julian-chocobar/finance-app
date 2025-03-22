document.addEventListener('DOMContentLoaded', function() {
    // Inicializar tooltips de Bootstrap
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });

    // Función para calcular el monto de conversión
    function calcularMontoConversion() {
        const montoOrigen = parseFloat(document.getElementById('montoOrigen').value) || 0;
        const tipoCambio = parseFloat(document.getElementById('valorTipoCambio').value) || 0;
        const montoCalculado = montoOrigen * tipoCambio;
        document.getElementById('montoCalculado').value = montoCalculado.toFixed(2);
    }

    // Eventos para calcular el monto de conversión
    const montoOrigenInput = document.getElementById('montoOrigen');
    const tipoCambioInput = document.getElementById('valorTipoCambio');
    
    if (montoOrigenInput && tipoCambioInput) {
        montoOrigenInput.addEventListener('input', calcularMontoConversion);
        tipoCambioInput.addEventListener('input', calcularMontoConversion);
        
        // Calcular al cargar la página
        calcularMontoConversion();
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
                    <input type="date" class="form-control" name="pagosOrigen[${numFilas}].fecha" required>
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
            
            // Establecer la fecha actual por defecto
            const fechaInput = nuevaFila.querySelector('input[type="date"]');
            const hoy = new Date().toISOString().split('T')[0];
            fechaInput.value = hoy;
            
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
                    <input type="date" class="form-control" name="pagosConversion[${numFilas}].fecha" required>
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
            
            // Establecer la fecha actual por defecto
            const fechaInput = nuevaFila.querySelector('input[type="date"]');
            const hoy = new Date().toISOString().split('T')[0];
            fechaInput.value = hoy;
            
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