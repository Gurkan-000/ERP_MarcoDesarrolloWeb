const BASE_URL = "http://localhost:8080/api/caja";

// ─── Estado local ────────────────────────────────────────────────────────────
let idCajaActual = null; 

// ─── Referencias al DOM ──────────────────────────────────────────────────────
const divCajaTurnoCerrado = document.getElementById("divCajaTurnoCerrado");
const divCajaTurnoAbierto = document.getElementById("divCajaTurnoAbierto");
const btnCerrarTurno      = document.getElementById("btnCerrarTurno");
const btnAbrirTurno       = document.getElementById("btnAbrirTurno");

// ─── Helpers ─────────────────────────────────────────────────────────────────

/** Muestra la UI de turno abierto y vuelca los datos de la caja */
function mostrarTurnoAbierto(caja) {
    idCajaActual = caja.idCaja;
    divCajaTurnoCerrado.classList.add("hidden");
    divCajaTurnoAbierto.classList.remove("hidden");

    document.querySelector(".stat-card h4.text-success").textContent =
        `S/ ${Number(caja.montoActual).toFixed(2)}`;
    document.querySelector(".monto-inicial").textContent =
        `Monto inicial: S/ ${Number(caja.montoInicial).toFixed(2)}`;
}

/** Muestra la UI de turno cerrado */
function mostrarTurnoCerrado(ultimoMonto = null) {
    idCajaActual = null;
    divCajaTurnoAbierto.classList.add("hidden");
    divCajaTurnoCerrado.classList.remove("hidden");

    if (ultimoMonto !== null) {
        document.querySelector(".stat-card h4.text-info").textContent =
            `S/ ${Number(ultimoMonto).toFixed(2)}`;
    }
}

/** Agrega una fila al listado de movimientos del turno */
function agregarMovimientoAlDOM(mov) {
    const lista = document.querySelector(".list");

    // Quitar el item de placeholder si existe
    const placeholder = lista.querySelector(".list-item");
    if (placeholder && placeholder.dataset.placeholder === "true") {
        placeholder.remove();
    }

    const item = document.createElement("div");
    item.classList.add("list-item");
    item.innerHTML = `
        <div>
            <p class="title">${mov.concepto}</p>
            <span class="badge">${mov.metodo}</span>
            <span class="badge ${mov.tipo === "EGRESO" ? "badge-orange" : "badge-green"}">${mov.tipo}</span>
        </div>
        <strong>${mov.tipo === "INGRESO" ? "+" : "-"} S/ ${Number(mov.monto).toFixed(2)}</strong>
    `;
    lista.appendChild(item);
}

// ─── 1. Obtener caja por ID ───────────────────────────────────────────────────
const obtenerCaja = async (idCaja) => {
    try {
        const response = await fetch(`${BASE_URL}/${idCaja}`);
        if (!response.ok) {
            console.error("Error al obtener caja:", await response.json());
            return null;
        }
        return await response.json();
    } catch (error) {
        console.error("Error de red al obtener caja:", error);
        return null;
    }
};

// ─── 2. Obtener movimientos por ID de caja ────────────────────────────────────
const obtenerMovimientos = async (idCaja) => {
    try {
        const response = await fetch(`${BASE_URL}/movimientos/${idCaja}`);
        if (!response.ok) {
            console.error("Error al obtener movimientos:", await response.json());
            return [];
        }
        return await response.json();
    } catch (error) {
        console.error("Error de red al obtener movimientos:", error);
        return [];
    }
};

// ─── 3. Aperturar caja ────────────────────────────────────────────────────────
const aperturarCaja = async (montoInicial) => {
    try {
        const response = await fetch(`${BASE_URL}/aperturarCaja`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ montoInicial })
        });

        const data = await response.json();

        if (!response.ok) {
            console.error("Error al aperturar caja:", data);
            alert("Error al abrir turno: " + (data.mensaje ?? JSON.stringify(data)));
            return null;
        }

        return data;
    } catch (error) {
        console.error("Error de red al aperturar caja:", error);
        return null;
    }
};

// ─── 4. Cerrar caja ───────────────────────────────────────────────────────────
const cerrarCaja = async (idCaja) => {
    try {
        const response = await fetch(`${BASE_URL}/cerrarCaja/${idCaja}`, {
            method: "PUT"
        });

        const data = await response.json();

        if (!response.ok) {
            console.error("Error al cerrar caja:", data);
            alert("Error al cerrar turno: " + (data.mensaje ?? JSON.stringify(data)));
            return null;
        }

        return data; // { ultimoMonto }
    } catch (error) {
        console.error("Error de red al cerrar caja:", error);
        return null;
    }
};

// ─── 5. Registrar movimiento ──────────────────────────────────────────────────
const registrarMovimiento = async (idCaja, requestMovimiento) => {
    try {
        const response = await fetch(`${BASE_URL}/registrarMovimiento/${idCaja}`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(requestMovimiento)
        });

        const data = await response.json();

        if (!response.ok) {
            console.error("Error al registrar movimiento:", data);
            alert("Error al registrar movimiento: " + (data.mensaje ?? JSON.stringify(data)));
            return null;
        }

        return data;
    } catch (error) {
        console.error("Error de red al registrar movimiento:", error);
        return null;
    }
};

// ─── Cargar estado inicial ────────────────────────────────────────────────────
// Recupera el idCaja guardado en sessionStorage (se pone al aperturar)
const cargarEstadoInicial = async () => {
    const idGuardado = sessionStorage.getItem("idCaja");
    if (!idGuardado) {
        mostrarTurnoCerrado();
        return;
    }

    const caja = await obtenerCaja(idGuardado);
    if (!caja || caja.estado === "CERRADA") {
        mostrarTurnoCerrado();
        sessionStorage.removeItem("idCaja");
        return;
    }

    mostrarTurnoAbierto(caja);

    // Cargar movimientos del turno
    const movimientos = await obtenerMovimientos(idGuardado);
    movimientos.forEach(agregarMovimientoAlDOM);
};

// ─── Evento: Abrir turno ──────────────────────────────────────────────────────
btnAbrirTurno.addEventListener("click", async (e) => {
    e.preventDefault();

    const inputMonto = document.querySelector("#divCajaTurnoCerrado input[type='number']");
    const montoInicial = parseFloat(inputMonto.value);

    if (isNaN(montoInicial) || montoInicial < 0) {
        alert("Ingresa un monto inicial válido.");
        return;
    }

    const caja = await aperturarCaja(montoInicial);
    if (!caja) return;

    sessionStorage.setItem("idCaja", caja.idCaja);
    inputMonto.value = "";
    mostrarTurnoAbierto(caja);
});

// ─── Evento: Cerrar turno ─────────────────────────────────────────────────────
btnCerrarTurno.addEventListener("click", async (e) => {
    e.preventDefault();

    if (!idCajaActual) {
        alert("No hay caja abierta.");
        return;
    }

    const resultado = await cerrarCaja(idCajaActual);
    if (!resultado) return;

    sessionStorage.removeItem("idCaja");
    mostrarTurnoCerrado(resultado.ultimoMonto);

    // Limpiar la lista de movimientos
    const lista = document.querySelector(".list");
    lista.innerHTML = `
        <div class="list-item" data-placeholder="true">
            <div><p class="title">Sin movimientos</p></div>
        </div>
    `;
});

// ─── Evento: Registrar movimiento ─────────────────────────────────────────────
const formMovimiento = document.querySelector("#divCajaTurnoAbierto .inline-form");
formMovimiento.addEventListener("submit", async (e) => {
    e.preventDefault();

    if (!idCajaActual) {
        alert("No hay caja abierta.");
        return;
    }

    const concepto = formMovimiento.querySelector("input[type='text']").value.trim();
    const tipo     = formMovimiento.querySelector("select:nth-of-type(1)").value;   // INGRESO | EGRESO
    const metodo   = formMovimiento.querySelector("select:nth-of-type(2)").value;   // EFECTIVO | TARJETA | YAPE
    const monto    = parseFloat(formMovimiento.querySelector("input[type='number']").value);

    if (!concepto || isNaN(monto) || monto <= 0) {
        alert("Completa todos los campos correctamente.");
        return;
    }

    const requestMovimiento = { concepto, tipo, metodo, monto };

    const movimiento = await registrarMovimiento(idCajaActual, requestMovimiento);
    if (!movimiento) return;

    agregarMovimientoAlDOM(movimiento);
    formMovimiento.reset();

    // Actualizar el monto mostrado
    const caja = await obtenerCaja(idCajaActual);
    if (caja) {
        document.querySelector(".stat-card h4.text-success").textContent =
            `S/ ${Number(caja.montoActual).toFixed(2)}`;
    }
});

// ─── Arranque ─────────────────────────────────────────────────────────────────
cargarEstadoInicial();