
const cargarTablaCategoria = async () => {

    try {

        const response = await fetch('http://localhost:8080/api/catalogo/listarCategorias');

        if (!response.ok) {
            console.log("Ocurrio un error");
        } else {
            const data = await response.json();

            data.forEach(categoria => {

                const tbody = document.getElementById("tbodyCategoria");
                const rowTBody = document.createElement("tr");

                rowTBody.dataset.id = categoria.idCategoria;
                
                rowTBody.innerHTML = `
                <td>${categoria.nombre}</td>
                <td>${categoria.productos}</td>
            `;

                tbody.appendChild(rowTBody);

            });

        }

    } catch (error) {
        console.log(error);
    }

}

cargarTablaCategoria();

const btnGuardarCategoria = document.getElementById("btnGuardarCategoria");

btnGuardarCategoria.addEventListener("click", (e) => {

    e.preventDefault();

    const txtNombreCategoria = document.getElementById("txtNombreCategoria");
    const requestCategoria = {
        "nombre": txtNombreCategoria.value
    }

    insertarCategoria(requestCategoria);

    const form_cat = document.getElementById("form-cat-categorias");
    form_cat.classList.add('hidden');
    txtNombreCategoria.value = " ";
});

const insertarCategoria = async (requestCategoria) => {

    try {

        const response = await fetch('http://localhost:8080/api/catalogo/crearCategoria', {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(requestCategoria)
        });

        const data = await response.json();
        
        if (!response.ok) {
            console.log(data);
        } else {
        
            const tbody = document.getElementById("tbodyCategoria");
            const rowTBody = document.createElement("tr");

            rowTBody.dataset.id = data.idCategoria;

            rowTBody.innerHTML = `
                <td>${data.nombre}</td>
                <td>${data.productos}</td>
            `;

            tbody.appendChild(rowTBody);
        }

    } catch (error) {
        console.log(error)
    }

}
