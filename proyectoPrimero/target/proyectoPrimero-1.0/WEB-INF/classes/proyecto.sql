-- 1. Crear base de datos y usarla
DROP DATABASE IF EXISTS Proyecto;
CREATE DATABASE Proyecto;
USE Proyecto;

-- 2. Crear tablas principales

CREATE TABLE Listado (
    ID_Listado INT PRIMARY KEY,
    Descripcion VARCHAR(255),
    Fecha_Creacion DATE
);

CREATE TABLE Equipo (
    ID_Equipo INT PRIMARY KEY,
    Nombre_Equipo VARCHAR(100) UNIQUE,
    ID_Listado INT,
    FOREIGN KEY (ID_Listado) REFERENCES Listado(ID_Listado)
);

CREATE TABLE Participante (
    ID_Participante INT PRIMARY KEY,
    Nombre VARCHAR(50),
    Apellido VARCHAR(50),
    Edad INT CHECK (Edad >= 0),
    ID_Equipo INT,
    FOREIGN KEY (ID_Equipo) REFERENCES Equipo(ID_Equipo)
);

CREATE TABLE Prueba (
    ID_Prueba INT PRIMARY KEY,
    Nombre_Prueba VARCHAR(100),
    Descripcion TEXT,
    Fecha DATE,
    Duracion TIME,
    Mejor_Puntuacion ENUM('MAXIMO', 'MINIMO')
);

-- 3. Tabla de participación
CREATE TABLE Participante_Prueba (
    ID_Participante INT,
    ID_Prueba INT,
    Puntuacion DECIMAL(4,2) CHECK (Puntuacion BETWEEN 0 AND 10),
    PRIMARY KEY (ID_Participante, ID_Prueba),
    FOREIGN KEY (ID_Participante) REFERENCES Participante(ID_Participante),
    FOREIGN KEY (ID_Prueba) REFERENCES Prueba(ID_Prueba)
);

-- 4. Tabla de puntos obtenidos por prueba (3-2-1 para top 3)
CREATE TABLE Puntos_Participante (
    ID_Participante INT,
    ID_Prueba INT,
    Puntos INT CHECK (Puntos BETWEEN 0 AND 3),
    PRIMARY KEY (ID_Participante, ID_Prueba),
    FOREIGN KEY (ID_Participante, ID_Prueba) REFERENCES Participante_Prueba(ID_Participante, ID_Prueba)
);

-- 5. Insertar datos de prueba mínimos (precondiciones)

INSERT INTO Listado VALUES (1, 'Torneo Instituto', '2025-01-01');

INSERT INTO Equipo VALUES (1, 'Algaba', 1);
INSERT INTO Equipo VALUES (2, 'Alcalá', 1);

INSERT INTO Participante VALUES (1, 'Ana', 'López', 22, 1);
INSERT INTO Participante VALUES (2, 'Luis', 'Díaz', 25, 1);
INSERT INTO Participante VALUES (3, 'Marta', 'Ríos', 28, 1);
INSERT INTO Participante VALUES (4, 'Carlos', 'Fernández', 24, 1);
INSERT INTO Participante VALUES (5, 'Lucía', 'Martínez', 23, 2);
INSERT INTO Participante VALUES (6, 'Jorge', 'Navas', 26, 2);

INSERT INTO Prueba VALUES (100, 'Salto', 'Medición de salto', '2025-06-01', '00:20:00', 'MAXIMO');

INSERT INTO Participante_Prueba VALUES 
(1, 100, 6.5),
(2, 100, 6.8),
(3, 100, 6.2),
(4, 100, 6.0),
(5, 100, 7.0),
(6, 100, 5.9);

-- 6. Asignar puntos (3-2-1) a top 3 participantes en una prueba

-- Se eliminarán puntos previos para evitar duplicados
DELETE FROM Puntos_Participante WHERE ID_Prueba = 100;

INSERT INTO Puntos_Participante (ID_Participante, ID_Prueba, Puntos)
SELECT 
    ID_Participante,
    ID_Prueba,
    CASE 
        WHEN rnk = 1 THEN 3
        WHEN rnk = 2 THEN 2
        WHEN rnk = 3 THEN 1
        ELSE 0
    END
FROM (
    SELECT 
        ID_Participante,
        ID_Prueba,
        Puntuacion,
        DENSE_RANK() OVER (ORDER BY Puntuacion DESC) AS rnk
    FROM Participante_Prueba
    WHERE ID_Prueba = 100
) AS ranked
WHERE rnk <= 3;

-- 7. Consulta detallada de puntuaciones por participante en una prueba

SELECT 
    p.ID_Participante,
    CONCAT(p.Nombre, ' ', p.Apellido) AS Participante,
    pr.Nombre_Prueba,
    pp.Puntuacion,
    IFNULL(pt.Puntos, 0) AS Puntos_Obtenidos
FROM Participante_Prueba pp
JOIN Participante p ON pp.ID_Participante = p.ID_Participante
JOIN Prueba pr ON pp.ID_Prueba = pr.ID_Prueba
LEFT JOIN Puntos_Participante pt ON pp.ID_Participante = pt.ID_Participante AND pp.ID_Prueba = pt.ID_Prueba
WHERE pr.ID_Prueba = 100
ORDER BY pp.Puntuacion DESC;

-- 8. Consulta de puntuación global por equipo

SELECT 
    e.Nombre_Equipo,
    ROUND(AVG(pp.Puntuacion), 2) AS Promedio_Puntuacion,
    SUM(IFNULL(pt.Puntos, 0)) AS Total_Puntos_Obtenidos
FROM Equipo e
JOIN Participante p ON e.ID_Equipo = p.ID_Equipo
JOIN Participante_Prueba pp ON p.ID_Participante = pp.ID_Participante
LEFT JOIN Puntos_Participante pt ON p.ID_Participante = pt.ID_Participante AND pp.ID_Prueba = pt.ID_Prueba
GROUP BY e.ID_Equipo;

-- 9. Búsqueda de participantes o equipos por puntuación específica

-- Por puntuación exacta
SELECT 
    CONCAT(p.Nombre, ' ', p.Apellido) AS Participante,
    pr.Nombre_Prueba,
    pp.Puntuacion
FROM Participante p
JOIN Participante_Prueba pp ON p.ID_Participante = pp.ID_Participante
JOIN Prueba pr ON pr.ID_Prueba = pp.ID_Prueba
WHERE pp.Puntuacion = 7.5;

-- 10. Modificación de puntuaciones de un participante en una prueba

UPDATE Participante_Prueba
SET Puntuacion = 7.5
WHERE ID_Participante = 1 AND ID_Prueba = 100;

-- Recalcular puntos luego de modificar
-- (repetir paso 6 si lo automatizas mediante procedimiento almacenado)
