/******************************************************************
***** FICHERO PARA DEFINIR LA ESTRUCTURA DE DATOS DE PATRONES *****
*******************************************************************/

const patrones = [
    {
      id: "patron-001",
      titulo: "Patrón de Osito",
      precio: "Gratis",
      publicado: true,
      usuario: {
          idUsuario: "usuario-001",
          nombre: "usu1",
          imagen: "imagenes/osito.jpg"
      },
      informacion: {
          dificultad: "Intermedio",
          descripcion: "Patrón detallado paso a paso para tejer un osito de peluche.",
          idioma: "Español",
          unidad: "Centímetros"
      },
      materiales: {
          lanas: "Lana chenilla beige y marrón",
          agujaGanchillo: "3.5 mm",
          agujaLanera: "Sí",
          otros: "Ojos de seguridad, relleno"
      },
      abreviaturas: "pb = punto bajo, aum = aumento, dism = disminución",
      tags: ["osito", "fiesta", "lana de chenilla"],
      imagenes: [
          "imagenes/osito.jpg",
          "imagenes/osito-izquierda.png",
          "imagenes/osito-derecha.png"
      ],
      resenas: [
          {
              nombre: "AnaCrochet",
              imagenUsuario: "imagenes/violet1.png",
              imagenPatron: "imagenes/osito.jpg",
              puntuacion: 5,
              comentario: "¡Me encantó el patrón! Súper claro y fácil de seguir."
          },
          {
              nombre: "PedroHilo",
              imagenUsuario: "imagenes/violet2.png",
              puntuacion: 4,
              comentario: "Muy bueno, aunque me costó un poco la parte de las piernas."
          }
      ],
      instrucciones: [
        {
            titulo: "Cuerpo del osito",
            contenido: [
            { tipo: "subtitulo", texto: "Parte superior" },
            { tipo: "vuelta", texto: "6 pb en anillo mágico" },
            { tipo: "vuelta", texto: "6 aum (12)" },
            { tipo: "info", texto: "Asegúrate de ajustar bien los puntos." },
            { tipo: "imagen", contenido: "imagenes/osito-derecha.png" },
            { tipo: "subtitulo", texto: "Parte inferior" },
            { tipo: "vuelta", texto: "Vuelta 10: 1 pb en cada punto (18)" }
            ]
        },
        {
            titulo: "Orejas",
            contenido: [
            { tipo: "vuelta", texto: "6 pb en anillo mágico" },
            { tipo: "vuelta", texto: "6 aum (12)" },
            { tipo: "imagen", contenido: "imagenes/osito-derecha.png" }
            ]
        }
        ]
    },
    {
      id: "patron-002",
      titulo: "Patrón de Violet Evergarden",
      precio: 4.99,      
      publicado: false,
      descripcionCorta: "Patrón para esta preciosa muñeca de la protagonista del famos anime Violet Evergarden.",
      usuario: {
          idUsuario: "usuario-001",
          nombre: "usu1",
          imagen: "imagenes/violet1.png"
      },
      informacion: {
          dificultad: "Avanzado",
          descripcion: "Incluye fotos paso a paso.",
          idioma: "Español",
          unidad: "Centímetros"
      },
      materiales: {
          lanas: "Algodón de colores amarillo, blanco, marrón, azul, carne",
          agujaGanchillo: "3 mm",
          agujaLanera: "Sí",
          otros: "Ojos de seguridad, relleno"
      },
      abreviaturas: "pb = punto bajo, cad = cadeneta, mpa = medio punto alto",
      tags: ["anime", "amigurumi", "muñeca"],
      imagenes: [
          "imagenes/violet1.png",
          "imagenes/violet2.png",
          "imagenes/violet3.png",
          "imagenes/violet4.png",
          "imagenes/violet5.png"
      ],
      resenas: [],
      instrucciones: []
      },
  ];
  