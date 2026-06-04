
const BASE_URL = "http://localhost:8080/api";

const detallesPedidoLlevar = new Map();
const detallesPedidoDelivery = new Map();
const detallesPedidoLocal = new Map();
const productosMap = new Map();

let tipoPedidoActivo = "LLEVAR";
let MesaActiva = null;

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

                detallesPedidoLocal.set(mesa.idMesa, new Map());
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
            const comboProductoMesa = document.getElementById("comboProductoMesa"); // Capturamos el nuevo select

            const optionDefault = `<option value="" disabled selected>Seleccione un producto</option>`;
            comboProductoPedido.innerHTML = optionDefault;
            if (comboProductoMesa) comboProductoMesa.innerHTML = optionDefault;

            data.forEach((producto) => {
                const option = document.createElement("option");
                option.value = producto.idProducto;
                option.textContent = producto.nombreProducto;

                productosMap.set(producto.idProducto, producto);

                comboProductoPedido.appendChild(option.cloneNode(true));
                if (comboProductoMesa) comboProductoMesa.appendChild(option);
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
    const tfootTotalPedido = document.getElementById("tfootTotalPedido");

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

    if (detalles.size > 0) {
        tfootTotalPedido.classList.remove("hidden");
        btnRegistrarPagoPedido.classList.remove("hidden");
    } else {
        tfootTotalPedido.classList.add("hidden");
        btnRegistrarPagoPedido.classList.add("hidden");
    }
};


const renderDetallesMesa = () => {
    const tbodyPedidosMesaAgregados = document.getElementById("tbodyPedidosMesaAgregados");
    const txtTotalPedidoMesa = document.getElementById("txtTotalPedidoMesa");
    const tfootTotalPedidoMesa = document.getElementById("tfootTotalPedidoMesa");

    tbodyPedidosMesaAgregados.innerHTML = "";

    if (!MesaActiva) {
        txtTotalPedidoMesa.textContent = formatoMoneda(0);
        tfootTotalPedidoMesa.classList.add("hidden");
        return;
    }

    const mapDetallesMesa = detallesPedidoLocal.get(MesaActiva.idMesa) || new Map();
    let total = 0;

    mapDetallesMesa.forEach((detalle) => {
        const fila = document.createElement("tr");
        fila.innerHTML = `
            <td>${detalle.nombreProducto}</td>
            <td>${detalle.cantidad}</td>
            <td>${formatoMoneda(detalle.precioUnitario)}</td>
            <td>${formatoMoneda(detalle.total)}</td>
        `;
        total += detalle.total;
        tbodyPedidosMesaAgregados.appendChild(fila);
    });

    txtTotalPedidoMesa.textContent = formatoMoneda(total);

    if (mapDetallesMesa.size > 0) {
        tfootTotalPedidoMesa.classList.remove("hidden");
    } else {
        tfootTotalPedidoMesa.classList.add("hidden");
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

    const mapActivo = tipoPedidoActivo === "DELIVERY" ? detallesPedidoDelivery : detallesPedidoLlevar;

    if (mapActivo.has(detalle.idProducto)) {
        const productoExistente = mapActivo.get(detalle.idProducto);
        productoExistente.cantidad += detalle.cantidad;
        productoExistente.total = productoExistente.cantidad * productoExistente.precioUnitario;
    } else {
        mapActivo.set(detalle.idProducto, detalle);
    }

    txtCantidadPedido.value = "";
    comboProductoPedido.selectedIndex = 0;
    renderDetallesPedido(tipoPedidoActivo);
});


const btnRegistrarPagoPedido = document.getElementById("btnRegistrarPagoPedido");
btnRegistrarPagoPedido.addEventListener("click", async (e) => {
    e.preventDefault();

    const comboMetodoPagoPedido = document.getElementById("comboMetodoPagoPedido");
    const mapActivo = tipoPedidoActivo === "DELIVERY" ? detallesPedidoDelivery : detallesPedidoLlevar;

    const requestPedido = {
        metodoPago: comboMetodoPagoPedido.value,
        tipoPedido: tipoPedidoActivo,
        detalles: Array.from(mapActivo.values()).map((detalle) => ({
            idProducto: detalle.idProducto,
            cantidad: detalle.cantidad
        }))
    };

    const pagoCorrecto = await cobrarPedido(requestPedido);
    if (pagoCorrecto) {
        mapActivo.clear();
        renderDetallesPedido();
    }
});


const divPedidoRegistro = document.getElementById("divPedidoRegistro");
const divMesaPedidos = document.getElementById("divMesaPedidos");

const btnTabLlevar = document.getElementById("btnTabLlevar");
const btnTabDelivery = document.getElementById("btnTabDelivery");
const btnTabLocal = document.getElementById("btnTabLocal");

const divPedidoLlevar = document.getElementById("divPedidoLlevar");
btnTabLlevar.addEventListener("click", () => {
    divPedidoLlevar.appendChild(divPedidoRegistro);
    divPedidoRegistro.classList.remove("hidden");
    divMesaPedidos.classList.add("hidden");
    tipoPedidoActivo = "LLEVAR";
    renderDetallesPedido();
});

const divPedidoDelivery = document.getElementById("divPedidoDelivery");
btnTabDelivery.addEventListener("click", () => {
    divPedidoDelivery.appendChild(divPedidoRegistro);
    divPedidoRegistro.classList.remove("hidden");
    divMesaPedidos.classList.add("hidden");
    tipoPedidoActivo = "DELIVERY";
    renderDetallesPedido();
});

btnTabLocal.addEventListener("click", () => {
    tipoPedidoActivo = "LOCAL";
    divPedidoRegistro.classList.add("hidden");
    divMesaPedidos.classList.remove("hidden");
    renderDetallesMesa();
});

const btnOcupar = document.getElementById("btnOcupar");
btnOcupar.addEventListener("click",(e)=>{

    e.preventDefault();

    

});

const btnAgregarPedidoMesa = document.getElementById("btnAgregarPedidoMesa");
btnAgregarPedidoMesa.addEventListener("click", async (e) => {

    e.preventDefault();

    if (MesaActiva == null) {
        mostrarToast("Por favor, selecciona una mesa primero", "info");
        return;
    }

    const comboProductoMesa = document.getElementById("comboProductoMesa");
    const txtCantidadMesa = document.getElementById("txtCantidadMesa");

    const requestDetalle = {
        idProducto: comboProductoMesa.value,
        cantidad: Number(txtCantidadMesa.value)
    };

    const detalleValido = await validarDetallePedido(requestDetalle);

    if (!detalleValido) {
        mostrarToast("La cantidad ingresada supera el stock", "error");
        return;
    }

    btnOcupar.classList.remove("hidden");
    const producto = productosMap.get(requestDetalle.idProducto);
    const precioUnitario = Number(producto.precio);
    const total = precioUnitario * requestDetalle.cantidad;

    const detalle = {
        idProducto: comboProductoMesa.value,
        nombreProducto: producto.nombreProducto,
        cantidad: Number(txtCantidadMesa.value),
        precioUnitario,
        total
    };

    const mapMesa = detallesPedidoLocal.get(MesaActiva.idMesa);

    if (mapMesa.has(detalle.idProducto)) {
        const productoExistente = mapMesa.get(detalle.idProducto);
        productoExistente.cantidad += detalle.cantidad;
        productoExistente.total = productoExistente.cantidad * productoExistente.precioUnitario;
    } else {
        mapMesa.set(detalle.idProducto, detalle);
    }

    txtCantidadMesa.value = "";
    comboProductoMesa.selectedIndex = 0;

    renderDetallesMesa();

});

const mesaGridVenta = document.getElementById("mesa-grid");
mesaGridVenta.addEventListener("click", (event) => {

    const boton = event.target.closest(".table-card");
    if (!boton) return;

    MesaActiva = boton;
    const numeroMesa = boton.dataset.mesa;
    const estadoMesa = boton.dataset.estado;


    document.getElementById("mesa-selected").textContent = `Mesa ${numeroMesa}`;
    document.getElementById("mesa-estado").textContent = estadoMesa;

    if(detallesPedidoLocal.get(MesaActiva.idMesa).size > 0){
        console.log("Tiene pedisos");
        btnOcupar.classList.remove("hidden");
    }else{
        btnOcupar.classList.add("hidden");
    }

    renderDetallesMesa();

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


