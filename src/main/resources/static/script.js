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
        document.getElementById("loginResult").innerText = `Error: ${result.error}`;
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

        const text = await response.text();
        console.log("Respuesta de la API:", text);

        const proyectos = JSON.parse(text);
        console.log("Proyectos obtenidos:", proyectos);

        // Esperar a que el DOM esté listo
        document.addEventListener("DOMContentLoaded", () => {
            const select = document.getElementById("tareaProyectoId");
            if (!select) {
                console.error("No se encontró el select de proyectos en tareas.");
                return;
            }

            select.innerHTML = '<option value="">Selecciona un Proyecto</option>'; // Limpia opciones anteriores

            proyectos.forEach(proyecto => {
                let option = document.createElement("option");
                option.value = proyecto.id;
                option.textContent = proyecto.nombre;
                select.appendChild(option);
            });
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

        if (!response.ok) {
            throw new Error(`Error al obtener proyectos: ${response.status}`);
        }

        const proyectos = await response.json();
        const listaProyectos = document.getElementById("proyectosLista");
        listaProyectos.textContent = JSON.stringify(proyectos, null, 2); // Formato bonito
    } catch (error) {
        console.error(error);
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
async function getTareas() {
    try {
        const response = await fetch(`${apiBaseUrl}/tareas`, {
            headers: { "Authorization": `Bearer ${token}` }
        });

        if (!response.ok) {
            throw new Error(`Error al obtener tareas: ${response.status}`);
        }

        const tareas = await response.json();
        console.log("Tareas obtenidas:", tareas);

        const listaTareas = document.getElementById("tareasLista");
        if (listaTareas) {
            listaTareas.textContent = JSON.stringify(tareas, null, 2);
        }
    } catch (error) {
        console.error("Error en getTareas():", error);
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
