/******************************************************************************************
 ** FICHERO PARA LAS FUNCIONES QUE DEFINEN LA CARGA DE DATOS DE instruccionesPatron.HTML **
 ******************************************************************************************/

//RECUPERAR EL PATRON SELECCIONADO PARA MOSTRAR LOS DATOS DESPUES
document.addEventListener("DOMContentLoaded", () => {
    const patronGuardado = localStorage.getItem("patronSeleccionado");
    if (patronGuardado) {
        const patron = JSON.parse(patronGuardado);
        cargarEdicionPatron(patron);
    } else {
        console.error("No se encontró ningún patrón.");
    }
});


//CARGA DE DATOS PATRONES PARA EDITARLOS
function cargarEdicionPatron(patron) {
    // Información general
    document.querySelector('input[placeholder="Ej: Gorro básico de invierno"]').value = patron.titulo || '';
    document.querySelector('input[placeholder="Tu nombre o alias"]').value = patron.creador.nombreUsuario || '';
    document.querySelector('textarea[placeholder*="Breve"]').value = patron.descripcion || '';
    document.querySelector('#idioma').value = patron.idioma || '';
    document.querySelector('#unidad').value = patron.unidad || '';
    document.querySelector('#dificultad').value = patron.dificultad || '';

    document.querySelector('input[placeholder*="Lana"]').value = patron.lanas || '';
    document.querySelector('input[placeholder*="5 mm"]').value = patron.agujaGanchillo || '';
    document.querySelector('input[placeholder*="Sí / No"]').value = patron.agujaLanera || '';
    document.querySelector('input[placeholder*="Marcadores"]').value = patron.otros || '';

    document.querySelector('textarea[placeholder*="pb = punto bajo"]').value = patron.abreviaturas || '';
    const tagContainer = document.querySelector(".div-tags");
    tagContainer.innerHTML = "";
    patron.tags.forEach(tag => {
        const p = document.createElement("p");
        p.classList.add("txt-tags");
        p.textContent = tag;
        tagContainer.appendChild(p);
    });

    // Carrusel de imágenes para la vista previa
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

    // Instrucciones
    let instrucciones = [];
    try {
        if (typeof patron.instrucciones === 'string' && patron.instrucciones.trim().startsWith('[')) {
            instrucciones = JSON.parse(patron.instrucciones);
        } else {
            console.warn("Formato de instrucciones inválido:", patron.instrucciones);
        }
    } catch (e) {
        console.error("Error al parsear instrucciones:", e);
    }
    if (Array.isArray(instrucciones)) {
        instrucciones.forEach(seccion => {
            addSeccion();
            const ultima = document.querySelectorAll('#secciones-patron > .card');
            const nuevaSeccion = ultima[ultima.length - 1];
            nuevaSeccion.querySelector('h3.txt-titulo').textContent = seccion.titulo;

            const contenedor = nuevaSeccion.querySelector('[id$="-contenido"]');

            seccion.contenido.forEach(item => {
                if (item.tipo === 'subtitulo') {
                    addSubtitle(contenedor.id);
                    contenedor.lastChild.querySelector('h5').textContent = item.texto;
                } else if (item.tipo === 'info') {
                    addInfo(contenedor.id);
                    contenedor.lastChild.querySelector('textarea').value = item.texto;
                } else if (item.tipo === 'vuelta') {
                    addRound(contenedor.id);
                    contenedor.lastChild.querySelector('input').value = item.texto;
                } else if (item.tipo === 'imagen') {
                    console.log("log: ",item.contenido)
                    addImagen(contenedor.id, [item.contenido]);
                }
            });
        });
    } else {
        console.warn('instrucciones no es un array:', patron.instrucciones);
    }
}

// LOGICA PARA LA CREACION DINAMICA DE SECCIONES Y SUS COMPONENTES
let contadorSecciones = 0;

function addSeccion() {
    contadorSecciones++;
    const seccionId = `seccion-${contadorSecciones}`;
    const seccion = document.createElement('div');
    seccion.className = 'card card-seccion mb-4';
    seccion.innerHTML = `
        <div class="card-body card-body-seccion">
            <div class="d-flex justify-content-between align-items-center mb-3 titulo-seccion">
                <h3 class="mb-0 txt-titulo" style="color: #fffbf3"></h3>
            </div>
            <div id="${seccionId}-contenido" data-round-count="0"></div>     
        </div>
        `;
    document.getElementById('secciones-patron').appendChild(seccion);
}

function addSubtitle(seccionId) {
    const container = document.getElementById(seccionId);
    const div = document.createElement('div');
    div.className = 'mb-2 d-flex align-items-center';
    div.setAttribute('data-type', 'subtitle');
    div.innerHTML = `
            <h5 class="txt-subtitulo" style="color: #fffbf3"></h5>
        `;
    container.appendChild(div);
    container.setAttribute('data-round-count', '0');
}


function addInfo(seccionId) {
    const container = document.getElementById(seccionId);
    const div = document.createElement('div');
    div.className = 'mb-2 d-flex align-items-center';
    div.innerHTML = `
        <textarea readonly class="form-control" placeholder="Información adicional"></textarea>
        `;
    container.appendChild(div);
}

function addImagen(seccionId, urls = []) {
    const container = document.getElementById(seccionId);
    const wrapper = document.createElement('div');
    wrapper.className = 'input-group-imagen mb-3';
    wrapper.setAttribute('data-type', 'imagen');

    const previewContainer = document.createElement('div');
    previewContainer.className = 'div-img-instruccion d-flex flex-wrap gap-2 mt-2';
    previewContainer.style.justifyContent = 'center';

    wrapper.appendChild(previewContainer);
    container.appendChild(wrapper);

    // Si se pasaron URLs (imágenes ya guardadas), mostrarlas
    urls = JSON.parse(urls);
    if (urls.length > 0) {
        previewContainer.innerHTML = '';
        urls.forEach(url => {
            const img = document.createElement('img');
            img.className = 'img-preview';
            img.src = url;
            img.style.width = '20vw';
            img.style.maxWidth = '20vw';
            img.style.height = '20vw';
            img.style.maxHeight = '20vw';
            img.style.objectFit = 'cover';
            img.style.borderRadius = '8px';
            previewContainer.appendChild(img);
        });
    }
}
1
function addRound(seccionId) {
    const seccionContent = document.getElementById(seccionId);
    const children = Array.from(seccionContent.children);

    // Buscar el índice del último subtítulo
    let startIndex = 0;
    for (let i = children.length - 1; i >= 0; i--) {
        if (children[i].getAttribute('data-type') === 'subtitle') {
            startIndex = i + 1;
            break;
        }
    }

    // Buscar el último número de vuelta dentro del bloque
    let lastRoundNumber = 0;
    for (let i = startIndex; i < children.length; i++) {
        if (children[i].getAttribute('data-type') === 'round') {
            const label = children[i].querySelector('.input-group-text');
            const match = label?.textContent.match(/Vuelta (\d+)/);
            if (match) {
                const num = parseInt(match[1]);
                if (num > lastRoundNumber) lastRoundNumber = num;
            }
        }
        if (children[i].getAttribute('data-type') === 'subtitle') {
            break; // encontramos otro subtítulo, detenemos
        }
    }

    const newRoundNumber = lastRoundNumber + 1;

    const group = document.createElement('div');
    group.className = 'input-group mb-2';
    group.setAttribute('data-type', 'round');

    const span = document.createElement('span');
    span.className = 'input-group-text';
    span.textContent = `Vuelta ${newRoundNumber}`;

    const input = document.createElement('input');
    input.type = 'text';
    input.readOnly = true;
    input.className = 'form-control';
    input.placeholder = 'Instrucciones de la vuelta';

    group.appendChild(span);
    group.appendChild(input);

    seccionContent.appendChild(group);
}

function renumberRounds(container, deletedIndex) {
    const elements = Array.from(container.children);

    // Encuentra el bloque de vueltas a renumerar
    let startIndex = 0;
    for (let i = deletedIndex - 1; i >= 0; i--) {
        if (elements[i].getAttribute('data-type') === 'subtitle') {
            startIndex = i + 1;
            break;
        }
    }

    let endIndex = elements.length;
    for (let i = deletedIndex + 1; i < elements.length; i++) {
        if (elements[i].getAttribute('data-type') === 'subtitle') {
            endIndex = i;
            break;
        }
    }

    // Renumerar solo las vueltas entre startIndex y endIndex
    let roundNum = 1;
    for (let i = startIndex; i < endIndex; i++) {
        if (elements[i].getAttribute('data-type') === 'round') {
            const label = elements[i].querySelector('.input-group-text');
            if (label) label.textContent = `Vuelta ${roundNum++}`;
        }
    }
}