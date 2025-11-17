-- Nombre de la empresa con más estaciones de servicio terrestres.
SELECT e.nombre, COUNT(*) AS num_estaciones
FROM empresa e
JOIN estacion_servicio es ON es.id_empresa = e.id_empresa
WHERE es.clase = 'terrestre'
GROUP BY e.nombre
ORDER BY num_estaciones DESC
LIMIT 1;

-- Nombre de la empresa con más estaciones de servicio marítimas.
SELECT e.nombre, COUNT(*) AS num_estaciones
FROM empresa e
JOIN estacion_servicio es ON es.id_empresa = e.id_empresa
WHERE es.clase = 'marítimo'
GROUP BY e.nombre
ORDER BY num_estaciones DESC
LIMIT 1;

-- Localización, nombre de empresa y margen de la estación con el precio más bajo para el combustible «Gasolina 95 E5» en la Comunidad de Madrid.
SELECT 
    es.municipio,
    es.localidad,
    es.cp,
    es.direccion,
    e.nombre AS empresa,
    es.margen,
    p.precio
FROM estacion_servicio es
JOIN empresa e ON es.id_empresa = e.id_empresa
JOIN precio p ON es.id_estacion = p.id_estacion
JOIN carburante c ON p.id_carburante = c.id_carburante
WHERE es.provincia = 'MADRID'
  	AND c.nombre = 'gasolina 95 E5'
ORDER BY p.precio ASC
LIMIT 1;


-- Localización, nombre de empresa y margen de la estación con el precio más bajo para el combustible «Gasóleo A» si resido en el centro de Albacete y no quiero desplazarme más de 10 km.
SELECT 
    e.provincia,
    e.municipio,
    e.localidad,
    e.direccion,
    e.margen,
    p.precio
FROM estacion_servicio e
JOIN precio p ON p.id_estacion = e.id_estacion
JOIN carburante c ON c.id_carburante = p.id_carburante
WHERE c.nombre = 'gasóleo A'
  -- Rango aproximado de ±10 km alrededor del centro de Albacete
  -- Latitud y longitud del centro de Albacete: 38.994349, -1.858542
  -- 0.1 grados ≈ 11 km
  AND e.latitud BETWEEN 38.994349 - 0.1 AND 38.994349 + 0.1
  AND e.longitud BETWEEN -1.858542 - 0.1 AND -1.858542 + 0.1
ORDER BY p.precio
LIMIT 1;


-- Provincia en la que se encuentre la estación de servicio marítima con el combustible «Gasolina 95 E5» más caro.
SELECT 
    es.provincia,
    p.precio
FROM estacion_servicio es
JOIN precio p ON es.id_estacion = p.id_estacion
JOIN carburante c ON p.id_carburante = c.id_carburante
WHERE es.clase = 'marítimo'
  AND c.nombre = 'gasolina 95 E5'
ORDER BY p.precio DESC
LIMIT 1;