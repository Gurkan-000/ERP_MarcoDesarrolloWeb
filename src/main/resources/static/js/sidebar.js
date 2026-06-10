/**
 * sidebar.js — Lógica compartida: hamburger toggle + topbar user dropdown
 * Se carga al final del <body> en todos los HTMLs (excepto login).
 */

// ─────────────────────────────────────────────
// HAMBURGER
// ─────────────────────────────────────────────
(function initSidebar() {
    const sidebar    = document.getElementById('mainSidebar');
    const btnHamburger = document.getElementById('btnHamburger');
    if (!sidebar || !btnHamburger) return;

    // Persistir estado con sessionStorage
    const SIDEBAR_KEY = 'sidebar_expanded';
    const savedExpanded = sessionStorage.getItem(SIDEBAR_KEY) === 'true';

    if (savedExpanded) {
        sidebar.classList.remove('collapsed');
    }

    btnHamburger.addEventListener('click', () => {
        const isCollapsed = sidebar.classList.toggle('collapsed');
        sessionStorage.setItem(SIDEBAR_KEY, (!isCollapsed).toString());
    });
})();

// ─────────────────────────────────────────────
// TOPBAR USER DROPDOWN
// ─────────────────────────────────────────────
(function initTopbarDropdown() {
    const btn      = document.getElementById('topbarUserBtn');
    const dropdown = document.getElementById('topbarDropdown');
    if (!btn || !dropdown) return;

    btn.addEventListener('click', (e) => {
        e.stopPropagation();
        const isOpen = dropdown.classList.toggle('open');
        btn.classList.toggle('open', isOpen);
    });

    // Cerrar al hacer click fuera
    document.addEventListener('click', (e) => {
        if (!btn.contains(e.target) && !dropdown.contains(e.target)) {
            dropdown.classList.remove('open');
            btn.classList.remove('open');
        }
    });
})();

// ─────────────────────────────────────────────
// TOPBAR — poblar nombre y rol desde sesión
// (usa el dato que ya cargó el JS de la página)
// ─────────────────────────────────────────────
window.poblarTopbarUsuario = function(sesion) {

    const displayName = sesion.usuario || 'Usuario';
    const rol         = sesion.rol || '';

    // Topbar derecho
    const topbarNombre = document.getElementById('topbarNombre');
    const topbarRol    = document.getElementById('topbarRol');
    const topbarAvatar = document.getElementById('topbarAvatar');

    if (topbarNombre) topbarNombre.textContent = displayName;
    if (topbarRol)    topbarRol.textContent    = rol;
    if (topbarAvatar) topbarAvatar.textContent = displayName.charAt(0).toUpperCase();

    // Sidebar footer (legado)
    const infoUsuario = document.getElementById('infoUsuario');
    const infoRol     = document.getElementById('infoRol');
    const infoAvatar  = document.getElementById('infoAvatar');

    if (infoUsuario) infoUsuario.textContent = displayName;
    if (infoRol)     infoRol.textContent     = rol;
    if (infoAvatar)  infoAvatar.textContent  = displayName.charAt(0).toUpperCase();
};
