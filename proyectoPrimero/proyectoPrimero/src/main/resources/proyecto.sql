DROP DATABASE IF EXISTS Proyecto;
CREATE DATABASE Proyecto;
USE Proyecto;


CREATE TABLE Equipo (
    ID_Equipo INT PRIMARY KEY AUTO_INCREMENT,
    Nombre_Equipo VARCHAR(100) UNIQUE
);

CREATE TABLE Participante (
    ID_Participante INT PRIMARY KEY AUTO_INCREMENT,
    Nombre VARCHAR(50),
    Apellido VARCHAR(50),
    Edad INT CHECK (Edad >= 0),
    ID_Equipo INT,
    FOREIGN KEY (ID_Equipo) REFERENCES Equipo(ID_Equipo)
);

CREATE TABLE Prueba (
    ID_Prueba INT PRIMARY KEY AUTO_INCREMENT,
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


INSERT INTO Equipo (Nombre_Equipo) VALUES ('Algaba'), ('Alcala');

INSERT INTO Participante (Nombre, Apellido, Edad, ID_Equipo) VALUES
('Ana', 'López', 22, 1),
('Luis', 'Díaz', 25, 1),
('Marta', 'Ríos', 28, 1),
('Carlos', 'Fernández', 24, 1),
('Lucía', 'Martínez', 23, 2),
('Jorge', 'Navas', 26, 2);

INSERT INTO Prueba (Nombre_Prueba, Descripcion, Fecha, Mejor_Puntuacion) VALUES
('Salto', 'Medición de salto', '2025-06-01', 'MAXIMO'),
('100m', 'Tiempo en 100m', '2025-06-01', 'MINIMO');

INSERT INTO Puntuacion VALUES 
(1, 1, 6.5),
(2, 1, 6.8),
(3, 1, 6.2),
(4, 1, 6.0),
(5, 2, 7.0),
(6, 2, 5.9);
