const apiBaseUrl = "http://localhost:8080/api/auth";
let token = "";

async function register() {
    const username = document.getElementById("regUsername").value;
    const password = document.getElementById("regPassword").value;

    const response = await fetch(`${apiBaseUrl}/register`, {
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

    const response = await fetch(`${apiBaseUrl}/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, password })
    });

    const result = await response.json();
    if (response.ok) {
        token = result.token;
        document.getElementById("loginResult").innerText = "Login exitoso. Token guardado.";
    } else {
        document.getElementById("loginResult").innerText = `Error: ${result.error}`;
    }
}

async function getProyectos() {
    const response = await fetch("http://localhost:8080/api/proyectos", {
        method: "GET",
        headers: {
            "Authorization": `Bearer ${token}`
        }
    });

    const result = await response.json();
    document.getElementById("apiResponse").innerText = JSON.stringify(result, null, 2);
}

async function crearProyecto() {
    if (!token) {
        alert("Debes iniciar sesión primero.");
        return;
    }

    const nombre = document.getElementById("proyectoNombre").value;
    const descripcion = document.getElementById("proyectoDescripcion").value;
    const fechaInicio = document.getElementById("proyectoFecha").value;
    const estado = document.getElementById("proyectoEstado").value;

    const response = await fetch("http://localhost:8080/api/proyectos", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify({
            nombre,
            descripcion,
            fechaInicio,
            estado
        })
    });

    if (!response.ok) {
        const errorText = await response.text();
        document.getElementById("proyectoResult").innerText = `Error ${response.status}: ${errorText}`;
        return;
    }

    const result = await response.json();
    document.getElementById("proyectoResult").innerText = `Proyecto creado: ${JSON.stringify(result, null, 2)}`;
}


async function crearTarea() {
    if (!token) {
        alert("Debes iniciar sesión primero.");
        return;
    }

    const titulo = document.getElementById("tareaTitulo").value;
    const descripcion = document.getElementById("tareaDescripcion").value;
    const fechaLimite = document.getElementById("tareaFecha").value;
    const estado = document.getElementById("tareaEstado").value;
    const proyectoId = document.getElementById("tareaProyectoId").value;

    const response = await fetch("http://localhost:8080/api/tareas", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify({
            titulo,
            descripcion,
            fechaLimite,
            estado,
            proyectoId
        })
    });

    if (!response.ok) {
        const errorText = await response.text();
        document.getElementById("tareaResult").innerText = `Error ${response.status}: ${errorText}`;
        return;
    }

    const result = await response.json();
    document.getElementById("tareaResult").innerText = `Tarea creada: ${JSON.stringify(result, null, 2)}`;
}


