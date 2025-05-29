/**************************************************************************
 *********** FICHERO PARA LAS FUNCIONES QUE DEFINEN LA CARGA DE ***********
 ***********        PATRONES GUARDADOS EN LA BIBLIOTECA         ***********
 **************************************************************************/

const cargarPatrones = (endpoint, container) => {
    //const container = document.getElementById("contenedor-cards");
    container.innerHTML = "";

    fetch(endpoint, {
        method: "GET",
        credentials: "include"
    })
    .then((response) => {
        if (!response.ok) {
            throw new Error("No se pudieron cargar los patrones");
        }
        return response.json();
    })
    .then((patrones) => {
        patrones.forEach((patron) => {
            const card = document.createElement("div");
            card.id = patron.id;
            card.className = "card position-relative";
            card.style.width = "18rem";

            card.innerHTML = `
                    <button class="btn btn-light boton-popover position-absolute top-0 end-0 m-1 p-1">
                        <i class="bi bi-gear-fill"></i>
                    </button>
                    <a href="infoPatron.html" class="card-patron" data-id="${patron.id}" id="card-patron-${patron.id}">
                        <img src="${patron.imagenes?.[0] || 'placeholder.jpg'}" class="card-img-top rounded-3" alt="${patron.titulo}">
                        <div class="card-body" href="infoPatron.html">
                            <div class="dificultad-titulo">
                                ${patron.dificultad === "Avanzado"
                                    ? `<i class="bi bi-star-fill"></i>`
                                    : patron.dificultad === "Intermedio"
                                    ? `<i class="bi bi-star-half"></i>`
                                    : `<i class="bi bi-star"></i>`
                                }
                                <span class="card-title text-titulo fw-bold">${patron.titulo}</span>
                            </div>
                            <span class="text-creador">Yo</span>
                            <div class="precio-fav-guardar">
                                <span class="text-precio fw-bold">${patron.precio}</span>
                            </div>
                        </div>
                    </a>
                `;

            container.appendChild(card);

            const cardLink = card.querySelector(`#card-patron-${patron.id}`);
            cardLink.addEventListener("click", async (e) => {
                e.preventDefault(); // Evitamos que el navegador cambie de página automáticamente
                try {
                    await guardarPatronSeleccionado(patron.id);
                    window.location.href = "infoPatron.html";
                } catch (error) {
                    console.error("Error al guardar patron:", error);
                    alert("No se pudo cargar el patrón seleccionado.");
                }
            });


        });
    }).catch((error) => {
        console.error("Error cargando patrones:", error);
        container.innerHTML = "<p>Error cargando los patrones.</p>";
    });
};

const guardarPatronSeleccionado = async (patronId) => {
    try {
        const response = await fetch(`/api/patrones/encontrar?id=${patronId}`, {
            method: "GET",
            credentials: "include"
        });
        if (!response.ok) {
            const errorText = await response.text();
            console.error("Estado:", response.status);
            console.error("Respuesta:", errorText);
            alert(`Error al encontrar patrón: ${response.status}`);
            return;
        }
        const patron = await response.json();
        localStorage.setItem("patronSeleccionado", JSON.stringify(patron));
        return;
    } catch (error) {
        console.error("Error al encontrar el patrón:", error);
        alert("No se pudo encontrar el patrón. Inténtalo de nuevo.");
    }
}