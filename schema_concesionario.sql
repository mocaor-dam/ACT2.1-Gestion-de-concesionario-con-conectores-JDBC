CREATE DATABASE IF NOT EXISTS concesionario;
USE concesionario;

CREATE TABLE IF NOT EXISTS propietarios (
                                            id_propietario INT AUTO_INCREMENT PRIMARY KEY,
                                            dni VARCHAR(10) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    apellidos VARCHAR(150) NOT NULL,
    telefono VARCHAR(15)
    );

CREATE TABLE IF NOT EXISTS coches (
                                      matricula VARCHAR(10) PRIMARY KEY,
    marca VARCHAR(50) NOT NULL,
    modelo VARCHAR(50) NOT NULL,
    extras VARCHAR(255),
    precio DECIMAL(10,2) NOT NULL,
    id_propietario INT,
    FOREIGN KEY (id_propietario) REFERENCES propietarios(id_propietario)
    );

CREATE TABLE IF NOT EXISTS traspasos (
                                         id_traspaso INT AUTO_INCREMENT PRIMARY KEY,
                                         matricula_coche VARCHAR(10) NOT NULL,
    id_vendedor INT,
    id_comprador INT NOT NULL,
    monto_economico DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (matricula_coche) REFERENCES coches(matricula),
    FOREIGN KEY (id_vendedor) REFERENCES propietarios(id_propietario),
    FOREIGN KEY (id_comprador) REFERENCES propietarios(id_propietario)
    );

DELIMITER //
DROP PROCEDURE IF EXISTS sp_coches_por_marca //
CREATE PROCEDURE sp_coches_por_marca(IN p_marca VARCHAR(50))
BEGIN
SELECT * FROM coches WHERE marca = p_marca;
END //
DELIMITER ;