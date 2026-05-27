document.addEventListener('DOMContentLoaded', () => {
    const userTableBody = document.querySelector('table tbody');
    const userForm = document.querySelector('form');

    const loadUsers = async () => {
        if (!userTableBody) return;
        try {
            const response = await fetch('/api/usuario/listar');
            const users = await response.json();
            
            userTableBody.innerHTML = '';
            users.forEach(user => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${user.nombre}</td>
                    <td>${user.rol}</td>
                    <td>${user.vistas ? user.vistas.join(', ') : ''}</td>
                `;
                userTableBody.appendChild(row);
            });
        } catch (error) {
            console.error('Error loading users:', error);
        }
    };

    if (userForm) {
        userForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const usuario = {
                nombre: userForm.querySelector('input[name="nombre"]').value,
                contrasena: userForm.querySelector('input[name="contrasena"]').value,
                rol: userForm.querySelector('select[name="rol"]').value
            };

            try {
                const response = await fetch('/api/usuario/crear', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(usuario)
                });

                if (response.ok) {
                    userForm.reset();
                    loadUsers();
                    alert('Usuario creado correctamente');
                } else {
                    alert('Error al crear usuario');
                }
            } catch (error) {
                console.error('Error creating user:', error);
            }
        });
    }

    loadUsers();
});
