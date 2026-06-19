-- ========================================================
-- BASE DE DATOS PARA EL MUNDIAL DE APUESTAS 2026
-- Generado automáticamente a partir de Mundial.java
-- ========================================================

CREATE DATABASE IF NOT EXISTS mundial_apuestas;
USE mundial_apuestas;

SET FOREIGN_KEY_CHECKS = 0;
DROP VIEW IF EXISTS ranking_apostadores;
DROP TABLE IF EXISTS apuestas;
DROP TABLE IF EXISTS historial_apuestas;
DROP TABLE IF EXISTS apostadores;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS partidos;
DROP TABLE IF EXISTS equipos;
DROP TABLE IF EXISTS grupos;
SET FOREIGN_KEY_CHECKS = 1;

-- ========================================================
-- 1. CREACIÓN DE TABLAS
-- ========================================================

-- Tabla de grupos
CREATE TABLE grupos (
    id CHAR(1) PRIMARY KEY
);

-- Tabla de equipos (Solo datos de registro del equipo)
CREATE TABLE equipos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    grupo_id CHAR(1) NOT NULL,
    FOREIGN KEY (grupo_id) REFERENCES grupos(id) ON DELETE CASCADE
);

-- Tabla de partidos programados y sus resultados reales
CREATE TABLE partidos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    grupo_id CHAR(1) NOT NULL,
    local_id INT NOT NULL,
    visitante_id INT NOT NULL,
    fecha VARCHAR(50) NOT NULL,
    goles_local INT DEFAULT NULL,
    goles_visitante INT DEFAULT NULL,
    registrado BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (grupo_id) REFERENCES grupos(id) ON DELETE CASCADE,
    FOREIGN KEY (local_id) REFERENCES equipos(id) ON DELETE CASCADE,
    FOREIGN KEY (visitante_id) REFERENCES equipos(id) ON DELETE CASCADE
);

-- Tabla de roles de usuario
CREATE TABLE roles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(30) UNIQUE NOT NULL
);

-- Insertar roles por defecto
INSERT INTO roles (id, nombre) VALUES (1, 'ADMINISTRADOR'), (2, 'USUARIO') 
ON DUPLICATE KEY UPDATE nombre=VALUES(nombre);

-- Tabla de apostadores: Registra a las personas (usuarios y administradores)
CREATE TABLE apostadores (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    cedula VARCHAR(20) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    rol_id INT NOT NULL DEFAULT 2,
    FOREIGN KEY (rol_id) REFERENCES roles(id) ON DELETE RESTRICT
);

-- Insertar administrador por defecto (la contraseña inicial es su cédula: '1093595196')
INSERT INTO apostadores (nombre, cedula, password, rol_id) VALUES ('Admin', '1093595196', SHA2('1093595196', 256), 1) 
ON DUPLICATE KEY UPDATE cedula=VALUES(cedula), password=VALUES(password), rol_id=VALUES(rol_id);


-- Tabla de apuestas realizadas por los apostadores (pronósticos)
CREATE TABLE apuestas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    apostador_id INT NOT NULL,
    partido_id INT NOT NULL,
    goles_local_apuesta INT NOT NULL,
    goles_visitante_apuesta INT NOT NULL,
    FOREIGN KEY (apostador_id) REFERENCES apostadores(id) ON DELETE CASCADE,
    FOREIGN KEY (partido_id) REFERENCES partidos(id) ON DELETE CASCADE,
    UNIQUE KEY apuesta_unica (apostador_id, partido_id)
);

-- Tabla de historial de pronósticos creados
CREATE TABLE historial_apuestas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    apuesta_id INT NOT NULL,
    apostador VARCHAR(100) NOT NULL,
    partido VARCHAR(150) NOT NULL,
    goles_local_apuesta INT NOT NULL,
    goles_visitante_apuesta INT NOT NULL,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    accion VARCHAR(20) NOT NULL
);

-- Trigger de inserción de apuestas para historial
DELIMITER //
CREATE TRIGGER trg_apuestas_insert
AFTER INSERT ON apuestas
FOR EACH ROW
BEGIN
    DECLARE v_apostador VARCHAR(100);
    DECLARE v_local VARCHAR(50);
    DECLARE v_visitante VARCHAR(50);
    
    SELECT nombre INTO v_apostador FROM apostadores WHERE id = NEW.apostador_id;
    
    SELECT e1.nombre, e2.nombre INTO v_local, v_visitante 
    FROM partidos p 
    JOIN equipos e1 ON p.local_id = e1.id 
    JOIN equipos e2 ON p.visitante_id = e2.id
    WHERE p.id = NEW.partido_id;
    
    INSERT INTO historial_apuestas (apuesta_id, apostador, partido, goles_local_apuesta, goles_visitante_apuesta, accion)
    VALUES (NEW.id, v_apostador, CONCAT(v_local, ' vs ', v_visitante), NEW.goles_local_apuesta, NEW.goles_visitante_apuesta, 'CREADA');
END//
DELIMITER ;


-- ========================================================
-- 2. VISTA SQL PARA CALCULAR PUNTOS Y RANKING EN TIEMPO REAL
-- ========================================================
-- Esta vista suma los puntos acumulados por cada apostador de forma dinámica.
-- No requiere guardar columnas estáticas ni usar disparadores.
CREATE VIEW ranking_apostadores AS
SELECT 
    a.id AS apostador_id,
    a.nombre AS apostador,
    COALESCE(SUM(
        CASE 
            -- 1. Acierto de marcador exacto: 3 puntos
            WHEN ap.goles_local_apuesta = p.goles_local 
             AND ap.goles_visitante_apuesta = p.goles_visitante 
                THEN 3
            
            -- 2. Acierto del ganador (no exacto): 2 puntos
            WHEN ((p.goles_local > p.goles_visitante AND ap.goles_local_apuesta > ap.goles_visitante_apuesta)
              OR (p.goles_visitante > p.goles_local AND ap.goles_visitante_apuesta > ap.goles_local_apuesta))
                THEN 2
            
            -- 3. Acierto del empate (no exacto): 1 punto
            WHEN (p.goles_local = p.goles_visitante AND ap.goles_local_apuesta = ap.goles_visitante_apuesta)
                THEN 1
            
            -- 4. No acertó o partido aún no jugado: 0 puntos
            ELSE 0 
        END
    ), 0) AS puntos_totales
FROM apostadores a
LEFT JOIN apuestas ap ON a.id = ap.apostador_id
LEFT JOIN partidos p ON ap.partido_id = p.id AND p.registrado = TRUE
GROUP BY a.id, a.nombre
ORDER BY puntos_totales DESC;

-- ========================================================
-- 3. DATOS INICIALES (MOCK DATA)
-- ========================================================

-- Inserción de grupos
INSERT INTO grupos (id) VALUES ('A');
INSERT INTO grupos (id) VALUES ('B');
INSERT INTO grupos (id) VALUES ('C');
INSERT INTO grupos (id) VALUES ('D');
INSERT INTO grupos (id) VALUES ('E');
INSERT INTO grupos (id) VALUES ('F');
INSERT INTO grupos (id) VALUES ('G');
INSERT INTO grupos (id) VALUES ('H');
INSERT INTO grupos (id) VALUES ('I');
INSERT INTO grupos (id) VALUES ('J');
INSERT INTO grupos (id) VALUES ('K');
INSERT INTO grupos (id) VALUES ('L');

-- Inserción de equipos
INSERT INTO equipos (id, nombre, grupo_id) VALUES (1, 'México', 'A');
INSERT INTO equipos (id, nombre, grupo_id) VALUES (2, 'Sudáfrica', 'A');
INSERT INTO equipos (id, nombre, grupo_id) VALUES (3, 'Corea del Sur', 'A');
INSERT INTO equipos (id, nombre, grupo_id) VALUES (4, 'Chequia', 'A');
INSERT INTO equipos (id, nombre, grupo_id) VALUES (5, 'Canadá', 'B');
INSERT INTO equipos (id, nombre, grupo_id) VALUES (6, 'Bosnia y Herzegovina', 'B');
INSERT INTO equipos (id, nombre, grupo_id) VALUES (7, 'Qatar', 'B');
INSERT INTO equipos (id, nombre, grupo_id) VALUES (8, 'Suiza', 'B');
INSERT INTO equipos (id, nombre, grupo_id) VALUES (9, 'Brasil', 'C');
INSERT INTO equipos (id, nombre, grupo_id) VALUES (10, 'Marruecos', 'C');
INSERT INTO equipos (id, nombre, grupo_id) VALUES (11, 'Haití', 'C');
INSERT INTO equipos (id, nombre, grupo_id) VALUES (12, 'Escocia', 'C');
INSERT INTO equipos (id, nombre, grupo_id) VALUES (13, 'EE.UU.', 'D');
INSERT INTO equipos (id, nombre, grupo_id) VALUES (14, 'Paraguay', 'D');
INSERT INTO equipos (id, nombre, grupo_id) VALUES (15, 'Australia', 'D');
INSERT INTO equipos (id, nombre, grupo_id) VALUES (16, 'Turquía', 'D');
INSERT INTO equipos (id, nombre, grupo_id) VALUES (17, 'Alemania', 'E');
INSERT INTO equipos (id, nombre, grupo_id) VALUES (18, 'Curazao', 'E');
INSERT INTO equipos (id, nombre, grupo_id) VALUES (19, 'Costa de Marfil', 'E');
INSERT INTO equipos (id, nombre, grupo_id) VALUES (20, 'Ecuador', 'E');
INSERT INTO equipos (id, nombre, grupo_id) VALUES (21, 'Países Bajos', 'F');
INSERT INTO equipos (id, nombre, grupo_id) VALUES (22, 'Japón', 'F');
INSERT INTO equipos (id, nombre, grupo_id) VALUES (23, 'Suecia', 'F');
INSERT INTO equipos (id, nombre, grupo_id) VALUES (24, 'Túnez', 'F');
INSERT INTO equipos (id, nombre, grupo_id) VALUES (25, 'Bélgica', 'G');
INSERT INTO equipos (id, nombre, grupo_id) VALUES (26, 'Egipto', 'G');
INSERT INTO equipos (id, nombre, grupo_id) VALUES (27, 'Irán', 'G');
INSERT INTO equipos (id, nombre, grupo_id) VALUES (28, 'Nueva Zelanda', 'G');
INSERT INTO equipos (id, nombre, grupo_id) VALUES (29, 'España', 'H');
INSERT INTO equipos (id, nombre, grupo_id) VALUES (30, 'Cabo Verde', 'H');
INSERT INTO equipos (id, nombre, grupo_id) VALUES (31, 'Arabia Saudita', 'H');
INSERT INTO equipos (id, nombre, grupo_id) VALUES (32, 'Uruguay', 'H');
INSERT INTO equipos (id, nombre, grupo_id) VALUES (33, 'Francia', 'I');
INSERT INTO equipos (id, nombre, grupo_id) VALUES (34, 'Senegal', 'I');
INSERT INTO equipos (id, nombre, grupo_id) VALUES (35, 'Irak', 'I');
INSERT INTO equipos (id, nombre, grupo_id) VALUES (36, 'Noruega', 'I');
INSERT INTO equipos (id, nombre, grupo_id) VALUES (37, 'Argentina', 'J');
INSERT INTO equipos (id, nombre, grupo_id) VALUES (38, 'Argelia', 'J');
INSERT INTO equipos (id, nombre, grupo_id) VALUES (39, 'Austria', 'J');
INSERT INTO equipos (id, nombre, grupo_id) VALUES (40, 'Jordania', 'J');
INSERT INTO equipos (id, nombre, grupo_id) VALUES (41, 'Portugal', 'K');
INSERT INTO equipos (id, nombre, grupo_id) VALUES (42, 'R.D. Congo', 'K');
INSERT INTO equipos (id, nombre, grupo_id) VALUES (43, 'Uzbekistán', 'K');
INSERT INTO equipos (id, nombre, grupo_id) VALUES (44, 'Colombia', 'K');
INSERT INTO equipos (id, nombre, grupo_id) VALUES (45, 'Inglaterra', 'L');
INSERT INTO equipos (id, nombre, grupo_id) VALUES (46, 'Croacia', 'L');
INSERT INTO equipos (id, nombre, grupo_id) VALUES (47, 'Ghana', 'L');
INSERT INTO equipos (id, nombre, grupo_id) VALUES (48, 'Panamá', 'L');

-- Inserción de partidos programados
INSERT INTO partidos (id, grupo_id, local_id, visitante_id, fecha) VALUES (1, 'A', 1, 2, '11 de Junio');
INSERT INTO partidos (id, grupo_id, local_id, visitante_id, fecha) VALUES (2, 'A', 1, 3, '12 de Junio');
INSERT INTO partidos (id, grupo_id, local_id, visitante_id, fecha) VALUES (3, 'A', 1, 4, '16 de Junio');
INSERT INTO partidos (id, grupo_id, local_id, visitante_id, fecha) VALUES (4, 'A', 2, 3, '17 de Junio');
INSERT INTO partidos (id, grupo_id, local_id, visitante_id, fecha) VALUES (5, 'A', 2, 4, '21 de Junio');
INSERT INTO partidos (id, grupo_id, local_id, visitante_id, fecha) VALUES (6, 'A', 3, 4, '21 de Junio');
INSERT INTO partidos (id, grupo_id, local_id, visitante_id, fecha) VALUES (7, 'B', 5, 6, '11 de Junio');
INSERT INTO partidos (id, grupo_id, local_id, visitante_id, fecha) VALUES (8, 'B', 5, 7, '12 de Junio');
INSERT INTO partidos (id, grupo_id, local_id, visitante_id, fecha) VALUES (9, 'B', 5, 8, '16 de Junio');
INSERT INTO partidos (id, grupo_id, local_id, visitante_id, fecha) VALUES (10, 'B', 6, 7, '17 de Junio');
INSERT INTO partidos (id, grupo_id, local_id, visitante_id, fecha) VALUES (11, 'B', 6, 8, '21 de Junio');
INSERT INTO partidos (id, grupo_id, local_id, visitante_id, fecha) VALUES (12, 'B', 7, 8, '21 de Junio');
INSERT INTO partidos (id, grupo_id, local_id, visitante_id, fecha) VALUES (13, 'C', 9, 10, '12 de Junio');
INSERT INTO partidos (id, grupo_id, local_id, visitante_id, fecha) VALUES (14, 'C', 9, 11, '13 de Junio');
INSERT INTO partidos (id, grupo_id, local_id, visitante_id, fecha) VALUES (15, 'C', 9, 12, '17 de Junio');
INSERT INTO partidos (id, grupo_id, local_id, visitante_id, fecha) VALUES (16, 'C', 10, 11, '18 de Junio');
INSERT INTO partidos (id, grupo_id, local_id, visitante_id, fecha) VALUES (17, 'C', 10, 12, '22 de Junio');
INSERT INTO partidos (id, grupo_id, local_id, visitante_id, fecha) VALUES (18, 'C', 11, 12, '22 de Junio');
INSERT INTO partidos (id, grupo_id, local_id, visitante_id, fecha) VALUES (19, 'D', 13, 14, '12 de Junio');
INSERT INTO partidos (id, grupo_id, local_id, visitante_id, fecha) VALUES (20, 'D', 13, 15, '13 de Junio');
INSERT INTO partidos (id, grupo_id, local_id, visitante_id, fecha) VALUES (21, 'D', 13, 16, '17 de Junio');
INSERT INTO partidos (id, grupo_id, local_id, visitante_id, fecha) VALUES (22, 'D', 14, 15, '18 de Junio');
INSERT INTO partidos (id, grupo_id, local_id, visitante_id, fecha) VALUES (23, 'D', 14, 16, '22 de Junio');
INSERT INTO partidos (id, grupo_id, local_id, visitante_id, fecha) VALUES (24, 'D', 15, 16, '22 de Junio');
INSERT INTO partidos (id, grupo_id, local_id, visitante_id, fecha) VALUES (25, 'E', 17, 18, '13 de Junio');
INSERT INTO partidos (id, grupo_id, local_id, visitante_id, fecha) VALUES (26, 'E', 17, 19, '14 de Junio');
INSERT INTO partidos (id, grupo_id, local_id, visitante_id, fecha) VALUES (27, 'E', 17, 20, '18 de Junio');
INSERT INTO partidos (id, grupo_id, local_id, visitante_id, fecha) VALUES (28, 'E', 18, 19, '19 de Junio');
INSERT INTO partidos (id, grupo_id, local_id, visitante_id, fecha) VALUES (29, 'E', 18, 20, '23 de Junio');
INSERT INTO partidos (id, grupo_id, local_id, visitante_id, fecha) VALUES (30, 'E', 19, 20, '23 de Junio');
INSERT INTO partidos (id, grupo_id, local_id, visitante_id, fecha) VALUES (31, 'F', 21, 22, '13 de Junio');
INSERT INTO partidos (id, grupo_id, local_id, visitante_id, fecha) VALUES (32, 'F', 21, 23, '14 de Junio');
INSERT INTO partidos (id, grupo_id, local_id, visitante_id, fecha) VALUES (33, 'F', 21, 24, '18 de Junio');
INSERT INTO partidos (id, grupo_id, local_id, visitante_id, fecha) VALUES (34, 'F', 22, 23, '19 de Junio');
INSERT INTO partidos (id, grupo_id, local_id, visitante_id, fecha) VALUES (35, 'F', 22, 24, '23 de Junio');
INSERT INTO partidos (id, grupo_id, local_id, visitante_id, fecha) VALUES (36, 'F', 23, 24, '23 de Junio');
INSERT INTO partidos (id, grupo_id, local_id, visitante_id, fecha) VALUES (37, 'G', 25, 26, '14 de Junio');
INSERT INTO partidos (id, grupo_id, local_id, visitante_id, fecha) VALUES (38, 'G', 25, 27, '15 de Junio');
INSERT INTO partidos (id, grupo_id, local_id, visitante_id, fecha) VALUES (39, 'G', 25, 28, '19 de Junio');
INSERT INTO partidos (id, grupo_id, local_id, visitante_id, fecha) VALUES (40, 'G', 26, 27, '20 de Junio');
INSERT INTO partidos (id, grupo_id, local_id, visitante_id, fecha) VALUES (41, 'G', 26, 28, '24 de Junio');
INSERT INTO partidos (id, grupo_id, local_id, visitante_id, fecha) VALUES (42, 'G', 27, 28, '24 de Junio');
INSERT INTO partidos (id, grupo_id, local_id, visitante_id, fecha) VALUES (43, 'H', 29, 30, '14 de Junio');
INSERT INTO partidos (id, grupo_id, local_id, visitante_id, fecha) VALUES (44, 'H', 29, 31, '15 de Junio');
INSERT INTO partidos (id, grupo_id, local_id, visitante_id, fecha) VALUES (45, 'H', 29, 32, '19 de Junio');
INSERT INTO partidos (id, grupo_id, local_id, visitante_id, fecha) VALUES (46, 'H', 30, 31, '20 de Junio');
INSERT INTO partidos (id, grupo_id, local_id, visitante_id, fecha) VALUES (47, 'H', 30, 32, '24 de Junio');
INSERT INTO partidos (id, grupo_id, local_id, visitante_id, fecha) VALUES (48, 'H', 31, 32, '24 de Junio');
INSERT INTO partidos (id, grupo_id, local_id, visitante_id, fecha) VALUES (49, 'I', 33, 34, '15 de Junio');
INSERT INTO partidos (id, grupo_id, local_id, visitante_id, fecha) VALUES (50, 'I', 33, 35, '16 de Junio');
INSERT INTO partidos (id, grupo_id, local_id, visitante_id, fecha) VALUES (51, 'I', 33, 36, '20 de Junio');
INSERT INTO partidos (id, grupo_id, local_id, visitante_id, fecha) VALUES (52, 'I', 34, 35, '21 de Junio');
INSERT INTO partidos (id, grupo_id, local_id, visitante_id, fecha) VALUES (53, 'I', 34, 36, '25 de Junio');
INSERT INTO partidos (id, grupo_id, local_id, visitante_id, fecha) VALUES (54, 'I', 35, 36, '25 de Junio');
INSERT INTO partidos (id, grupo_id, local_id, visitante_id, fecha) VALUES (55, 'J', 37, 38, '15 de Junio');
INSERT INTO partidos (id, grupo_id, local_id, visitante_id, fecha) VALUES (56, 'J', 37, 39, '16 de Junio');
INSERT INTO partidos (id, grupo_id, local_id, visitante_id, fecha) VALUES (57, 'J', 37, 40, '20 de Junio');
INSERT INTO partidos (id, grupo_id, local_id, visitante_id, fecha) VALUES (58, 'J', 38, 39, '21 de Junio');
INSERT INTO partidos (id, grupo_id, local_id, visitante_id, fecha) VALUES (59, 'J', 38, 40, '25 de Junio');
INSERT INTO partidos (id, grupo_id, local_id, visitante_id, fecha) VALUES (60, 'J', 39, 40, '25 de Junio');
INSERT INTO partidos (id, grupo_id, local_id, visitante_id, fecha) VALUES (61, 'K', 41, 42, '16 de Junio');
INSERT INTO partidos (id, grupo_id, local_id, visitante_id, fecha) VALUES (62, 'K', 41, 43, '17 de Junio');
INSERT INTO partidos (id, grupo_id, local_id, visitante_id, fecha) VALUES (63, 'K', 41, 44, '21 de Junio');
INSERT INTO partidos (id, grupo_id, local_id, visitante_id, fecha) VALUES (64, 'K', 42, 43, '22 de Junio');
INSERT INTO partidos (id, grupo_id, local_id, visitante_id, fecha) VALUES (65, 'K', 42, 44, '26 de Junio');
INSERT INTO partidos (id, grupo_id, local_id, visitante_id, fecha) VALUES (66, 'K', 43, 44, '26 de Junio');
INSERT INTO partidos (id, grupo_id, local_id, visitante_id, fecha) VALUES (67, 'L', 45, 46, '16 de Junio');
INSERT INTO partidos (id, grupo_id, local_id, visitante_id, fecha) VALUES (68, 'L', 45, 47, '17 de Junio');
INSERT INTO partidos (id, grupo_id, local_id, visitante_id, fecha) VALUES (69, 'L', 45, 48, '21 de Junio');
INSERT INTO partidos (id, grupo_id, local_id, visitante_id, fecha) VALUES (70, 'L', 46, 47, '22 de Junio');
INSERT INTO partidos (id, grupo_id, local_id, visitante_id, fecha) VALUES (71, 'L', 46, 48, '26 de Junio');
INSERT INTO partidos (id, grupo_id, local_id, visitante_id, fecha) VALUES (72, 'L', 47, 48, '26 de Junio');
