/********************************************************************************
** FICHERO PARA LAS FUNCIONES QUE DEFINEN LA CARGA DE DATOS DE INFOPATRON.HTML **
*********************************************************************************/

//RECUPERAR EL PATRON SELECCIONADO PARA MOSTRAR LOS DATOS DESPUES
document.addEventListener("DOMContentLoaded", () => {
    const patronGuardado = localStorage.getItem("patronSeleccionado");
    if (patronGuardado) {
      const patron = JSON.parse(patronGuardado);
      //const patronId = patron.id;
      cargarDatosInfoPatron(patron);
    } else {
      console.error("No se encontró ningún patrón.");
    }
});

//CARGA DE DATOS PATRONES
function cargarDatosInfoPatron(patron) {
    // Título y usuario
    document.querySelector(".card-title").textContent = patron.titulo;
    document.querySelector(".card-subtitle .img-usu").src = patron.creador.imagen;
    document.querySelector(".card-subtitle").innerHTML = `
        <div class="d-flex gap-1 creador" id="creadorPatron" data-id-creador="${patron.creador.id}" style="cursor:pointer;">
            <img src="${patron.creador.imagenPerfil}" alt="Perfil" class="rounded-circle img-usu">
            ${patron.creador.nombreUsuario}
        </div>
    `;
  
    // Descripción
    document.querySelector(".card-text").textContent = patron.descripcion;

    // Precio
    if (patron.precio > 0){
        document.querySelector(".card-precio").textContent = patron.precio;
        document.querySelector(".card-link").textContent = "Comprar";
        document.querySelector(".card-link").href = "pasarelaPago.html";
    }
    else{
        document.querySelector(".card-precio").textContent = "Gratis";
        document.querySelector(".card-link").textContent = "Guardar";
        //TODO: guardar el patron en la biblioteca
    }

    // Información
    document.querySelector("#panelInformacion .accordion-body").innerHTML = `
        <div class="row"><div class="col titulo-info"><p>Dificultad:</p></div><div class="col col-9 txt-info"><p>${patron.dificultad}</p></div></div>
        <!--<div class="row"><div class="col titulo-info"><p>Descripción:</p></div><div class="col col-9 txt-info"><p>${patron.descripcion}</p></div></div>-->
        <div class="row"><div class="col titulo-info"><p>Idioma:</p></div><div class="col col-9 txt-info"><p>${patron.idioma}</p></div></div>
        <div class="row"><div class="col titulo-info"><p>Unidad:</p></div><div class="col col-9 txt-info"><p>${patron.unidad}</p></div></div>
    `;
  
    // Materiales
    document.querySelector("#panelMateriales .accordion-body").innerHTML = `
        <div class="row"><div class="col titulo-info"><p>Lanas:</p></div><div class="col col-9 txt-info"><p>${patron.lanas}</p></div></div>
        <div class="row"><div class="col titulo-info"><p>Aguja de ganchillo:</p></div><div class="col col-9 txt-info"><p>${patron.agujaGanchillo}</p></div></div>
        <div class="row"><div class="col titulo-info"><p>Aguja lanera:</p></div><div class="col col-9 txt-info"><p>${patron.agujaLanera}</p></div></div>
        <div class="row"><div class="col titulo-info"><p>Otros:</p></div><div class="col col-9 txt-info"><p>${patron.otros}</p></div></div>
    `;
  
    // Abreviaturas
    document.querySelector("#panelAbreviaturas .accordion-body").innerHTML = `
        <div class="row"><div class="col titulo-info"><p>${patron.abreviaturas}</p></div></div>
    `;
  
    // Tags
    const tagContainer = document.querySelector("#panelTags .accordion-body .div-tags");
    tagContainer.innerHTML = "";
    patron.tags.forEach(tag => {
        const p = document.createElement("p");
        p.classList.add("txt-tags");
        p.textContent = tag;
        tagContainer.appendChild(p);
    });
  
    // Carrusel de imágenes
    const carouselInner = document.querySelector("#carouselFotos .carousel-inner");
    const carouselIndicators = document.querySelector(".carousel-indicators");
    carouselInner.innerHTML = "";
    carouselIndicators.innerHTML = "";
    patron.imagenes.forEach((img, index) => {
        const div = document.createElement("div");
        div.className = `carousel-item ${index === 0 ? 'active' : ''}`;
        div.innerHTML = `<img src="${img}" class="d-block w-100 rounded-3" alt="...">`;
        carouselInner.appendChild(div);
  
        const button = document.createElement("button");
        button.type = "button";
        button.setAttribute("data-bs-target", "#carouselFotos");
        button.setAttribute("data-bs-slide-to", index);
        button.setAttribute("aria-label", `Slide ${index + 1}`);
        if (index === 0) {
            button.className = "active";
            button.setAttribute("aria-current", "true");
        }
        carouselIndicators.appendChild(button);
    });
  
    // Reseñas
    const contenedorResenas = document.getElementById("contenedorReseñas");
    contenedorResenas.innerHTML = "";
    patron.resenas.forEach(resena => {
        const card = document.createElement("div");
        card.className = "card mb-3 card-resenia";
        card.innerHTML = `
            <div class="card-header d-flex align-items-center gap-1">
                <img src="${resena.imagenUsuario}" alt="Usuario" class="rounded-circle img-usu-resenia">
                <strong>${resena.nombre}</strong>
            </div>
            <div class="card-body card-body-resenia">
                ${resena.imagenPatron ? `<img src="${resena.imagenPatron}" class="img-fluid rounded mb-3 img-resenia">` : ""}
                <div class="mb-2">
                    ${[...Array(5)].map((_, i) =>
                        `<i class="bi ${i < resena.puntuacion ? 'bi-star-fill text-warning' : 'bi-star text-muted'}"></i>`
                    ).join('')}
                </div>
                <p class="mb-0 comentario-resenia">${resena.comentario}</p>
            </div>
        `;
        contenedorResenas.appendChild(card);
    });

    //Event si se pulse en el creador: redirige al usuario a su tienda
    document.getElementById("creadorPatron").addEventListener("click", function () {
        const idCreador = this.getAttribute("data-id-creador");
        localStorage.setItem("idCreadorSeleccionado", idCreador);
        window.location.href = "tiendaVistaUsuario.html";
    });
    
}
  
  document.addEventListener("DOMContentLoaded", () => {
    const patronGuardado = localStorage.getItem("patronSeleccionado");
    if (patronGuardado) {
      const patron = JSON.parse(patronGuardado);
      cargarDatosInfoPatron(patron);
    } else {
      console.error("No se encontró ningún patrón.");
    }
  });
