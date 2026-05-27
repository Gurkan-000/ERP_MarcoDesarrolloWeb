
const cargarTablaProducto = async () => {

    try {

        const response = await fetch('http://localhost:8080/api/catalogo/listarProductos');

        if (!response.ok) {
            console.log("Ocurrio un error");
        } else {
            const data = await response.json();

            data.forEach(producto => {

                const tbody = document.getElementById("tbodyProducto");
                const rowTBody = document.createElement("tr");

                rowTBody.dataset.id = producto.idProducto;
                
                rowTBody.innerHTML = `
                <td>${producto.nombreProducto}</td>
                <td>${producto.nombreCategoria}</td>
                <td>${producto.precio}</td>
            `;

                tbody.appendChild(rowTBody);

            });

        }

    } catch (error) {
        console.log(error);
    }

}

cargarTablaProducto();

const llenarComboCategoria = async () => {
    try { 

        const combo = document.getElementById('comboCategorias');

        const response = await fetch('http://localhost:8080/api/catalogo/listarCategorias');

        if(!response.ok) {

            console.log("Ocurrió un error listando categorias con el fetch");

        } else { 

            const data = await response.json();

            data.forEach(categoria => {

                const option = document.createElement("option");

                option.value = categoria.idCategoria;
                option.textContent = categoria.nombre;

                combo.appendChild(option);

            })
        }
    } catch (error) {

        console.log(error);

    }
}

llenarComboCategoria();

const btnGuardarProducto = document.getElementById('btnGuardarProducto');

btnGuardarProducto.addEventListener("click", async (e) => {
    
    e.preventDefault();

    const nombreProducto = document.getElementById('productoNombre');

    const combo = document.getElementById('comboCategorias');
    const idCategoria = combo.value;

    const precioProducto = document.getElementById('precioProducto');

    const stockProducto = document.getElementById('stockProducto');

    const requestProducto = {
        "nombre" : nombreProducto.value,
        "stock" : Number(stockProducto.value),
        "precio" : Number(precioProducto.value)
    }

    //VALIDAR SI SE HA SELECCIONADO CATEGORIA PORQUE QUIZA PUEDA DAR ERROR XD
    if (!idCategoria) {

        alert("Seleccione una categoría");

    return;
    }

    await insertarProducto(requestProducto, idCategoria);

    const form = document.getElementById('form-cat-productos');
    form.classList.add('hidden');
    nombreProducto.value = "";
    precioProducto.value = "";
    stockProducto.value = "";
    document.getElementById('comboCategorias').selectedIndex = 0;

})

const insertarProducto = async (requestProducto, idCategoria) => {
    try {

        const response = await fetch(`http://localhost:8080/api/catalogo/crearProducto/categoria/${idCategoria}`, 
            {
                method: "POST",

                headers: {
                    "Content-Type": "application/json"
                },

                body: JSON.stringify(requestProducto)
            });

        const data = await response.json();

        if(!response.ok) {

            console.log("Ocurrió un error creando el producto al usar FETCH /crearProducto/categoria/idCat - line 103", data);

        } else {

            console.log("Producto creado, BABY!")

            const tBody = document.getElementById("tbodyProducto");
            const rowtBody = document.createElement("tr");

            rowtBody.dataset.id = data.idProducto;

            rowtBody.innerHTML = `
                <td>${data.nombreProducto}</td>
                <td>${data.nombreCategoria}</td>
                <td>${data.precio}</td>
            `;

            tBody.appendChild(rowtBody);

        }
    } catch (error) {

        console.log(error);

    }
}