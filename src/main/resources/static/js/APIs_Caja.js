
const btnCerrarTurno = document.getElementById("btnCerrarTurno");
// CAMBIO AQUÍ: getAnimations -> getElementById
const btnAbrirTurno = document.getElementById("btnAbrirTurno"); 

btnCerrarTurno.addEventListener("click",(e)=>{
    e.preventDefault();
    const divCajaTurnoCerrado = document.getElementById("divCajaTurnoCerrado");
    const divCajaTurnoAbierto = document.getElementById("divCajaTurnoAbierto");

    divCajaTurnoCerrado.classList.remove("hidden");
    divCajaTurnoAbierto.classList.add("hidden");
});

btnAbrirTurno.addEventListener("click",(e)=>{
    e.preventDefault();
    const divCajaTurnoCerrado = document.getElementById("divCajaTurnoCerrado");
    const divCajaTurnoAbierto = document.getElementById("divCajaTurnoAbierto");

    divCajaTurnoCerrado.classList.add("hidden");
    divCajaTurnoAbierto.classList.remove("hidden");
});