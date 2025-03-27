document.addEventListener('DOMContentLoaded', function() {
    // Inicializar tooltips de Bootstrap
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });

    // Función para formatear números con 2 decimales
    function formatearNumero(numero) {
        return parseFloat(numero).toFixed(2);
    }

    // Función para formatear fecha y hora
    function formatearFechaHora(fecha) {
        if (!fecha) return '';
        const d = new Date(fecha);
        return d.toLocaleString('es-ES', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    }

    // Función para obtener fecha y hora actual en formato ISO
    function obtenerFechaHoraActual() {
        const now = new Date();
        const year = now.getFullYear();
        const month = String(now.getMonth() + 1).padStart(2, '0');
        const day = String(now.getDate()).padStart(2, '0');
        const hours = String(now.getHours()).padStart(2, '0');
        const minutes = String(now.getMinutes()).padStart(2, '0');
        
        return `${year}-${month}-${day}T${hours}:${minutes}`;
    }

    // Establecer fecha y hora actual en campos de fecha al cargar la página
    const camposFecha = document.querySelectorAll('input[type="datetime-local"]');
    camposFecha.forEach(campo => {
        if (!campo.value) {
            campo.value = obtenerFechaHoraActual();
        }
    });

    // Validación para saldos de caja
    const saldoRealInput = document.getElementById('saldoReal');
    const saldoDisponibleInput = document.getElementById('saldoDisponible');
    
    if (saldoRealInput && saldoDisponibleInput) {
        function validarSaldos() {
            const saldoReal = parseFloat(saldoRealInput.value) || 0;
            const saldoDisponible = parseFloat(saldoDisponibleInput.value) || 0;
            
            if (saldoDisponible > saldoReal) {
                saldoDisponibleInput.setCustomValidity('El saldo disponible no puede ser mayor que el saldo real');
                saldoDisponibleInput.classList.add('is-invalid');
            } else {
                saldoDisponibleInput.setCustomValidity('');
                saldoDisponibleInput.classList.remove('is-invalid');
            }
        }
        
        saldoRealInput.addEventListener('input', validarSaldos);
        saldoDisponibleInput.addEventListener('input', validarSaldos);
        
        // Validar al cargar la página
        validarSaldos();
    }

    // Validación para montos de movimientos
    const montoInput = document.getElementById('monto');
    const montoEjecutadoInput = document.getElementById('montoEjecutado');
    
    if (montoInput && montoEjecutadoInput) {
        function validarMontos() {
            const monto = parseFloat(montoInput.value) || 0;
            const montoEjecutado = parseFloat(montoEjecutadoInput.value) || 0;
            
            if (montoEjecutado < monto) {
                montoEjecutadoInput.setCustomValidity('El monto ejecutado no puede ser menor que el monto');
                montoEjecutadoInput.classList.add('is-invalid');
            } else {
                montoEjecutadoInput.setCustomValidity('');
                montoEjecutadoInput.classList.remove('is-invalid');
            }
        }
        
        montoInput.addEventListener('input', function() {
            validarMontos();
            
            // Actualizar montoEjecutado cuando cambia monto (si no ha sido editado manualmente)
            if (!montoEjecutadoInput.dataset.editadoManualmente) {
                montoEjecutadoInput.value = this.value;
            }
        });
        
        montoEjecutadoInput.addEventListener('input', function() {
            validarMontos();
            this.dataset.editadoManualmente = true;
        });
        
        // Validar al cargar la página
        validarMontos();
    }

    // Configurar modales de eliminación
    const btnEliminar = document.querySelectorAll('.btnEliminar');
    if (btnEliminar.length > 0) {
        btnEliminar.forEach(button => {
            button.addEventListener('click', function() {
                const id = this.getAttribute('data-id');
                const formEliminar = document.getElementById('formEliminar');
                formEliminar.action = formEliminar.action.replace('{id}', id);
            });
        });
    }

    const btnEliminarMovimiento = document.querySelectorAll('.btnEliminarMovimiento');
    if (btnEliminarMovimiento.length > 0) {
        btnEliminarMovimiento.forEach(button => {
            button.addEventListener('click', function() {
                const id = this.getAttribute('data-id');
                const formEliminarMovimiento = document.getElementById('formEliminarMovimiento');
                formEliminarMovimiento.action = formEliminarMovimiento.action.replace('{id}', id);
            });
        });
    }
});