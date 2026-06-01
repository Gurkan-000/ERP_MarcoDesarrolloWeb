
const BASE_URL = 'http://localhost:8080/api';

const toastContainer = document.getElementById('toast-container');
const mostrarToast = (mensaje, tipo = 'info') => {
    const toast = document.createElement('div');

    toast.className = `toast toast-${tipo}`;
    toast.textContent = mensaje;

    toastContainer.appendChild(toast);

    setTimeout(() => {
        toast.classList.add('toast-hide');

        setTimeout(() => toast.remove(), 300);
    }, 4000);
};

const tbodyUsuario = document.getElementById("tbodyUsuario");
const cargarTablaUsuarios = async () => {

    try {

        const response = await fetch(`${BASE_URL}/usuario/listarUsuarios`);

        if (!response.ok) {
            mostrarToast("Ocurrio un error", "error");
        } else {
            const data = await response.json();
            tbodyUsuario.innerHTML = "";

            data.forEach(usuario => {

                const rowTBody = document.createElement("tr");

                rowTBody.dataset.id = usuario.idUsuario;

                rowTBody.innerHTML = `
                    <td>${usuario.nombre}</td>
                    <td>
                        <span class="badge">${usuario.rol}</span>
                    </td>
            `;
                tbodyUsuario.appendChild(rowTBody);

            });

        }

    } catch (error) {
        console.log(error);
    }

}

const crearUsuario = async (requestUsuario) => {

    try {

        const response = await fetch(`${BASE_URL}/usuario/crearUsuario`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(requestUsuario)
        });

        const data = await response.json();

        if (!response.ok) {
            data.mensajes.forEach(m => {
                mostrarToast(m, "error");
            });
            console.log(requestUsuario);
        } else {
            mostrarToast("Usuario creado con éxito", "success");
            const rowTBody = document.createElement("tr");

            rowTBody.dataset.id = data.idUsuario;

            rowTBody.innerHTML = `
                    <td>${data.nombre}</td>
                    <td>
                        <span class="badge">${data.rol}</span>
                    </td>
            `;
            tbodyUsuario.appendChild(rowTBody);

        }

    } catch (error) {
        console.log(error);
    }

}

const btnGuardarUsuario = document.getElementById("btnGuardarUsuario");
btnGuardarUsuario.addEventListener("click", async () => {

    const txtUsuario = document.getElementById("txtUsuario");
    const txtContrasena = document.getElementById("txtContrasena");
    const comboRol = document.getElementById("comboRol");

    const requestUsuario = {
        nombre: txtUsuario.value,
        contrasena: txtContrasena.value,
        rol: comboRol.value
    }

    console.log(requestUsuario);

    await crearUsuario(requestUsuario);

    document.getElementById("txtUsuario").value = "";
    document.getElementById("txtContrasena").value = "";
    document.getElementById("comboRol").selectedIndex = 0;
});

const verificarSesionActiva = async () => {

    try {

        const response = await fetch(`${BASE_URL}/usuario/sesionActiva`);
        const data = await response.json();

        if (!response.ok) {
            return null;
        } else {
            return data;
        }

    } catch (error) {
        console.log(error);
    }

}

const cargarSecciones = (sesionActiva) => {

    if (sesionActiva.rol === "Administrador") {
        return;
    }

    if (sesionActiva.rol === "Mesero") {
        document.getElementById("navSeccionCaja").classList.add("hidden");
        document.getElementById("navSeccionInventario").classList.add("hidden");
        document.getElementById("navSeccionCatalogo").classList.add("hidden");
        document.getElementById("navSeccionUsuario").classList.add("hidden");
        document.getElementById("navSeccionConfiguracion").classList.add("hidden");
    }

    if (sesionActiva.rol === "Cajero") {
        document.getElementById("navSeccionInventario").classList.add("hidden");
        document.getElementById("navSeccionCatalogo").classList.add("hidden");
        document.getElementById("navSeccionUsuario").classList.add("hidden");
        document.getElementById("navSeccionConfiguracion").classList.add("hidden");
    }

}

document.getElementById("btnCerrarSesion").addEventListener("click", async (e) => {
    e.preventDefault();

    try {

        const response = await fetch(`http://localhost:8080/api/usuario/cerrarSesion`, {
            method: 'PUT'
        });

        if (!response.ok) {
            console.log("Ocurrio un error");
        } else {
            window.location.href = 'login.html';
        }

    } catch (error) {
        console.log(error);
    }
});

document.addEventListener("DOMContentLoaded", async () => {

    const sesionActiva = await verificarSesionActiva();

    if (sesionActiva == null) {
        window.location.href = 'login.html';
        return;
    }

    document.getElementById("infoUsuario").textContent = sesionActiva.nombre;
    document.getElementById("infoRol").textContent = sesionActiva.rol;
    document.getElementById("infoAvatar").textContent = sesionActiva.rol.charAt(0).toUpperCase();

    cargarSecciones(sesionActiva);
    await cargarTablaUsuarios();

});