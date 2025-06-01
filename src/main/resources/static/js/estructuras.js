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

/*
//CARDS DE LOS PATRONES - VISTA PREVIA
document.addEventListener("DOMContentLoaded", () => {
  const container = document.getElementById("contenedor-cards");

  patrones.forEach((patron) => {

    const card = document.createElement("div");
    card.className = "card";
    card.style.width = "18rem";

    card.innerHTML = `
        <a href="infoPatron.html" class="card-patron" data-id="${patron.id}">
          <img src="${patron.imagenes[0]}" class="card-img-top rounded-3" alt="${patron.titulo}">
          <div class="card-body">
            <div class="dificultad-titulo">
            ${patron.informacion.dificultad === "Avanzado"
        ? `<i class="bi bi-star-fill"></i>`
        : patron.informacion.dificultad === "Intermedio"
          ? `<i class="bi bi-star-half"></i>`
          : `<i class="bi bi-cart-fill"></i>`
      }
              <span class="card-title text-titulo fw-bold">${patron.titulo}</span>
            </div>
            <span class="text-creador">${patron.usuario.nombre}</span>
            <div class="precio-fav-guardar">
              <span class="text-precio fw-bold">${patron.precio}</span> 
              <div class="fav-guardar"> 
                <button type="button" class="btn toggle-btn-fav" data-bs-toggle="button"> 
                  <i class="bi bi-heart-fill"></i> 
                </button> 
                ${patron.precio === "Gratis" ?
        `<button type="button" class="btn toggle-btn-guardar" data-bs-toggle="button">
                    <i class="bi bi-bookmark-fill"></i>
                  </button>`
        : `<button type="button" class="btn toggle-btn-comprar" data-bs-toggle="button">
                    <i class="bi bi-cart-fill"></i>
                  </button>`} 
              </div>
            </div> 
          </div> 
        </a>
      `;
    container.appendChild(card);
  });

  // Guardar en localStorage al hacer clic
  container.addEventListener("click", (e) => {
    const link = e.target.closest(".card-patron");
    if (link) {
      const id = link.getAttribute("data-id");
      const patronSeleccionado = patrones.find((p) => p.id == id);
      localStorage.setItem("patronSeleccionado", JSON.stringify(patronSeleccionado));
    }
  });
});


//CARDS DE LOS PATRONES - VISTA PREVIA TIENDA 
document.addEventListener("DOMContentLoaded", () => {
  const container = document.getElementById("contenedor-cards-tienda");

  patrones.forEach((patron) => {
    const card = document.createElement("div");
    card.className = "card position-relative";
    card.style.width = "18rem";

    card.innerHTML = `
      <!-- Popover -->
      <button class="btn btn-light boton-popover position-absolute top-0 end-0 m-1 p-1">
        <i class="bi bi-gear-fill"></i>
      </button>
      <a href="infoPatron.html" class="card-patron" data-id="${patron.id}">
        <img src="${patron.imagenes[0]}" class="card-img-top rounded-3" alt="${patron.titulo}">
        <div class="card-body">
          <div class="dificultad-titulo">
          ${
            patron.informacion.dificultad === "Avanzado"
              ? `<i class="bi bi-star-fill"></i>`
              : patron.informacion.dificultad === "Intermedio"
              ? `<i class="bi bi-star-half"></i>`
              : `<i class="bi bi-cart-fill"></i>`
          }
            <span class="card-title text-titulo fw-bold">${patron.titulo}</span>
          </div>
          <span class="text-creador">${patron.usuario.nombre}</span>
          <div class="precio-fav-guardar">
            <span class="text-precio fw-bold">${patron.precio}</span> 
          </div> 
        </div> 
      </a>
    `;

    container.appendChild(card);

    // Inicializar el popover después de agregar el card al DOM
    const popoverBtn = card.querySelector(".boton-popover");

    const popoverContent = document.createElement("div");
    popoverContent.classList.add("text-center", "text-popover");

    const btnPrecio = document.createElement("button");
    btnPrecio.className = "btn btn-sm btn-outline-primary mb-1";
    btnPrecio.innerHTML = '<i class="bi bi-tag-fill"></i> Precio';

    const btnEditar = document.createElement("a");
    btnEditar.className = "btn btn-sm btn-outline-secondary mb-1 popover-editar";
    btnEditar.href = `edicionPatron.html?id=${patron.id}`;
    btnEditar.innerHTML = '<i class="bi bi-pencil-fill"></i> Editar';

    const btnEliminar = document.createElement("button");
    btnEliminar.className = "btn btn-sm btn-outline-danger";
    btnEliminar.innerHTML = '<i class="bi bi-trash3-fill"></i> Eliminar';

    popoverContent.appendChild(btnPrecio);
    popoverContent.appendChild(btnEditar);
    popoverContent.appendChild(btnEliminar);

    new bootstrap.Popover(popoverBtn, {
      content: popoverContent,
      html: true,
      placement: "bottom",
      trigger: "click", // más fiable que 'focus'
    });

    // Guardar en localStorage al hacer clic
    btnEditar.addEventListener("click", (e) => {
      const id = patron.id;
      const patronSeleccionado = patrones.find((p) => p.id == id);
      localStorage.setItem("patronSeleccionado", JSON.stringify(patronSeleccionado));
    });
  });
});
*/
