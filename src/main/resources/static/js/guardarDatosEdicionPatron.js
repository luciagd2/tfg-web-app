/********************************************************************************
**********     FICHERO PARA LAS FUNCIONES QUE DEFINEN COMO GUARDAR     **********
**********         LOS DATOS DEL PATRON DE EDICIONPATRON.HTML          **********
*********************************************************************************/
//TODO: Eliminar el código anterior (comentado con ////)

//TODO: ¿obsoleto? los datos e instrucciones se cargan en el html, instrucciones seguro que si

function guardarPatron(patron) {

  console.log("Dentro de guardarPatron");
  //const patron = {};

  // Título y autor
  patron.setTitulo(document.querySelector('input[placeholder="Ej: Gorro básico de invierno"]').value.trim());

  // Información general
  patron.setDescripcion(document.querySelector('textarea[placeholder*="Breve"]').value.trim());
  patron.setIdioma(document.querySelector('select#idioma').value);
  patron.setUnidad(document.querySelector('select#unidad').value);
  patron.setDificultad(document.querySelector('select#dificultad').value);

  // Materiales
  patron.setLanas(document.querySelector('input[placeholder*="Lana"]').value.trim());
  patron.setAgujaGanchillo(document.querySelector('input[placeholder*="5 mm"]').value.trim());
  patron.setAgujadaLanera(document.querySelector('input[placeholder*="Sí / No"]').value.trim());
  patron.setOtros(document.querySelector('input[placeholder*="Marcadores"]').value.trim());

  // Abreviaturas y tags
  patron.setAbreviaturas(document.querySelector('textarea[placeholder*="pb = punto bajo"]').value.trim());
  const tags = document.querySelector('input[placeholder*="gorro"]').value
      .split(',')
      .map(tag => tag.trim())
      .filter(tag => tag !== '');
  patron.setTags(tags);
  //1  <- TODO: no se porque estaba este 1, creo que lo puse sin querer, ¿quitar?
  // Instrucciones — SERIALIZADAS como string JSON
  const instruccionesArray = recopilarInstrucciones();
  patron.instrucciones = JSON.stringify(instruccionesArray);
  localStorage.setItem("patronSeleccionado", JSON.stringify(patron));

  alert("Patrón guardado correctamente.");
}

async function subirImagen(file) {
  const formData = new FormData();
  formData.append("file", file);

  const response = await fetch("http://localhost:8080/api/imagenes/subir", {
    method: "POST",
    body: formData
  });

  if (!response.ok) throw new Error("Error al subir imagen");

  const data = await response.json();
  return data.url; // Por ejemplo: /imagenes/patrones/archivo.jpg
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
        previews.forEach(async img => {
          const file = obtenerArchivoDeImgPreview(img); // <- Necesitas guardar el File original o usar un input hidden
          const url = await subirImagen(file);
          contenido.push({ tipo: 'imagen', contenido: url });
        });
      }
    });

    instrucciones.push({ titulo, contenido });
  });

  return instrucciones;
}
