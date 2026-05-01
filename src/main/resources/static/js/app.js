const moduleTitle = document.getElementById('module-title');
const todayDate = document.getElementById('today-date');
const userName = document.getElementById('user-name');
const userRole = document.getElementById('user-role');
const userAvatar = document.getElementById('user-avatar');

const resetModal = document.getElementById('reset-modal');
const openReset = document.getElementById('open-reset');
const cancelReset = document.getElementById('cancel-reset');
const confirmReset = document.getElementById('confirm-reset');

const navItems = Array.from(document.querySelectorAll('.nav-item'));
const modules = Array.from(document.querySelectorAll('.module'));
const catalogActions = Array.from(document.querySelectorAll('.catalog-action'));
const catalogForms = {
  'cat-productos': document.getElementById('form-cat-productos'),
  'cat-categorias': document.getElementById('form-cat-categorias')
};

const updateDate = () => {
  if (!todayDate) return;
  todayDate.textContent = new Date().toLocaleDateString('es-PE', {
    weekday: 'long',
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  });
};

const activateModule = (moduleId, navButton) => {
  navItems.forEach((item) => item.classList.remove('active'));
  if (navButton) {
    navButton.classList.add('active');
    if (moduleTitle) moduleTitle.textContent = navButton.textContent.trim();
  }

  modules.forEach((module) => module.classList.remove('active'));
  const target = document.getElementById(`module-${moduleId}`);
  if (target) {
    target.classList.add('active');
  }
};

const updateCatalogActions = (activeTabId) => {
  if (!catalogActions.length) return;
  catalogActions.forEach((button) => {
    const shouldShow = button.dataset.target === activeTabId;
    button.classList.toggle('hidden', !shouldShow);
  });
};

const closeCatalogForms = () => {
  Object.values(catalogForms).forEach((form) => {
    if (form) form.classList.add('hidden');
  });
};

const openCatalogForm = (tabId) => {
  Object.entries(catalogForms).forEach(([key, form]) => {
    if (!form) return;
    const shouldShow = key === tabId;
    form.classList.toggle('hidden', !shouldShow);
    if (shouldShow) {
      const focusable = form.querySelector('input, select');
      if (focusable) focusable.focus();
    }
  });
};

const handleNavClick = (event) => {
  const button = event.target.closest('.nav-item');
  if (!button) return;
  activateModule(button.dataset.module, button);
};

const handleTabClick = (event) => {
  const tabButton = event.target.closest('.tab');
  if (!tabButton) return;
  const module = tabButton.closest('.module');
  const tabId = tabButton.dataset.tab;
  if (!module || !tabId) return;

  module.querySelectorAll('.tab').forEach((tab) => tab.classList.remove('active'));
  tabButton.classList.add('active');

  module.querySelectorAll('.tab-content').forEach((panel) => panel.classList.remove('active'));
  const target = module.querySelector(`#${tabId}`);
  if (target) target.classList.add('active');

  if (module.id === 'module-catalogo') {
    updateCatalogActions(tabId);
    closeCatalogForms();
  }
};

const handleCatalogAction = (event) => {
  const button = event.target.closest('[data-action="open-form"]');
  if (!button) return;
  openCatalogForm(button.dataset.target);
};

const handleFormCancel = (event) => {
  const button = event.target.closest('[data-action="cancel-form"]');
  if (!button) return;
  const form = button.closest('.inline-form');
  if (form) form.classList.add('hidden');
};

const openModal = () => {
  resetModal.classList.remove('hidden');
};

const closeModal = () => {
  resetModal.classList.add('hidden');
};

const init = () => {
  closeModal();
  updateDate();

  const catalogActiveTab = document.querySelector('#module-catalogo .tab.active');
  if (catalogActiveTab) {
    updateCatalogActions(catalogActiveTab.dataset.tab);
  }

  document.addEventListener('click', handleNavClick);
  document.addEventListener('click', handleTabClick);
  document.addEventListener('click', handleCatalogAction);
  document.addEventListener('click', handleFormCancel);

  if (openReset) openReset.addEventListener('click', openModal);
  if (cancelReset) cancelReset.addEventListener('click', closeModal);
  if (confirmReset) confirmReset.addEventListener('click', closeModal);
  if (resetModal) {
    resetModal.addEventListener('click', (event) => {
      if (event.target === resetModal) closeModal();
    });
  }

  if (window.lucide) {
    window.lucide.createIcons();
  }
};

document.addEventListener('DOMContentLoaded', init);
