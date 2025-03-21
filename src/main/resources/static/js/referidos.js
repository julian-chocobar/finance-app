
		// Función para mostrar el modal
		function mostrarModal(modo, id = null, nombre = "", telefono = "", email = "", direccion = "") {
		    const form = document.getElementById("referidoForm");

		    if (modo === "crear") {
		        form.action = "/referidos"; // URL para crear un nuevo referido
		        form.method = "post";
		    } else if (modo === "editar" && id) {
		        form.action = "/referidos/editar/" + id; // URL con el ID en la ruta
		        form.method = "post";
		    }

		    // Llenar los campos del formulario con datos si es edición
		    document.getElementById("nombre").value = nombre;
		    document.getElementById("telefono").value = telefono;
		    document.getElementById("email").value = email;
		    document.getElementById("direccion").value = direccion;

		    // Mostrar el modal
		    document.getElementById("modal-overlay").style.display = "block";
		}


		// Función para ocultar el modal
		function ocultarModal() {
		    document.getElementById("modal-overlay").style.display = "none";
		}

        function cancelarEdicion() {
            document.getElementById("formulario-referido").style.display = "none";
        }
		
		function confirmarEliminacion(referidoId) {
		    const confirmar = confirm("¿Estás seguro de que deseas eliminar este referido?");
		    if (confirmar) {
		        fetch(`/referidos/${referidoId}`, {
		            method: 'DELETE',
		            headers: {
		                'Content-Type': 'application/json',
		            },
		        })
		        .then(response => {
		            if (response.ok) {
		                window.location.reload();
		            } else {
		                alert("Hubo un error al eliminar el referido.");
		            }
		        })
		        .catch(error => {
		            console.error("Error:", error);
		            alert("Hubo un error al eliminar el referido.");
		        });
		    }
		}
		
		function toggleSidebar() {
		    const sidebar = document.getElementById("sidebar");
		    const main = document.getElementById("main");
		    if (sidebar.style.left === "0px") {
		        sidebar.style.left = "-250px";
		        main.style.marginLeft = "0";
		    } else {
		        sidebar.style.left = "0px";
		        main.style.marginLeft = "250px";
		    }
		}