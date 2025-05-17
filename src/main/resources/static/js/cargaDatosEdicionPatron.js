/***********************************************************************************
** FICHERO PARA LAS FUNCIONES QUE DEFINEN LA CARGA DE DATOS DE EDICIONPATRON.HTML **
************************************************************************************/

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
    document.querySelector('input[placeholder="Tu nombre o alias"]').value = patron.usuario.nombre || '';
    document.querySelector('textarea[placeholder*="Breve"]').value = patron.informacion.descripcion || '';
    document.querySelector('select.form-select#idioma').value = patron.informacion.idioma || '';
    document.querySelector('select.form-select#unidad').value = patron.informacion.unidad || '';
    document.querySelector('select.form-select#dificultad').value = patron.informacion.dificultad || '';
    document.querySelector('input[aria-label="Euro"]').value = patron.precio || '';

    document.querySelector('input[placeholder*="Lana"]').value = patron.materiales.lanas || '';
    document.querySelector('input[placeholder*="5 mm"]').value = patron.materiales.agujaGanchillo || '';
    document.querySelector('input[placeholder*="Sí / No"]').value = patron.materiales.agujaLanera || '';
    document.querySelector('input[placeholder*="Marcadores"]').value = patron.materiales.otros || '';
    
    document.querySelector('textarea[placeholder*="pb = punto bajo"]').value = patron.abreviaturas || '';
    document.querySelector('input[placeholder*="gorro"]').value = (patron.tags || []).join(', ');

    //Imagenes para la vista previa
    const previewContainer = document.getElementById('div-imagenes-muestra');
    if (Array.isArray(patron.imagenes)) {1
        patron.imagenes.forEach(src => {
            const img = document.createElement('img');
            img.className = 'img-preview';
            img.src = src;
            img.style.width = '100px';
            img.style.height = '100px';
            img.style.objectFit = 'cover';
            img.style.borderRadius = '8px';
            previewContainer.appendChild(img);
        });
    };

    // Instrucciones
    
    if (Array.isArray(patron.instrucciones)) {
        patron.instrucciones.forEach(seccion => {
            addSeccion();
            const ultima = document.querySelectorAll('#secciones-patron > .card');
            const nuevaSeccion = ultima[ultima.length - 1];
            nuevaSeccion.querySelector('.txt-titulo').value = seccion.titulo;

            const contenedor = nuevaSeccion.querySelector('[id$="-contenido"]');

            seccion.contenido.forEach(item => {
                if (item.tipo === 'subtitulo') {
                    addSubtitle(contenedor.id);
                    contenedor.lastChild.querySelector('input').value = item.texto;
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
                <h5 class="mb-0">
                    <input type="text" class="form-control txt-titulo mb-3" placeholder="Título de la sección">
                </h5>
                <button class="btn btn-outline-danger" onclick="removeElement(this, 'seccion')">
                    <i class="bi bi-trash3"></i>
                </button>
            </div>
            
            <div id="${seccionId}-contenido" data-round-count="0"></div>

            <div class="btn-group mb-3" role="group">
                <button class="btn btn-outline-primary btn-sm" onclick="addSubtitle('${seccionId}-contenido')">Añadir subtítulo</button>
                <button class="btn btn-outline-secondary btn-sm" onclick="addInfo('${seccionId}-contenido')">Añadir información</button>
                <button class="btn btn-outline-success btn-sm" onclick="addRound('${seccionId}-contenido')">Añadir vuelta</button>
                <button class="btn btn-outline-dark btn-sm" onclick="addImagen('${seccionId}-contenido')">Añadir imagen</button>
            </div>            
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
            <input type="text" class="form-control txt-subtitulo me-2" placeholder="Subtítulo">
            <button class="btn btn-outline-danger" onclick="removeElement(this, '')"><i class="bi bi-trash3"></i></button>
        `;
        container.appendChild(div);
        container.setAttribute('data-round-count', '0');
    }

    
    function addInfo(seccionId) {
        const container = document.getElementById(seccionId);
        const div = document.createElement('div');
        div.className = 'mb-2 d-flex align-items-center';
        div.innerHTML = `
        <textarea class="form-control" placeholder="Información adicional"></textarea>
        <button class="btn btn-outline-danger" onclick="removeElement(this, '')"><i class="bi bi-trash3"></i></button>
        `;
        container.appendChild(div);
    }
    
    function addImagen(seccionId, urls = []) {
        const container = document.getElementById(seccionId);
        const wrapper = document.createElement('div');
        wrapper.className = 'input-group-imagen mb-3';
        wrapper.setAttribute('data-type', 'imagen');

        const divInputGroup = document.createElement('div');
        divInputGroup.className = 'input-group mb-2 div-input-images';

        const input = document.createElement('input');
        input.type = 'file';
        input.accept = 'image/*';
        input.className = 'form-control';
        input.multiple = true;

        const removeBtn = document.createElement('button');
        removeBtn.className = 'btn btn-outline-danger';
        removeBtn.type = 'button';
        removeBtn.innerHTML = '<i class="bi bi-trash3"></i>';
        removeBtn.onclick = () => wrapper.remove();

        const previewContainer = document.createElement('div');
        previewContainer.className = 'd-flex flex-wrap gap-2 mt-2';

        input.onchange = function () {
            previewContainer.innerHTML = '';
            Array.from(input.files).reverse().forEach(file => {
                const reader = new FileReader();
                reader.onload = e => {
                    const img = document.createElement('img');
                    img.className = 'img-preview';
                    img.src = e.target.result;
                    img.style.width = '100px';
                    img.style.height = '100px';
                    img.style.objectFit = 'cover';
                    img.style.borderRadius = '8px';
                    previewContainer.appendChild(img);
                };
                reader.readAsDataURL(file);
            });
        };

        divInputGroup.appendChild(input);
        divInputGroup.appendChild(removeBtn);
        wrapper.appendChild(divInputGroup);
        wrapper.appendChild(previewContainer);
        container.appendChild(wrapper);

        // Si se pasaron URLs (imágenes ya guardadas), mostrarlas
        if (urls.length > 0) {
            previewContainer.innerHTML = '';
            urls.forEach(url => {
                const img = document.createElement('img');
                img.className = 'img-preview';
                img.src = url;
                img.style.width = '100px';
                img.style.height = '100px';
                img.style.objectFit = 'cover';
                img.style.borderRadius = '8px';
                previewContainer.appendChild(img);
            });
        }
    }


  
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
        input.className = 'form-control';
        input.placeholder = 'Instrucciones de la vuelta';

        const removeBtn = document.createElement('button');
        removeBtn.className = 'btn btn-outline-danger';
        removeBtn.type = 'button';
        removeBtn.innerHTML = '<i class="bi bi-trash3"></i>';
        removeBtn.onclick = function () {
            removeElement(this);
        };

        group.appendChild(span);
        group.appendChild(input);
        group.appendChild(removeBtn);

        seccionContent.appendChild(group);
    }


    function removeElement(button, tipo) {
        if (tipo === 'seccion') {
            const seccion = button.closest('.card');
            if (seccion) {
                const confirmed = confirm('¿Estás seguro de que quieres eliminar esta sección completa?');
                if (confirmed) {
                    seccion.remove();
                }
            }
        } else {
            const element = button.closest('.input-group, .d-flex, div.mb-2, div.mb-3');
            if (element) {
                const container = element.parentElement;
                const siblings = Array.from(container.children);
                const deletedIndex = siblings.indexOf(element); // Captura el índice antes de eliminar
                element.remove();
                renumberRounds(container, deletedIndex);
            }
        }
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