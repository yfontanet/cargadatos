-- Creación de tablas según nuestro Database ER
CREATE TABLE empresa (
    id_empresa SERIAL PRIMARY KEY,
    nombre VARCHAR(120) NOT NULL
);

CREATE TABLE estacion_servicio (
    id_estacion SERIAL PRIMARY KEY,
    id_empresa INT REFERENCES empresa(id_empresa),
    clase VARCHAR(30) NOT NULL,
    provincia VARCHAR(100),
    municipio VARCHAR(100),
    localidad VARCHAR(100),
    cp VARCHAR(15),
    direccion VARCHAR(200),
    margen VARCHAR(30),
    longitud DECIMAL(9,6),
    latitud DECIMAL(9,6),
    rem VARCHAR(20),
    tipo_servicio VARCHAR(150),
    horario VARCHAR(300),
    tipo_venta VARCHAR(50)
);

CREATE TABLE carburante (
    id_carburante SERIAL PRIMARY KEY,
    nombre VARCHAR(60) NOT NULL UNIQUE
);

CREATE TABLE precio (
    id_precio SERIAL PRIMARY KEY,
    id_estacion INT NOT NULL REFERENCES estacion_servicio(id_estacion),
    id_carburante INT NOT NULL REFERENCES carburante(id_carburante),
    precio DECIMAL(8,3),
    hora_datos TIMESTAMP
);
