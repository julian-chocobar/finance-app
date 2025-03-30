document.addEventListener('DOMContentLoaded', function() {
    // Inicializar tooltips de Bootstrap
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
    
    // Validación para formularios de tipo de cambio
    const formTipoCambio = document.querySelector('form.needs-validation');
    if (formTipoCambio) {
        const selectMonedaOrigen = document.getElementById('codigoMonedaOrigen');
        const selectMonedaConversion = document.getElementById('codigoMonedaConversion');
        
        formTipoCambio.addEventListener('submit', function(event) {
            // Validar que ambas monedas estén seleccionadas
            if (!selectMonedaOrigen.value || !selectMonedaConversion.value) {
                event.preventDefault();
                event.stopPropagation();
                return;
            }
            
            // Validar que las monedas sean diferentes
            if (selectMonedaOrigen.value === selectMonedaConversion.value) {
                event.preventDefault();
                event.stopPropagation();
                alert('La moneda origen y la moneda de conversión deben ser diferentes.');
                return;
            }
        });
        
        // Actualizar etiquetas de moneda de conversión
        if (selectMonedaConversion) {
            const labelMonedaConversion1 = document.getElementById('labelMonedaConversion1');
            const labelMonedaConversion2 = document.getElementById('labelMonedaConversion2');
            
            function actualizarEtiquetas() {
                if (selectMonedaConversion.selectedIndex > 0) {
                    const monedaConversionSeleccionada = selectMonedaConversion.options[selectMonedaConversion.selectedIndex].value;
                    if (labelMonedaConversion1) labelMonedaConversion1.textContent = monedaConversionSeleccionada;
                    if (labelMonedaConversion2) labelMonedaConversion2.textContent = monedaConversionSeleccionada;
                }
            }
            
            selectMonedaConversion.addEventListener('change', actualizarEtiquetas);
            
            // Inicializar etiquetas al cargar la página
            actualizarEtiquetas();
        }
    }
    
    // Configurar modales de eliminación
    const btnEliminarMoneda = document.querySelectorAll('.btnEliminarMoneda');
    if (btnEliminarMoneda.length > 0) {
        btnEliminarMoneda.forEach(button => {
            button.addEventListener('click', function() {
                const codigo = this.getAttribute('data-codigo');
                const formEliminar = document.getElementById('formEliminarMoneda');
                formEliminar.action = formEliminar.action + '/' + codigo;
            });
        });
    }
    
    const btnEliminarTipoCambio = document.querySelectorAll('.btnEliminarTipoCambio');
    if (btnEliminarTipoCambio.length > 0) {
        btnEliminarTipoCambio.forEach(button => {
            button.addEventListener('click', function() {
                const id = this.getAttribute('data-id');
                const formEliminar = document.getElementById('formEliminarTipoCambio');
                formEliminar.action = formEliminar.action + '/' + id;
            });
        });
    }
});