-- SELECT
select * from tfg_db.usuario;
select * from tfg_db.usuario_seguidores;
select * from tfg_db.usuario_patrones_guardados;
select * from tfg_db.usuario_patrones_comprados;
select * from tfg_db.usuario_patrones_empezados;
select * from tfg_db.patron;
select * from tfg_db.patron_tags;
select * from tfg_db.patron_imagenes;
select * from tfg_db.review;
select * from tfg_db.compra;
select * from tfg_db.notificacion;

-- USUARIOS CREADORES
-- Usuarios creadores
INSERT INTO tfg_db.usuario (email, password, nombre_usuario, descripcion_usuario, es_creador, imagen_perfil)
VALUES
('creador1@ejemplo.com', '123', 'CreadorUno', 'Apasionado del crochet vintage.', true, 'imagenes/perfiles/1748894766336_gato2.jpg'),
('creador2@ejemplo.com', '123', 'CreadorDos', 'Diseña patrones modernos y minimalistas.', true, 'imagenes/perfiles/1748894689079_penguin.jpg'),
('creador3@ejemplo.com', '123', 'CreadorTres', 'Especialista en amigurumis.', true, 'imagenes/perfiles/1748895100189_bufanda.jpg'),
('creador4@ejemplo.com', '123', 'CreadorCuatro', 'Instructora certificada con 10 años de experiencia.', true, 'imagenes/perfiles/1748895354090_leon_bebe.jpg'),
('creador5@ejemplo.com', '123', 'CreadorCinco', 'Creador de patrones inspirados en la naturaleza.', true, 'imagenes/perfiles/1748895498375_osito.jpg');

-- USUARIOS NO CREADORES
INSERT INTO tfg_db.usuario (email, password, nombre_usuario, es_creador, imagen_perfil)
VALUES
('usuario1@ejemplo.com', '123', 'UsuarioUno', false, 'imagenes/perfiles/1748895552081_chaleco.jpg'),
('usuario2@ejemplo.com', '123', 'UsuarioDos', false, 'imagenes/perfiles/1748895601212_Untitled7.png'),
('usuario3@ejemplo.com', '123', 'UsuarioTres', false, 'imagenes/perfiles/1748895622178_Untitled6.png'),
('usuario4@ejemplo.com', '123', 'UsuarioCuatro', false, 'imagenes/perfiles/1748895641769_Untitled4.png'),
('usuario5@ejemplo.com', '123', 'UsuarioCinco', false, 'imagenes/perfiles/1748895661610_Untitled3.png');

-- RELACIONES DE SEGUIMIENTO (solo se puede seguir a los creadores)
-- Usuario 6 sigue a 1, 2 y 3
INSERT INTO tfg_db.usuario_seguidores (usaurio_id, seguidor) VALUES (1, 6), (2, 6), (3, 6);
-- Usuario 7 sigue a 1, 3 y 4
INSERT INTO tfg_db.usuario_seguidores (usaurio_id, seguidor) VALUES (1, 7), (3, 7), (4, 7);
-- Usuario 8 sigue a 2, 4 y 5
INSERT INTO tfg_db.usuario_seguidores (usaurio_id, seguidor) VALUES (2, 8), (4, 8), (5, 8);
-- Usuario 9 sigue a 1 y 5
INSERT INTO tfg_db.usuario_seguidores (usaurio_id, seguidor) VALUES (1, 9), (5, 9);
-- Usuario 10 sigue a todos los creadores
INSERT INTO tfg_db.usuario_seguidores (usaurio_id, seguidor) 
VALUES (1, 10), (2, 10), (3, 10), (4, 10), (5, 10);

-- CREADOR 1: 3 publicados, 2 borradores
INSERT INTO tfg_db.patron 
(titulo, precio, estado, id_creador, dificultad, descripcion, idioma, unidad, lanas, aguja_ganchillo, aguja_lanera, otros, abreviaturas, instrucciones)
VALUES 
('Gatito Curioso', 6.1, 'Publicado', 1, 'Intermedio', 'Amigurumi de gatito curioso.', 'Frances', 'Centímetros', 'Lana de algodón', '3mm', 'Aguja lanera', 'Ojos de seguridad y fibra de relleno', 'pb, pa, pr, dis', '[{"titulo":"Cabeza","contenido":[{"tipo":"vuelta","texto":"am 6 pb"},{"tipo":"vuelta","texto":"6 aum"},{"tipo":"vuelta","texto":"(1pb, 2 aum)x6"},{"tipo":"imagen","contenido":"[\"imagenes/patrones/1748890162099_gato2.jpg\"]"},{"tipo":"subtitulo","texto":"Orejas"},{"tipo":"vuelta","texto":"am 6 pb"},{"tipo":"subtitulo","texto":"Hacer dos"}]},{"titulo":"Cuerpo","contenido":[{"tipo":"info","texto":"El cuerpo se hace todo en una sola pieza"},{"tipo":"vuelta","texto":"am 8 pb"},{"tipo":"imagen","contenido":"[\"imagenes/patrones/1748890162111_gato2.jpg\",\"imagenes/patrones/1748890162124_gato3.jpg\"]"}]}]');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (1, 'juguete');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (1, 'niños');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (1, 'amigurumi');

INSERT INTO tfg_db.patron 
(titulo, precio, estado, id_creador, dificultad, descripcion, idioma, unidad, lanas, aguja_ganchillo, aguja_lanera, otros, abreviaturas, instrucciones)
VALUES 
('Blusa Holgada', 4.58, 'Publicado', 1, 'Avanzado', 'Prenda de vestir: blusa holgada.', 'Frances', 'Centímetros', 'Lana de algodón', '3mm', 'Aguja lanera', 'Botones decorativos', 'pb, pa, pr, dis', '');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (2, 'verano');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (2, 'accesorio');

INSERT INTO tfg_db.patron 
(titulo, precio, estado, id_creador, dificultad, descripcion, idioma, unidad, lanas, aguja_ganchillo, aguja_lanera, otros, abreviaturas, instrucciones)
VALUES 
('Zorro Astuto', 2.92, 'Publicado', 1, 'Avanzado', 'Amigurumi de zorro astuto.', 'Inglés', 'Centímetros', 'Lana de algodón', '3mm', 'Aguja lanera', '', 'pb, pa, pr, dis', '');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (3, 'juguete');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (3, 'animal');

INSERT INTO tfg_db.patron 
(titulo, precio, estado, id_creador, dificultad, descripcion, idioma, unidad, lanas, aguja_ganchillo, aguja_lanera, otros, abreviaturas, instrucciones)
VALUES 
('Chaleco Casual', 2.54, 'Borrador', 1, 'Principiante', 'Prenda de vestir: chaleco casual.', 'Inglés', 'Centímetros', 'Lana de algodón', '3mm', 'Aguja lanera', '', 'pb, pa, pr, dis', '');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (4, 'accesorio');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (4, 'verano');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (4, 'ropa');

INSERT INTO tfg_db.patron 
(titulo, precio, estado, id_creador, dificultad, descripcion, idioma, unidad, lanas, aguja_ganchillo, aguja_lanera, otros, abreviaturas, instrucciones)
VALUES 
('Gatito Curioso', 5.42, 'Borrador', 1, 'Principiante', 'Amigurumi de gatito curioso.', 'Inglés', 'Centímetros', 'Lana de algodón', '3mm', 'Aguja lanera', 'Fibra de relleno', 'pb, pa, pr, dis', '');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (5, 'juguete');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (5, 'muñeco');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (5, 'amigurumi');

-- CREADOR 2: 4 publicados, 1 borradores
INSERT INTO tfg_db.patron 
(titulo, precio, estado, id_creador, dificultad, descripcion, idioma, unidad, lanas, aguja_ganchillo, aguja_lanera, otros, abreviaturas, instrucciones)
VALUES 
('Muñeca Clásica', 4.6, 'Publicado', 2, 'Avanzado', 'Amigurumi de muñeca clásica.', 'Frances', 'Centímetros', 'Lana de algodón', '3mm', 'Aguja lanera', 'Fibra de relleno', 'pb, pa, pr, dis', '');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (6, 'niños');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (6, 'animal');

INSERT INTO tfg_db.patron 
(titulo, precio, estado, id_creador, dificultad, descripcion, idioma, unidad, lanas, aguja_ganchillo, aguja_lanera, otros, abreviaturas, instrucciones)
VALUES 
('León Bebé', 5.25, 'Publicado', 2, 'Principiante', 'Amigurumi de león bebé.', 'Español', 'Centímetros', 'Lana de algodón', '3mm', 'Aguja lanera', '', 'pb, pa, pr, dis', '');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (7, 'animal');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (7, 'muñeco');

INSERT INTO tfg_db.patron 
(titulo, precio, estado, id_creador, dificultad, descripcion, idioma, unidad, lanas, aguja_ganchillo, aguja_lanera, otros, abreviaturas, instrucciones)
VALUES 
('Pingüino Polar', 3.26, 'Publicado', 2, 'Intermedio', 'Amigurumi de pingüino polar.', 'Español', 'Centímetros', 'Lana de algodón', '3mm', 'Aguja lanera', 'Ojos de seguridad y fibra de relleno', 'pb, pa, pr, dis', '');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (8, 'juguete');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (8, 'amigurumi');

INSERT INTO tfg_db.patron 
(titulo, precio, estado, id_creador, dificultad, descripcion, idioma, unidad, lanas, aguja_ganchillo, aguja_lanera, otros, abreviaturas, instrucciones)
VALUES 
('Gorro de Invierno', 5.51, 'Publicado', 2, 'Avanzado', 'Prenda de vestir: gorro de invierno.', 'Frances', 'Centímetros', 'Lana de algodón', '3mm', 'Aguja lanera', '', 'pb, pa, pr, dis', '');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (9, 'bebé');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (9, 'ropa');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (9, 'invierno');

INSERT INTO tfg_db.patron 
(titulo, precio, estado, id_creador, dificultad, descripcion, idioma, unidad, lanas, aguja_ganchillo, aguja_lanera, otros, abreviaturas, instrucciones)
VALUES 
('Conjunto para Bebé', 5.77, 'Borrador', 2, 'Intermedio', 'Prenda de vestir: conjunto para bebé.', 'Inglés', 'Centímetros', 'Lana de algodón', '3mm', 'Aguja lanera', 'Botones decorativos', 'pb, pa, pr, dis', '');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (10, 'verano');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (10, 'accesorio');

-- CREADOR 3: 4 publicados, 3 borradores
INSERT INTO tfg_db.patron 
(titulo, precio, estado, id_creador, dificultad, descripcion, idioma, unidad, lanas, aguja_ganchillo, aguja_lanera, otros, abreviaturas, instrucciones)
VALUES 
('Top de Verano', 4.4, 'Publicado', 3, 'Principiante', 'Prenda de vestir: top de verano.', 'Inglés', 'Centímetros', 'Lana de algodón', '3mm', 'Aguja lanera', 'Cinta elástica', 'pb, pa, pr, dis', '');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (11, 'ropa');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (11, 'invierno');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (11, 'bebé');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (11, 'accesorio');

INSERT INTO tfg_db.patron 
(titulo, precio, estado, id_creador, dificultad, descripcion, idioma, unidad, lanas, aguja_ganchillo, aguja_lanera, otros, abreviaturas, instrucciones)
VALUES 
('Bufanda Cálida', 5.08, 'Publicado', 3, 'Avanzado', 'Prenda de vestir: bufanda cálida.', 'Frances', 'Centímetros', 'Lana de algodón', '3mm', 'Aguja lanera', '', 'pb, pa, pr, dis', '');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (12, 'invierno');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (12, 'ropa');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (12, 'verano');

INSERT INTO tfg_db.patron 
(titulo, precio, estado, id_creador, dificultad, descripcion, idioma, unidad, lanas, aguja_ganchillo, aguja_lanera, otros, abreviaturas, instrucciones)
VALUES 
('Osito Dormilón', 3.9, 'Publicado', 3, 'Intermedio', 'Amigurumi de osito dormilón.', 'Inglés', 'Centímetros', 'Lana de algodón', '3mm', 'Aguja lanera', '', 'pb, pa, pr, dis', '');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (13, 'amigurumi');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (13, 'muñeco');
INSERT INTO tfg_db.patron 
(titulo, precio, estado, id_creador, dificultad, descripcion, idioma, unidad, lanas, aguja_ganchillo, aguja_lanera, otros, abreviaturas, instrucciones)
VALUES 
('Dragón Miniatura', 4.3, 'Publicado', 3, 'Principiante', 'Amigurumi de dragón miniatura.', 'Inglés', 'Centímetros', 'Lana de algodón', '3mm', 'Aguja lanera', 'Fibra de relleno', 'pb, pa, pr, dis', '');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (14, 'muñeco');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (14, 'niños');

INSERT INTO tfg_db.patron 
(titulo, precio, estado, id_creador, dificultad, descripcion, idioma, unidad, lanas, aguja_ganchillo, aguja_lanera, otros, abreviaturas, instrucciones)
VALUES 
('Pingüino Polar', 6.47, 'Borrador', 3, 'Intermedio', 'Amigurumi de pingüino polar.', 'Frances', 'Centímetros', 'Lana de algodón', '3mm', 'Aguja lanera', 'Ojos de seguridad y fibra de relleno', 'pb, pa, pr, dis', '');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (15, 'animal');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (15, 'juguete');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (15, 'amigurumi');

INSERT INTO tfg_db.patron 
(titulo, precio, estado, id_creador, dificultad, descripcion, idioma, unidad, lanas, aguja_ganchillo, aguja_lanera, otros, abreviaturas, instrucciones)
VALUES 
('Chaleco Casual', 3.91, 'Borrador', 3, 'Principiante', 'Prenda de vestir: chaleco casual.', 'Frances', 'Centímetros', 'Lana de algodón', '3mm', 'Aguja lanera', 'Botones decorativos', 'pb, pa, pr, dis', '');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (16, 'accesorio');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (16, 'invierno');

INSERT INTO tfg_db.patron 
(titulo, precio, estado, id_creador, dificultad, descripcion, idioma, unidad, lanas, aguja_ganchillo, aguja_lanera, otros, abreviaturas, instrucciones)
VALUES 
('Osito Dormilón', 5.32, 'Borrador', 3, 'Avanzado', 'Amigurumi de osito dormilón.', 'Frances', 'Centímetros', 'Lana de algodón', '3mm', 'Aguja lanera', '', 'pb, pa, pr, dis', '');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (17, 'amigurumi');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (17, 'niños');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (17, 'animal');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (17, 'juguete');

-- CREADOR 4: 5 publicados, 1 borradores
INSERT INTO tfg_db.patron 
(titulo, precio, estado, id_creador, dificultad, descripcion, idioma, unidad, lanas, aguja_ganchillo, aguja_lanera, otros, abreviaturas, instrucciones)
VALUES ('Muñeca Madoka Kaname', 5.50, 'Publicado', 1, 'Avanzado', 'Amigurumi detallado de Madoka Kaname, personaje de Madoka Magica.', 'Español', 'Centímetros', 'Algodón', '2.5mm', 'Sí', 'Ojos de seguridad, fibra de relleno, tela rosa para vestido', 'pb, pa, pr', '');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (18, 'anime');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (18, 'muñeco');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (18, 'coleccionista');

INSERT INTO tfg_db.patron 
(titulo, precio, estado, id_creador, dificultad, descripcion, idioma, unidad, lanas, aguja_ganchillo, aguja_lanera, otros, abreviaturas, instrucciones)
VALUES 
('León Bebé', 3.13, 'Publicado', 4, 'Principiante', 'Amigurumi de león bebé.', 'Inglés', 'Centímetros', 'Lana de algodón', '3mm', 'Aguja lanera', 'Ojos de seguridad y fibra de relleno', 'pb, pa, pr, dis', '');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (19, 'muñeco');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (19, 'animal');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (19, 'amigurumi');

INSERT INTO tfg_db.patron 
(titulo, precio, estado, id_creador, dificultad, descripcion, idioma, unidad, lanas, aguja_ganchillo, aguja_lanera, otros, abreviaturas, instrucciones)
VALUES ('Muñeca Violet Evergarden', 5.75, 'Publicado', 2, 'Avanzado', 'Amigurumi inspirado en Violet Evergarden, con uniforme azul.', 'Español', 'Centímetros', 'Algodón', '3mm', 'Sí', 'Botones decorativos, ojos de seguridad, fibra de relleno', 'pb, pa, pr', '');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (20, 'amigurumi');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (20, 'muñeca');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (20, 'anime');

INSERT INTO tfg_db.patron 
(titulo, precio, estado, id_creador, dificultad, descripcion, idioma, unidad, lanas, aguja_ganchillo, aguja_lanera, otros, abreviaturas, instrucciones)
VALUES ('Kuromi Amigurumi', 4.50, 'Publicado', 3, 'Intermedio', 'Amigurumi del personaje Kuromi, de Sanrio.', 'Español', 'Centímetros', 'Algodón', '3mm', 'Sí', 'Fieltro negro y rosa, ojos de seguridad, relleno', 'pb, pa, pr', '');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (21, 'amigurumi');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (21, 'sanrio');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (21, 'kuromi');

INSERT INTO tfg_db.patron 
(titulo, precio, estado, id_creador, dificultad, descripcion, idioma, unidad, lanas, aguja_ganchillo, aguja_lanera, otros, abreviaturas, instrucciones)
VALUES ('Pochacco Amigurumi', 3.99, 'Publicado', 3, 'Principiante', 'Amigurumi sencillo del tierno perrito Pochacco.', 'Español', 'Centímetros', 'Algodón', '3.5mm', 'Sí', 'Relleno de fibra, ojos de seguridad', 'pb, pa, pr', '');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (22, 'amigurumi');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (22, 'sanrio');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (22, 'perro');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (22, 'juguete');

INSERT INTO tfg_db.patron 
(titulo, precio, estado, id_creador, dificultad, descripcion, idioma, unidad, lanas, aguja_ganchillo, aguja_lanera, otros, abreviaturas, instrucciones)
VALUES 
('Muñeca Clásica', 3.4, 'Borrador', 4, 'Avanzado', 'Amigurumi de muñeca clásica.', 'Español', 'Centímetros', 'Lana de algodón', '3mm', 'Aguja lanera', '', 'pb, pa, pr, dis', '');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (23, 'amigurumi');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (23, 'animal');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (23, 'niños');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (23, 'muñeco');

-- CREADOR 5: 5 publicados, 2 borradores
INSERT INTO tfg_db.patron 
(titulo, precio, estado, id_creador, dificultad, descripcion, idioma, unidad, lanas, aguja_ganchillo, aguja_lanera, otros, abreviaturas, instrucciones)
VALUES ('My Melody Amigurumi', 4.25, 'Publicado', 4, 'Intermedio', 'Amigurumi de My Melody con capucha rosa.', 'Español', 'Centímetros', 'Algodón', '3mm', 'Sí', 'Ojos de seguridad, lazo decorativo, fibra de relleno', 'pb, pa, pr', '');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (24, 'amigurumi');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (24, 'sanrio');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (24, 'conejo');

INSERT INTO tfg_db.patron 
(titulo, precio, estado, id_creador, dificultad, descripcion, idioma, unidad, lanas, aguja_ganchillo, aguja_lanera, otros, abreviaturas, instrucciones)
VALUES ('Pompompurin Amigurumi', 3.80, 'Publicado', 4, 'Principiante', 'Amigurumi del adorable perro Pompompurin con su boina marrón.', 'Español', 'Centímetros', 'Algodón', '3.5mm', 'Sí', 'Fieltro marrón, ojos de seguridad, relleno', 'pb, pa, pr', '');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (25, 'amigurumi');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (25, 'sanrio');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (25, 'perro');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (25, 'juguete');

INSERT INTO tfg_db.patron 
(titulo, precio, estado, id_creador, dificultad, descripcion, idioma, unidad, lanas, aguja_ganchillo, aguja_lanera, otros, abreviaturas, instrucciones)
VALUES 
('Osito', 3.9, 'Publicado', 5, 'Principiante', 'Amigurumi de osito.', 'Español', 'Centímetros', 'Lana de algodón', '3mm', 'Aguja lanera', '', 'pb, pa, pr, dis', '');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (26, 'muñeco');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (26, 'amigurumi');

INSERT INTO tfg_db.patron (titulo, precio, estado, id_creador, dificultad, descripcion, idioma, unidad, lanas, aguja_ganchillo, aguja_lanera, otros, abreviaturas, instrucciones)
VALUES ('Pompompurin Amigurumi', 3.80, 'Publicado', 4, 'Principiante', 'Amigurumi del adorable perro Pompompurin con su boina marrón.', 'Español', 'Centímetros', 'Algodón', '3.5mm', 'Sí', 'Fieltro marrón, ojos de seguridad, relleno', 'pb, pa, pr', '');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (27, 'amigurumi');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (27, 'sanrio');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (27, 'perro');

INSERT INTO tfg_db.patron 
(titulo, precio, estado, id_creador, dificultad, descripcion, idioma, unidad, lanas, aguja_ganchillo, aguja_lanera, otros, abreviaturas, instrucciones)
VALUES ('Keroppi Amigurumi', 4.10, 'Publicado', 5, 'Intermedio', 'Amigurumi de la rana Keroppi de Sanrio.', 'Español', 'Centímetros', 'Algodón', '3mm', 'Sí', 'Ojos saltones, relleno, hilo rojo para corbata', 'pb, pa, pr', '');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (28, 'amigurumi');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (28, 'sanrio');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (28, 'rana');

INSERT INTO tfg_db.patron 
(titulo, precio, estado, id_creador, dificultad, descripcion, idioma, unidad, lanas, aguja_ganchillo, aguja_lanera, otros, abreviaturas, instrucciones)
VALUES 
('Gatito Curioso', 3.18, 'Borrador', 5, 'Principiante', 'Amigurumi de gatito curioso.', 'Inglés', 'Centímetros', 'Lana de algodón', '3mm', 'Aguja lanera', 'Ojos de seguridad y fibra de relleno', 'pb, pa, pr, dis', '');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (29, 'animal');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (29, 'juguete');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (29, 'niños');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (29, 'muñeco');

INSERT INTO tfg_db.patron 
(titulo, precio, estado, id_creador, dificultad, descripcion, idioma, unidad, lanas, aguja_ganchillo, aguja_lanera, otros, abreviaturas, instrucciones)
VALUES 
('Zorro Astuto', 3.13, 'Borrador', 5, 'Intermedio', 'Amigurumi de zorro astuto.', 'Español', 'Centímetros', 'Lana de algodón', '3mm', 'Aguja lanera', 'Fibra de relleno', 'pb, pa, pr, dis', '');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (30, 'niños');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (30, 'amigurumi');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (30, 'juguete');
INSERT INTO tfg_db.patron_tags (patron_id, tag) VALUES (30, 'animal');

-- REVIEWS
-- Patrones 1
INSERT INTO tfg_db.review (id_patron, id_usuario, imagen, puntuacion, mensaje) VALUES (1, 6, '', 5, '¡Me encantó hacer este amigurumi! El patrón está muy bien explicado.');
INSERT INTO tfg_db.review (id_patron, id_usuario, imagen, puntuacion, mensaje) VALUES (1, 7, '', 4, 'Bonito resultado, aunque algunas partes fueron complicadas.');

-- Patrón 2
INSERT INTO tfg_db.review (id_patron, id_usuario, imagen, puntuacion, mensaje) VALUES (2, 8, '', 3, 'El diseño es bonito pero esperaba más detalle.');
INSERT INTO tfg_db.review (id_patron, id_usuario, imagen, puntuacion, mensaje) VALUES (2, 9, '', 5, 'Muy fácil de seguir. Perfecto para principiantes.');
INSERT INTO tfg_db.review (id_patron, id_usuario, imagen, puntuacion, mensaje) VALUES (2, 10, '', 4, 'Top de verano muy bonito, ideal para regalo.');

-- Patrón 3
INSERT INTO tfg_db.review (id_patron, id_usuario, imagen, puntuacion, mensaje) VALUES (3, 6, '', 5, '¡Me encantó! Mi hija lo amó.');
INSERT INTO tfg_db.review (id_patron, id_usuario, imagen, puntuacion, mensaje) VALUES (3, 8, '', 4, 'Un poco avanzado pero con resultados hermosos.');

-- Patrón 4
INSERT INTO tfg_db.review (id_patron, id_usuario, imagen, puntuacion, mensaje) VALUES (4, 7, '', 2, 'Tuve problemas con algunas instrucciones.');
INSERT INTO tfg_db.review (id_patron, id_usuario, imagen, puntuacion, mensaje) VALUES (4, 9, '', 4, 'Buen patrón. El personaje quedó adorable.');

-- Patrón 5
INSERT INTO tfg_db.review (id_patron, id_usuario, imagen, puntuacion, mensaje) VALUES (5, 10, '', 5, 'Muy divertido de hacer. Recomiendo totalmente.');
INSERT INTO tfg_db.review (id_patron, id_usuario, imagen, puntuacion, mensaje) VALUES (5, 6, '', 3, 'Lindo pero me costó entender algunas abreviaturas.');

-- Patrón 6
INSERT INTO tfg_db.review (id_patron, id_usuario, imagen, puntuacion, mensaje) VALUES (6, 7, '', 4, 'Muy detallado. ¡Quedó igual al personaje!');
INSERT INTO tfg_db.review (id_patron, id_usuario, imagen, puntuacion, mensaje) VALUES (6, 8, '', 4, 'Fácil de seguir y muy tierno.');
INSERT INTO tfg_db.review (id_patron, id_usuario, imagen, puntuacion, mensaje) VALUES (6, 9, '', 5, 'Me encantó hacerlo. Buen patrón.');

-- Patrón 7
INSERT INTO tfg_db.review (id_patron, id_usuario, imagen, puntuacion, mensaje) VALUES (7, 10, '', 5, 'Ideal para regalar. Rápido y fácil.');
INSERT INTO tfg_db.review (id_patron, id_usuario, imagen, puntuacion, mensaje) VALUES (7, 6, '', 4, 'Muy simpático. Me gustó mucho el resultado.');

-- Patrón 8
INSERT INTO tfg_db.review (id_patron, id_usuario, imagen, puntuacion, mensaje) VALUES (8, 7, '', 5, 'Precioso, uno de mis favoritos.');
INSERT INTO tfg_db.review (id_patron, id_usuario, imagen, puntuacion, mensaje) VALUES (8, 8, '', 3, 'Esperaba algo más elaborado.');

-- Patrón 9
INSERT INTO tfg_db.review (id_patron, id_usuario, imagen, puntuacion, mensaje) VALUES (9, 9, '', 4, 'Quedó hermoso. Mi hijo está encantado.');

-- Patrón 10
INSERT INTO tfg_db.review (id_patron, id_usuario, imagen, puntuacion, mensaje) VALUES (10, 10, '', 5, 'Muy bien diseñado. Repetiré con otros patrones del mismo creador.');
INSERT INTO tfg_db.review (id_patron, id_usuario, imagen, puntuacion, mensaje) VALUES (10, 6, '', 4, 'Satisfecha con el resultado.');
INSERT INTO tfg_db.review (id_patron, id_usuario, imagen, puntuacion, mensaje) VALUES (10, 8, '', 5, 'De los mejores patrones que he usado.');

-- PATRONES GUARDADOS
-- Usuario 1 guarda 3 patrones
INSERT INTO tfg_db.usuario_patrones_guardados (usuario_id, patron_id) VALUES (1, 2);
INSERT INTO tfg_db.usuario_patrones_guardados (usuario_id, patron_id) VALUES (1, 5);
INSERT INTO tfg_db.usuario_patrones_guardados (usuario_id, patron_id) VALUES (1, 8);

-- Usuario 2 guarda 2 patrones
INSERT INTO tfg_db.usuario_patrones_guardados (usuario_id, patron_id) VALUES (2, 1);
INSERT INTO tfg_db.usuario_patrones_guardados (usuario_id, patron_id) VALUES (2, 3);

-- Usuario 3 guarda 1 patrón
INSERT INTO tfg_db.usuario_patrones_guardados (usuario_id, patron_id) VALUES (3, 4);

-- Usuario 4 guarda 3 patrones
INSERT INTO tfg_db.usuario_patrones_guardados (usuario_id, patron_id) VALUES (4, 6);
INSERT INTO tfg_db.usuario_patrones_guardados (usuario_id, patron_id) VALUES (4, 7);
INSERT INTO tfg_db.usuario_patrones_guardados (usuario_id, patron_id) VALUES (4, 10);

-- Usuario 5 guarda 2 patrones
INSERT INTO tfg_db.usuario_patrones_guardados (usuario_id, patron_id) VALUES (5, 9);
INSERT INTO tfg_db.usuario_patrones_guardados (usuario_id, patron_id) VALUES (5, 2);

-- Usuario 6 guarda 3 patrones
INSERT INTO tfg_db.usuario_patrones_guardados (usuario_id, patron_id) VALUES (6, 1);
INSERT INTO tfg_db.usuario_patrones_guardados (usuario_id, patron_id) VALUES (6, 5);
INSERT INTO tfg_db.usuario_patrones_guardados (usuario_id, patron_id) VALUES (6, 8);

-- Usuario 7 guarda 2 patrones
INSERT INTO tfg_db.usuario_patrones_guardados (usuario_id, patron_id) VALUES (7, 3);
INSERT INTO tfg_db.usuario_patrones_guardados (usuario_id, patron_id) VALUES (7, 4);

-- Usuario 8 guarda 1 patrón
INSERT INTO tfg_db.usuario_patrones_guardados (usuario_id, patron_id) VALUES (8, 7);

-- Usuario 9 guarda 3 patrones
INSERT INTO tfg_db.usuario_patrones_guardados (usuario_id, patron_id) VALUES (9, 2);
INSERT INTO tfg_db.usuario_patrones_guardados (usuario_id, patron_id) VALUES (9, 6);
INSERT INTO tfg_db.usuario_patrones_guardados (usuario_id, patron_id) VALUES (9, 9);

-- Usuario 10 guarda 1 patrón
INSERT INTO tfg_db.usuario_patrones_guardados (usuario_id, patron_id) VALUES (10, 10);

-- IMAGENES PATRONES
INSERT INTO tfg_db.patron_imagenes (patron_id, imagen)
VALUES
(1, 'imagenes/patrones/1748890162058_gato1.jpg'),
(1, 'imagenes/patrones/1748890162075_gato2.jpg'),
(1, 'imagenes/patrones/1748890162087_gato3.jpg'),
(2, 'imagenes/patrones/1748890566851_blusa.jpg'),
(3, 'imagenes/patrones/1748890889059_zorro.jpg'),
(18, 'imagenes/patrones/1748890911184_27.png'),
(18, 'imagenes/patrones/1748890911209_28.png'),
(18, 'imagenes/patrones/1748890911231_29.png'),
(18, 'imagenes/patrones/1748890911252_30.png'),
(4, 'imagenes/patrones/1748890965704_chaleco.jpg'),
(5, 'imagenes/patrones/1748891061411_gato-curioso.jpg'),
(6, 'imagenes/patrones/1748891281328_muñeca.jpg'),
(7, 'imagenes/patrones/1748891342492_leon.jpg'),
(8, 'imagenes/patrones/1748891385463_penguin.jpg'),
(9, 'imagenes/patrones/1748891658954_gorro_invierno.jpg'),
(20, 'imagenes/patrones/1748891796814_violet_no.jpg'),
(10, 'imagenes/patrones/1748891837413_bebe.jpg'),
(11, 'imagenes/patrones/1748894861549_top.jpg'),
(12, 'imagenes/patrones/1748894926745_bufanda.jpg'),
(13, 'imagenes/patrones/1748894986385_osito-dormilon.jpg'),
(14, 'imagenes/patrones/1748895013165_dragon.jpg'),
(21, 'imagenes/patrones/1748895034384_15.jpg'),
(21, 'imagenes/patrones/1748895034398_16.jpg'),
(21, 'imagenes/patrones/1748895034409_17.jpg'),
(21, 'imagenes/patrones/1748895034420_18.jpg'),
(22, 'imagenes/patrones/1748895046824_10.png'),
(22, 'imagenes/patrones/1748895046846_11.png'),
(22, 'imagenes/patrones/1748895046867_12.png'),
(22, 'imagenes/patrones/1748895046889_13.png'),
(22, 'imagenes/patrones/1748895046912_14.png'),
(19, 'imagenes/patrones/1748895254284_leon_bebe.jpg'),
(24, 'imagenes/patrones/1748895268495_1.png'),
(24, 'imagenes/patrones/1748895268520_2.png'),
(24, 'imagenes/patrones/1748895268544_3.png'),
(24, 'imagenes/patrones/1748895268568_4.png'),
(24, 'imagenes/patrones/1748895268589_5.png'),
(25, 'imagenes/patrones/1748895318659_16.jpg'),
(25, 'imagenes/patrones/1748895318674_17.jpg'),
(25, 'imagenes/patrones/1748895318687_18.jpg'),
(23, 'imagenes/patrones/1748895343546_violet_no.jpg'),
(26, 'imagenes/patrones/1748895473360_osito.jpg'),
(28, 'imagenes/patrones/1748895486859_6.jpg'),
(28, 'imagenes/patrones/1748895486871_7.jpg'),
(28, 'imagenes/patrones/1748895486884_8.jpg'),
(28, 'imagenes/patrones/1748895486897_9.jpg'),
(28, 'imagenes/patrones/1748895486910_10.jpg')