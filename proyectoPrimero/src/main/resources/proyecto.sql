DROP DATABASE IF EXISTS Proyecto;
CREATE DATABASE Proyecto;
USE Proyecto;


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
    Mejor_Puntuacion ENUM('MAXIMO', 'MINIMO')
);


CREATE TABLE Puntuacion (
    ID_Participante INT,
    ID_Prueba INT,
    Puntuacion DECIMAL(4,2) CHECK (Puntuacion BETWEEN 0 AND 10),
    PRIMARY KEY (ID_Participante, ID_Prueba),
    FOREIGN KEY (ID_Participante) REFERENCES Participante(ID_Participante),
    FOREIGN KEY (ID_Prueba) REFERENCES Prueba(ID_Prueba)
);



INSERT INTO Equipo VALUES (1, 'Algaba', 1);
INSERT INTO Equipo VALUES (2, 'Alcalá', 1);

INSERT INTO Participante VALUES (1, 'Ana', 'López', 22, 1);
INSERT INTO Participante VALUES (2, 'Luis', 'Díaz', 25, 1);
INSERT INTO Participante VALUES (3, 'Marta', 'Ríos', 28, 1);
INSERT INTO Participante VALUES (4, 'Carlos', 'Fernández', 24, 1);
INSERT INTO Participante VALUES (5, 'Lucía', 'Martínez', 23, 2);
INSERT INTO Participante VALUES (6, 'Jorge', 'Navas', 26, 2);

INSERT INTO Prueba VALUES (100, 'Salto', 'Medición de salto', '2025-06-01', 'MAXIMO');
INSERT INTO Prueba VALUES (200, '100m lisos', 'Tiempo en 100m', '2025-06-01', 'MAXIMO');

INSERT INTO Puntuacion VALUES 
(1, 100, 6.5),
(2, 100, 6.8),
(3, 100, 6.2),
(4, 100, 6.0),
(5, 200, 7.0),
(6, 200, 5.9);
