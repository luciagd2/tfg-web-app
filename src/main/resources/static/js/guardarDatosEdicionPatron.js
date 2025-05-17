/********************************************************************************
**********     FICHERO PARA LAS FUNCIONES QUE DEFINEN COMO GUARDAR     **********
**********         LOS DATOS DEL PATRON DE EDICIONPATRON.HTML          **********
*********************************************************************************/

function guardarPatron() {
  const patron = {};

  // Título y autor
  patron.titulo = document.querySelector('input[placeholder="Ej: Gorro básico de invierno"]').value.trim();
  patron.usuario = {
    nombre: document.querySelector('input[placeholder="Tu nombre o alias"]').value.trim(),
    imagen: localStorage.getItem("patronSeleccionado").usuario.imagen
  };

  // Información general
  patron.informacion = {
    descripcion: document.querySelector('textarea[placeholder*="Breve"]').value.trim(),
    idioma: document.querySelector('select#idioma').value,
    unidad: document.querySelector('select#unidad').value,
    dificultad: document.querySelector('select#dificultad').value
  };

  // Materiales
  patron.materiales = {
    lanas: document.querySelector('input[placeholder*="Lana"]').value.trim(),
    agujaGanchillo: document.querySelector('input[placeholder*="5 mm"]').value.trim(),
    agujaLanera: document.querySelector('input[placeholder*="Sí / No"]').value.trim(),
    otros: document.querySelector('input[placeholder*="Marcadores"]').value.trim()
  };

  // Abreviaturas y tags
  patron.abreviaturas = document.querySelector('textarea[placeholder*="pb = punto bajo"]').value.trim();
  patron.tags = document.querySelector('input[placeholder*="gorro"]').value.split(',').map(t => t.trim());

  // Instrucciones
  patron.instrucciones = recopilarInstrucciones();

  // Opción: guardar en localStorage (o enviarlo al servidor con fetch/post)
  localStorage.setItem("patronSeleccionado", JSON.stringify(patron));

  alert("Patrón guardado correctamente.");
}



function recopilarInstrucciones() {
  const secciones = document.querySelectorAll('#secciones-patron .card-seccion');
  const instrucciones = [];

  secciones.forEach(seccion => {
    const titulo = seccion.querySelector('.txt-titulo').value;
    const contenido = [];

    const elementos = seccion.querySelectorAll('[id$="-contenido"] > *');

    elementos.forEach(el => {
      if (el.classList.contains('input-group')) {
        const span = el.querySelector('.input-group-text');
        const input = el.querySelector('input');
        if (span && input) {
          // Es una vuelta
          contenido.push({ tipo: 'vuelta', texto: input.value });
        }
      } else if (el.classList.contains('d-flex') && el.querySelector('.txt-subtitulo')) {
        contenido.push({ tipo: 'subtitulo', texto: el.querySelector('input').value });
      } else if (el.querySelector('textarea')) {
        contenido.push({ tipo: 'info', texto: el.querySelector('textarea').value });
      } else if (el.classList.contains('input-group-imagen')) {
        const previews = el.querySelectorAll('img.img-preview');
        previews.forEach(img => {
          contenido.push({ tipo: 'imagen', contenido: img.src }); // usar la URL directa, no base64
        });
      }
    });

    instrucciones.push({ titulo, contenido });
  });

  return instrucciones;
}
