/*******************************************************************************
***** FICHERO PARA LAS FUNCIONES QUE DEFINEN LAS ESTRUCTURAS REUTILIZABLES *****
********************************************************************************/

//MENU
function cargarMenu() {
    const paginaActual = window.location.pathname.split("/").pop();

    // Obtener datos del usuario
    const user = JSON.parse(sessionStorage.getItem("usuario"));
    console.log("Usuario creador: ",user.esCreador)

    // Determinar las páginas debe mostrar
    let perfilPagina = "perfil.html";
    if (user.esCreador) {
      perfilPagina = "tienda.html";
    }

    const menuHTML = `
    <nav class="navbar navbar-expand-lg bg-body-tertiary menu">
        <div class="container-fluid">
          <a class="navbar-brand" href="#">TFG-nombre</a>
          <div class="collapse navbar-collapse" id="navbarNavAltMarkup">
            <div class="navbar-nav">
              <a class="nav-link ${paginaActual === "inicio.html" ? "active" : ""}" href="inicio.html" aria-current="${paginaActual === "inicio.html" ? "page" : ""}">Inicio</a>
              <a class="nav-link ${paginaActual === "busqueda.html" ? "active" : ""}" href="busqueda.html" aria-current="${paginaActual === "busqueda.html" ? "page" : ""}">Busqueda</a>
              <a class="nav-link ${paginaActual === "notificaciones.html" ? "active" : ""}" href="notificaciones.html" aria-current="${paginaActual === "notificaciones.html" ? "page" : ""}">Notificaciones</a>
              <a class="nav-link ${paginaActual === perfilPagina ? "active" : ""}" href="${perfilPagina}" aria-current="${paginaActual === perfilPagina ? "page" : ""}">Perfil</a>
            </div>
          </div>
        </div>
    </nav>
    `;
    document.getElementById("container-menu").innerHTML = menuHTML;
}
document.addEventListener("DOMContentLoaded", cargarMenu);

