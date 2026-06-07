select*from usuarios;

USE fitgym_db;
GO


DELETE FROM rutinas_lista_ejercicios;
DELETE FROM ejercicios;
DELETE FROM rutinas;
select*from usuarios;

INSERT INTO rutinas (nombre, nivel, tiempo, grupo_muscular) 
VALUES ('Espalda: Densidad', 'INTERMEDIO', '45 min', 'Espalda');

INSERT INTO rutinas (nombre, nivel, tiempo, grupo_muscular) 
VALUES ('Pecho: Fuerza', 'INTERMEDIO', '60 min', 'Pecho');

INSERT INTO rutinas (nombre, nivel, tiempo, grupo_muscular) 
VALUES ('Pierna: Explosivo', 'AVANZADO', '70 min', 'Pierna');


INSERT INTO ejercicios (nombre, series, repeticiones, peso_sugerido) VALUES ('Remo con barra T', 4, 10, 40.0);
INSERT INTO ejercicios (nombre, series, repeticiones, peso_sugerido) VALUES ('Jalón al pecho', 4, 12, 50.0);
INSERT INTO ejercicios (nombre, series, repeticiones, peso_sugerido) VALUES ('Press Banca Plano', 4, 8, 60.0);
INSERT INTO ejercicios (nombre, series, repeticiones, peso_sugerido) VALUES ('Sentadillas', 4, 10, 80.0);


INSERT INTO rutinas_lista_ejercicios (rutina_nombre, lista_ejercicios_id) VALUES ('Espalda: Densidad', 1);
INSERT INTO rutinas_lista_ejercicios (rutina_nombre, lista_ejercicios_id) VALUES ('Espalda: Densidad', 2);
INSERT INTO rutinas_lista_ejercicios (rutina_nombre, lista_ejercicios_id) VALUES ('Pecho: Fuerza', 3);
INSERT INTO rutinas_lista_ejercicios (rutina_nombre, lista_ejercicios_id) VALUES ('Pierna: Explosivo', 4);