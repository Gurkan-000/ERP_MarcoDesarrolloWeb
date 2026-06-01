
const BASE_URL = "http://localhost:8080/api";

const detallesPedidoLlevar = [];
const detallesPedidoDelivery = [];
const detllesPedidoLocal = [];
const productosMap = new Map();

let tipoPedidoActivo = "LLEVAR";

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

const formatoMoneda = (valor) => `S/ ${Number(valor).toFixed(2)}`;

const obtenerCajaAbierta = async () => {

    try {

        const response = await fetch(`${BASE_URL}/caja/obtenerCajaAbierta`);
        const data = await response.json();

        if (!response.ok) {
            return null;
        }

        return data;


    } catch (error) {
        console.log(error);
    }

}

const cargarMesas = async () => {
    try {
        const response = await fetch(`${BASE_URL}/venta/listarMesas`);

        if (!response.ok) {
            console.log("Ocurrio un error");
        } else {
            const data = await response.json();

            const mesaGridVenta = document.getElementById("mesa-grid");
            if (!mesaGridVenta) return;
            mesaGridVenta.innerHTML = "";

            data.forEach((mesa) => {
                const botonMesa = document.createElement("button");
                const estadoMesa = mesa.estado === "OCUPADO" ? "Ocupada" : "Libre";

                botonMesa.id = "btnMesa";
                botonMesa.type = "button";
                botonMesa.classList.add("table-card");
                botonMesa.dataset.mesa = mesa.numero;
                botonMesa.dataset.estado = estadoMesa;
                botonMesa.dataset.idMesa = mesa.idMesa;
                botonMesa.innerHTML = `
                <span class="table-number">${mesa.numero}</span>
                <span>${estadoMesa}</span>
                `;

                mesaGridVenta.appendChild(botonMesa);
            });
        }
    } catch (error) {
        console.log(error);
    }
};

const cargarProductos = async () => {
    try {
        const response = await fetch(`${BASE_URL}/catalogo/listarProductos`);

        if (!response.ok) {
            console.log("Ocurrio un error");
        } else {
            const data = await response.json();

            const comboProductoPedido = document.getElementById("comboProductoPedido");
            comboProductoPedido.innerHTML = `
            <option value="" disabled selected>Seleccione un producto</option>
			`;

            data.forEach((producto) => {
                const option = document.createElement("option");
                option.value = producto.idProducto;
                option.textContent = producto.nombreProducto;

                productosMap.set(producto.idProducto, producto);
                comboProductoPedido.appendChild(option);
            });
        }
    } catch (error) {
        console.log(error);
    }
};

const validarDetallePedido = async (requestDetalle) => {
    try {
        const response = await fetch(`${BASE_URL}/venta/validarDetallePedido`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(requestDetalle)
        });

        const data = await response.json();

        if (!response.ok) {
            data.mensajes.forEach(m => {
                mostrarToast(m, "error");
            });
            return false;
        }

        return Boolean(data);
    } catch (error) {
        console.log(error);
        return false;
    }
};

const cobrarPedido = async (requestPedido) => {
    try {
        const response = await fetch(`${BASE_URL}/venta/cobrarPedido`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(requestPedido)
        });

        if (!response.ok) {
            const data = await response.json();
            data.mensajes.forEach(m => {
                mostrarToast(m, "error");
            });
            return false;
        }

        return true;
    } catch (error) {
        mostrarToast("Ocurrio un error inesperado", "error");
        return false;
    }
};


const renderDetallesPedido = () => {
    const detalles = tipoPedidoActivo === "DELIVERY" ? detallesPedidoDelivery : detallesPedidoLlevar;

    const tbodyPedidosAgregados = document.getElementById("tbodyPedidosAgregados");
    const txtTotalPedido = document.getElementById("txtTotalPedido");

    tbodyPedidosAgregados.innerHTML = "";

    let total = 0;

    detalles.forEach((detalle) => {
        const fila = document.createElement("tr");
        fila.innerHTML = `
        <td>${detalle.nombreProducto}</td>
        <td>${detalle.cantidad}</td>
        <td>${formatoMoneda(detalle.precioUnitario)}</td>
        <td>${formatoMoneda(detalle.total)}</td>
        `;

        total += detalle.total;
        tbodyPedidosAgregados.appendChild(fila);
    });

    txtTotalPedido.textContent = formatoMoneda(total);

    const tieneDetalles = detalles.length > 0;
    const tfootTotalPedido = document.getElementById("tfootTotalPedido");

    if (detalles.length > 0) {
        tfootTotalPedido.classList.remove("hidden");
        btnRegistrarPagoPedido.classList.remove("hidden");
    } else {
        tfootTotalPedido.classList.add("hidden");
        btnRegistrarPagoPedido.classList.add("hidden");
    }

};


const btnAgregarPedido = document.getElementById("btnAgregarPedido");
btnAgregarPedido.addEventListener("click", async (e) => {
    e.preventDefault();

    const comboProductoPedido = document.getElementById("comboProductoPedido");
    const txtCantidadPedido = document.getElementById("txtCantidadPedido");

    const requestDetalle = {
        idProducto: comboProductoPedido.value,
        cantidad: Number(txtCantidadPedido.value)
    };

    const detalleValido = await validarDetallePedido(requestDetalle);

    if (!detalleValido) {
        mostrarToast("La cantidad ingresada supera el stock", "error");
        return;
    }

    const producto = productosMap.get(requestDetalle.idProducto);
    const precioUnitario = Number(producto.precio);
    const total = precioUnitario * requestDetalle.cantidad;

    const detalle = {
        idProducto: comboProductoPedido.value,
        nombreProducto: producto.nombreProducto,
        cantidad: Number(txtCantidadPedido.value),
        precioUnitario,
        total
    };

    const detallesPorTipo = tipoPedidoActivo === "DELIVERY" ? detallesPedidoDelivery : detallesPedidoLlevar;
    detallesPorTipo.push(detalle);

    txtCantidadPedido.value = "";
    comboProductoPedido.selectedIndex = 0;
    renderDetallesPedido(tipoPedidoActivo);
});


const btnRegistrarPagoPedido = document.getElementById("btnRegistrarPagoPedido");
btnRegistrarPagoPedido.addEventListener("click", async (e) => {
    e.preventDefault();

    const comboMetodoPagoPedido = document.getElementById("comboMetodoPagoPedido");
    const detalles = tipoPedidoActivo === "DELIVERY" ? detallesPedidoDelivery : detallesPedidoLlevar;

    const requestPedido = {
        metodoPago: comboMetodoPagoPedido.value,
        tipoPedido: tipoPedidoActivo,
        detalles: detalles.map((detalle) => ({
            idProducto: detalle.idProducto,
            cantidad: detalle.cantidad
        }))
    };

    const pagoCorrecto = await cobrarPedido(requestPedido);
    detalles.length = 0;
    renderDetallesPedido(tipoPedidoActivo);
});


const divPedidoRegistro = document.getElementById("divPedidoRegistro");
const btnTabLlevar = document.getElementById("btnTabLlevar");
const btnTabDelivery = document.getElementById("btnTabDelivery");

const divPedidoLlevar = document.getElementById("divPedidoLlevar");
btnTabLlevar.addEventListener("click", () => {
    divPedidoLlevar.appendChild(divPedidoRegistro);
    divPedidoRegistro.classList.remove("hidden");
    tipoPedidoActivo = "LLEVAR";
    renderDetallesPedido();
});

const divPedidoDelivery = document.getElementById("divPedidoDelivery");
btnTabDelivery.addEventListener("click", () => {
    divPedidoDelivery.appendChild(divPedidoRegistro);
    divPedidoRegistro.classList.remove("hidden");
    tipoPedidoActivo = "DELIVERY";
    renderDetallesPedido();
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

document.addEventListener("DOMContentLoaded", async (e) => {

    e.preventDefault();
    const sesionActiva = await verificarSesionActiva();

    if (sesionActiva == null) {
        window.location.href = 'login.html';
        return;
    }

    document.getElementById("infoUsuario").textContent = sesionActiva.nombre;
    document.getElementById("infoRol").textContent = sesionActiva.rol;
    document.getElementById("infoAvatar").textContent = sesionActiva.rol.charAt(0).toUpperCase();

    cargarSecciones(sesionActiva);

    const cajaAbierta = await obtenerCajaAbierta();

    if (cajaAbierta) {
        cargarMesas();
        cargarProductos();
    } else {
        const cajaCerradaModal = document.getElementById("caja-cerrada-modal");
        const cajaCerradaIr = document.getElementById("caja-cerrada-ir");
        cajaCerradaModal.classList.remove("hidden");
        cajaCerradaIr.addEventListener("click", () => {
            window.location.href = "caja.html";
        });

    }
});


