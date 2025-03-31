document.addEventListener('DOMContentLoaded', function() {
    // Inicializar tooltips de Bootstrap si bootstrap está disponible
    if (typeof bootstrap !== 'undefined') {
        var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
        var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
            return new bootstrap.Tooltip(tooltipTriggerEl);
        });
    }
    
    // Validación de formularios
    const forms = document.querySelectorAll('.needs-validation');
    Array.prototype.slice.call(forms).forEach(function(form) {
        form.addEventListener('submit', function(event) {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            
            form.classList.add('was-validated');
        }, false);
    });
    
    // Configurar modal para eliminar cliente - ESPECÍFICO PARA CLIENTES
    const btnEliminarCliente = document.querySelectorAll('.btnEliminarCliente');
    if (btnEliminarCliente.length > 0 && window.location.pathname.includes('/clientes')) {
        btnEliminarCliente.forEach(button => {
            button.addEventListener('click', function() {
                const id = this.getAttribute('data-id');
                const formEliminar = document.getElementById('formEliminarCliente');
                
                // Asegurarse de que la URL comience con /clientes/
                formEliminar.action = '/clientes/eliminar/' + id;
                
                // Verificar si estamos en una página de clientes
                console.log('Configurando eliminación de cliente: ' + formEliminar.action);
            });
        });
    }
    
    // Validación de email
    const emailInput = document.getElementById('email');
    if (emailInput) {
        emailInput.addEventListener('input', function() {
            const email = this.value;
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            
            if (email && !emailRegex.test(email)) {
                this.setCustomValidity('Por favor ingrese un email válido');
            } else {
                this.setCustomValidity('');
            }
        });
    }
    
    // Validación de teléfono
    const telefonoInput = document.getElementById('telefono');
    if (telefonoInput) {
        telefonoInput.addEventListener('input', function() {
            const telefono = this.value;
            const telefonoRegex = /^[0-9+\-\s()]{7,15}$/;
            
            if (telefono && !telefonoRegex.test(telefono)) {
                this.setCustomValidity('Por favor ingrese un número de teléfono válido');
            } else {
                this.setCustomValidity('');
            }
        });
    }
});