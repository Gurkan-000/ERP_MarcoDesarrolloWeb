const initLogin = () => {
  if (window.lucide) {
    window.lucide.createIcons();
  }
};

document.addEventListener('DOMContentLoaded', initLogin);
