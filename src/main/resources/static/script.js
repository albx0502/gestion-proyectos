const apiBaseUrl = "http://localhost:8080/api";
let token = "";

// Manejo de autenticación
async function register() {
    const username = document.getElementById("regUsername").value;
    const password = document.getElementById("regPassword").value;

    if (password.length < 6 || !/(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/.test(password)) {
        document.getElementById("regResult").innerText = "La contraseña debe tener al menos 6 caracteres, una mayúscula, una minúscula y un número.";
        return;
    }

    const response = await fetch(`${apiBaseUrl}/auth/register`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, password })
    });

    const result = await response.json();
    document.getElementById("regResult").innerText = response.ok ? result.message : `Error: ${result.error}`;
}

async function login() {
    const username = document.getElementById("loginUsername").value;
    const password = document.getElementById("loginPassword").value;

    const response = await fetch(`${apiBaseUrl}/auth/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, password })
    });

    const result = await response.json();
    if (response.ok) {
        token = result.token;
        document.getElementById("loginResult").innerText = "Login exitoso.";
        document.getElementById("authContainer").classList.add("hidden");
        document.getElementById("appContainer").classList.remove("hidden");

        // Obtener y almacenar el usuario autenticado
        const usuarioId = await obtenerUsuarioId();
        sessionStorage.setItem("usuarioId", usuarioId); // Guardar en sesión

        cargarProyectos(); // Cargar proyectos en el select de tareas

    } else {
        document.getElementById("loginResult").innerText = `Error: Credenciales Incorrectas`;
    }
}
async function obtenerUsuarioId() {
    if (!token) {
        console.error("Token no disponible");
        return null;
    }

    const response = await fetch(`${apiBaseUrl}/usuarios/me`, {
        headers: { "Authorization": `Bearer ${token}` }
    });

    if (response.ok) {
        const usuario = await response.json();
        return usuario.id; // Devuelve el ID del usuario autenticado
    } else {
        console.error("No se pudo obtener el usuario, error:", response.status);
        return null;
    }
}



function logout() {
    token = "";
    document.getElementById("authContainer").classList.remove("hidden");
    document.getElementById("appContainer").classList.add("hidden");
}

// Navegación entre secciones
function mostrarSeccion(id) {
    document.querySelectorAll(".seccion").forEach(sec => sec.classList.add("hidden"));
    document.getElementById(id).classList.remove("hidden");

    // Cargar proyectos al ir a la sección de tareas
    if (id === "tareas") {
        cargarProyectos();
    }
}

// Cargar proyectos en select de tareas
async function cargarProyectos() {
    if (!token) {
        console.error("Token no disponible");
        return;
    }

    try {
        const response = await fetch(`${apiBaseUrl}/proyectos`, {
            headers: { "Authorization": `Bearer ${token}` }
        });

        if (!response.ok) throw new Error(`Error al obtener proyectos: ${response.status}`);

        const proyectos = await response.json();
        const select = document.getElementById("tareaProyectoId");

        if (!select) {
            console.error("No se encontró el select de proyectos en tareas.");
            return;
        }

        select.innerHTML = '<option value="">Selecciona un Proyecto</option>';
        proyectos.forEach(proyecto => {
            let option = document.createElement("option");
            option.value = proyecto.id;
            option.textContent = proyecto.nombre;
            select.appendChild(option);
        });
    } catch (error) {
        console.error("Error en cargarProyectos():", error);
    }
}

async function getProyectos() {
    try {
        const response = await fetch(`${apiBaseUrl}/proyectos`, {
            headers: { "Authorization": `Bearer ${token}` }
        });

        if (!response.ok) throw new Error(`Error al obtener proyectos: ${response.status}`);

        const proyectos = await response.json();
        const listaProyectos = document.getElementById("proyectosLista");
        listaProyectos.innerHTML = ""; // Limpiar antes de agregar nuevas tarjetas

        proyectos.forEach(proyecto => {
            // Contenedor de la tarjeta
            const card = document.createElement("div");
            card.classList.add("proyecto-card");

            // Contenido de la tarjeta
            card.innerHTML = `
                <h3>${proyecto.nombre}</h3>
                <p><strong>ID:</strong> ${proyecto.id}</p>
                <p><strong>Descripción:</strong> ${proyecto.descripcion}</p>
                <p><strong>Fecha de inicio:</strong> ${proyecto.fechaInicio}</p>
                <p><strong>Estado:</strong> ${proyecto.estado}</p>
                <p><strong>Usuario ID:</strong> ${proyecto.usuarioId}</p>
            `;

            // Botón de editar
            const btnEditar = document.createElement("button");
            btnEditar.textContent = "Editar";
            btnEditar.classList.add("btn-editar");
            btnEditar.onclick = () => editarProyecto(proyecto.id, proyecto.nombre, proyecto.descripcion, proyecto.fechaInicio, proyecto.estado);

            // Botón de eliminar
            const btnEliminar = document.createElement("button");
            btnEliminar.textContent = "Eliminar";
            btnEliminar.classList.add("btn-eliminar");
            btnEliminar.onclick = () => eliminarProyecto(proyecto.id);

            // Agregar botones a la tarjeta
            card.appendChild(btnEditar);
            card.appendChild(btnEliminar);


            // Agregar tarjeta a la lista
            listaProyectos.appendChild(card);
        });

    } catch (error) {
        console.error("Error en getProyectos():", error);
    }
}



// Gestión de proyectos
async function crearProyecto() {
    const usuarioId = await obtenerUsuarioId(); // Obtener el ID del usuario autenticado

    const proyecto = {
        nombre: document.getElementById("proyectoNombre").value,
        descripcion: document.getElementById("proyectoDescripcion").value,
        fechaInicio: document.getElementById("proyectoFecha").value,
        estado: document.getElementById("proyectoEstado").value,
        usuarioId: usuarioId
    };

    const response = await fetch(`${apiBaseUrl}/proyectos`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify(proyecto)
    });

    document.getElementById("proyectoResult").innerText = response.ok ? "Proyecto creado." : "Error al crear proyecto.";
}

async function editarProyecto(id, nombre, descripcion, fechaInicio, estado) {
    const nuevoNombre = prompt("Nuevo nombre:", nombre);
    const nuevaDescripcion = prompt("Nueva descripción:", descripcion);
    const nuevaFecha = prompt("Nueva fecha de inicio (YYYY-MM-DD):", fechaInicio);
    const nuevoEstado = prompt("Nuevo estado (ACTIVO, EN_PROGRESO, FINALIZADO):", estado);

    if (!nuevoNombre || !nuevaDescripcion || !nuevaFecha || !nuevoEstado) {
        alert("Todos los campos son obligatorios.");
        return;
    }

    const proyectoActualizado = {
        nombre: nuevoNombre,
        descripcion: nuevaDescripcion,
        fechaInicio: nuevaFecha,
        estado: nuevoEstado
    };

    try {
        const response = await fetch(`${apiBaseUrl}/proyectos/${id}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            },
            body: JSON.stringify(proyectoActualizado)
        });

        if (response.ok) {
            alert("Proyecto actualizado.");
            getProyectos();
        } else {
            alert("Error al actualizar el proyecto.");
        }
    } catch (error) {
        console.error("Error en editarProyecto():", error);
    }
}

async function eliminarProyecto(id) {
    if (!confirm("¿Estás seguro de que deseas eliminar este proyecto?")) {
        return;
    }

    try {
        const response = await fetch(`${apiBaseUrl}/proyectos/${id}`, {
            method: "DELETE",
            headers: { "Authorization": `Bearer ${token}` }
        });

        if (response.ok) {
            alert("Proyecto eliminado.");
            cargarProyectos();
        } else {
            alert("No se pudo eliminar el proyecto. Asegúrate de que no tenga tareas asociadas.");
        }
    } catch (error) {
        console.error("Error en eliminarProyecto():", error);
    }
}

// Obtener lista de tareas
async function getTareas() {
    try {
        const response = await fetch(`${apiBaseUrl}/tareas`, {
            headers: { "Authorization": `Bearer ${token}` }
        });

        if (!response.ok) throw new Error(`Error al obtener tareas: ${response.status}`);

        const tareas = await response.json();
        const listaTareas = document.getElementById("tareasLista");
        listaTareas.innerHTML = ""; // Limpiar antes de agregar nuevas tarjetas

        tareas.forEach(tarea => {
            // Contenedor de la tarjeta
            const card = document.createElement("div");
            card.classList.add("tarea-card");

            // Contenido de la tarjeta
            card.innerHTML = `
                <h3>${tarea.nombre}</h3>
                <p><strong>ID:</strong> ${tarea.id}</p>
                <p><strong>Descripción:</strong> ${tarea.descripcion}</p>
                <p><strong>Fecha límite:</strong> ${tarea.fechaLimite}</p>
                <p><strong>Estado:</strong> ${tarea.estado}</p>
                <p><strong>Proyecto ID:</strong> ${tarea.proyectoId}</p>
            `;

            // Botón de editar
            const btnEditar = document.createElement("button");
            btnEditar.textContent = "Editar";
            btnEditar.classList.add("btn-editar");
            btnEditar.onclick = () => editarTarea(tarea.id, tarea.titulo, tarea.descripcion, tarea.fechaLimite, tarea.estado);


            // Botón de eliminar
            const btnEliminar = document.createElement("button");
            btnEliminar.textContent = "Eliminar";
            btnEliminar.classList.add("btn-eliminar");
            btnEliminar.onclick = () => eliminarTarea(tarea.id);

            // Agregar botón a la tarjeta
            card.appendChild(btnEditar)
            card.appendChild(btnEliminar);

            // Agregar tarjeta a la lista
            listaTareas.appendChild(card);
        });

    } catch (error) {
        console.error("Error en getTareas():", error);
    }
}

async function cargarTareas() {
    if (!token) {
        console.error("Token no disponible");
        return;
    }

    try {
        const response = await fetch(`${apiBaseUrl}/tareas`, {
            headers: { "Authorization": `Bearer ${token}` }
        });

        if (!response.ok) throw new Error(`Error al obtener tareas: ${response.status}`);

        const tareas = await response.json();
        const select = document.getElementById("proyectoTareaId");

        if (!select) {
            console.error("No se encontró el select de tareas en proyectos.");
            return;
        }

        select.innerHTML = '<option value="">Selecciona una Tarea</option>';
        tareas.forEach(tarea => {
            let option = document.createElement("option");
            option.value = tarea.id;
            option.textContent = tarea.titulo;
            select.appendChild(option);
        });

    } catch (error) {
        console.error("Error en cargarTareas():", error);
    }
}




// Gestión de tareas
async function crearTarea() {
    const tarea = {
        titulo: document.getElementById("tareaTitulo").value,
        descripcion: document.getElementById("tareaDescripcion").value,
        fechaLimite: document.getElementById("tareaFecha").value,
        estado: document.getElementById("tareaEstado").value,
        proyectoId: document.getElementById("tareaProyectoId").value
    };

    const response = await fetch(`${apiBaseUrl}/tareas`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify(tarea)
    });

    document.getElementById("tareaResult").innerText = response.ok ? "Tarea creada." : "Error al crear tarea.";
}
async function editarTarea(id, titulo, descripcion, fechaLimite, estado) {
    const nuevoTitulo = prompt("Nuevo título:", titulo);
    const nuevaDescripcion = prompt("Nueva descripción:", descripcion);
    const nuevaFechaLimite = prompt("Nueva fecha límite (YYYY-MM-DD):", fechaLimite);
    const nuevoEstado = prompt("Nuevo estado (PENDIENTE, EN_CURSO, COMPLETADA):", estado);

    if (!nuevoTitulo || !nuevaDescripcion || !nuevaFechaLimite || !nuevoEstado) {
        alert("Todos los campos son obligatorios.");
        return;
    }

    const tareaActualizada = {
        titulo: nuevoTitulo,
        descripcion: nuevaDescripcion,
        fechaLimite: nuevaFechaLimite,
        estado: nuevoEstado
    };

    try {
        const response = await fetch(`${apiBaseUrl}/tareas/${id}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            },
            body: JSON.stringify(tareaActualizada)
        });

        if (response.ok) {
            alert("Tarea actualizada.");
            getTareas();
        } else {
            alert("Error al actualizar la tarea.");
        }
    } catch (error) {
        console.error("Error en editarTarea():", error);
    }
}

async function eliminarTarea(id) {
    if (!confirm("¿Estás seguro de que deseas eliminar esta tarea?")) {
        return;
    }

    try {
        const response = await fetch(`${apiBaseUrl}/tareas/${id}`, {
            method: "DELETE",
            headers: { "Authorization": `Bearer ${token}` }
        });

        if (response.ok) {
            alert("Tarea eliminada.");
            cargarTareas();
        } else {
            alert("No se pudo eliminar la tarea.");
        }
    } catch (error) {
        console.error("Error en eliminarTarea():", error);
    }
}

