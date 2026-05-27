const initLogin = () => {
  if (window.lucide) {
    window.lucide.createIcons();
  }
};

document.addEventListener('DOMContentLoaded', initLogin);

document.getElementById("btnLogin").addEventListener("click",()=>{

  window.location.href = 'venta.html';

});