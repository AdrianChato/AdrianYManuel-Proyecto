CREATE DATABASE IF NOT EXISTS miBaseDatos;
USE miBaseDatos;

CREATE TABLE IF NOT EXISTS usuarios (
    usuario VARCHAR(50) PRIMARY KEY,
    clave VARCHAR(10) NOT NULL, 
    direccion  VARCHAR(100)
);

-- Usuario de prueba
INSERT INTO usuarios (usuario, clave, direccion) VALUES ('pepep', 'pepe1234', 'Calle Proyecto')
ON DUPLICATE KEY UPDATE clave = 'pepe1234';

INSERT INTO usuarios (usuario, clave, direccion) VALUES ('pepa', 'pepe1234', 'Calle Proyecto 2')
ON DUPLICATE KEY UPDATE clave = 'pepe1234';
