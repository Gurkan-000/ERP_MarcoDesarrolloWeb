document.addEventListener('DOMContentLoaded', () => {
    const productsTableBody = document.querySelector('#cat-productos table tbody');
    const categoriesTableBody = document.querySelector('#cat-categorias table tbody');
    const categorySelect = document.querySelector('select[name="categoriaNombre"]');
    const productForm = document.getElementById('form-cat-productos');
    const categoryForm = document.querySelector('#cat-categorias form');

    const loadCategories = async () => {
        try {
            const response = await fetch('/api/catalogo/listarCategorias');
            const categories = await response.json();
            
            if (categoriesTableBody) {
                categoriesTableBody.innerHTML = '';
                categories.forEach(cat => {
                    const row = document.createElement('tr');
                    row.innerHTML = `
                        <td>${cat.nombre}</td>
                        <td>${cat.productos ? cat.productos.length : 0}</td>
                        <td><span class="badge ${cat.estado === 'Activo' ? 'badge-green' : 'badge-muted'}">${cat.estado}</span></td>
                    `;
                    categoriesTableBody.appendChild(row);
                });
            }

            if (categorySelect) {
                categorySelect.innerHTML = '<option value="" disabled selected>Seleccione una categoria</option>';
                categories.forEach(cat => {
                    const option = document.createElement('option');
                    option.value = cat.idCategoria; // We use UUID now
                    option.textContent = cat.nombre;
                    categorySelect.appendChild(option);
                });
            }
        } catch (error) {
            console.error('Error loading categories:', error);
        }
    };

    const loadProducts = async () => {
        if (!productsTableBody) return;
        try {
            const response = await fetch('/api/catalogo/listarProductos');
            const products = await response.json();
            
            productsTableBody.innerHTML = '';
            products.forEach(prod => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${prod.nombre}</td>
                    <td>${prod.categoriaNombre}</td>
                    <td>S/ ${prod.precio.toFixed(2)}</td>
                `;
                productsTableBody.appendChild(row);
            });
        } catch (error) {
            console.error('Error loading products:', error);
        }
    };

    if (productForm) {
        productForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const idCategoria = categorySelect.value;
            const producto = {
                nombre: productForm.querySelector('input[type="text"]').value,
                precio: parseFloat(productForm.querySelector('input[step="0.1"]').value),
                stock: parseInt(productForm.querySelector('input[placeholder="Ej. 10"]').value)
            };

            try {
                const response = await fetch(`/api/catalogo/crearProducto/categoria/${idCategoria}`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(producto)
                });

                if (response.ok) {
                    productForm.classList.add('hidden');
                    productForm.reset();
                    loadProducts();
                }
            } catch (error) {
                console.error('Error creating product:', error);
            }
        });
    }

    if (categoryForm) {
        categoryForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const categoria = {
                nombre: categoryForm.querySelector('input[type="text"]').value,
                estado: categoryForm.querySelector('select').value
            };

            try {
                const response = await fetch('/api/catalogo/crearCategoria', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(categoria)
                });

                if (response.ok) {
                    categoryForm.classList.add('hidden');
                    categoryForm.reset();
                    loadCategories();
                }
            } catch (error) {
                console.error('Error creating category:', error);
            }
        });
    }

    loadCategories();
    loadProducts();
});
