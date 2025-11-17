package loader;

import dao.CarburanteDAO;
import dao.EmpresaDAO;
import dao.EstacionDAO;
import dao.PrecioDAO;
import dao.PrecioDAO.PrecioRecord;
import db.DatabaseConnection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

// Clase para cargar datos desde archivos CSV (resul_mar.csv y resul_t.csv)
public class CSVLoader {

    // Formato para la columna "Toma de datos" en archivos terrestres
    private static final DateTimeFormatter TERRESTRIAL_DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Procesa un archivo CSV (resul_mar.csv o resul_t.csv)
     *
     * @param filePath ruta relativa al proyecto (ej: "datos/resul_t.csv")
     * @param clase    "marítimo" o "terrestre"
     */
    public void processFile(String filePath, String clase) {
        System.out.println("Procesando " + filePath + " como clase=" + clase);
        
        File f = new File(filePath);
        if (!f.exists()) {
            System.err.println("No existe: " + filePath);
            return;
        }
        // leer el archivo línea a línea
        try (Connection conn = DatabaseConnection.getConnection();
             BufferedReader br = new BufferedReader(new FileReader(f))) {

            conn.setAutoCommit(false);

            String headerLine = br.readLine();
            if (headerLine == null) {
                System.err.println("Fichero vacío: " + filePath);
                return;
            }

            // Normalizar cabeceras y detectar índices
            String[] headers = splitCsvLine(headerLine);
            for (int i = 0; i < headers.length; i++) headers[i] = headers[i].trim();

            Integer idxProvincia = findIndex(headers, "Provincia");
            Integer idxMunicipio = findIndex(headers, "Municipio");
            Integer idxLocalidad = findIndex(headers, "Localidad");
            Integer idxCp = findIndex(headers, "Código postal", "Codigo postal", "CP");
            Integer idxDireccion = findIndex(headers, "Dirección", "Direccion");
            Integer idxMargen = findIndex(headers, "Margen");
            Integer idxLong = findIndex(headers, "Longitud");
            Integer idxLat = findIndex(headers, "Latitud");
            Integer idxTomaDatos = findIndex(headers, "Toma de datos", "Toma de datos");
            Integer idxRotulo = findIndex(headers, "Rótulo", "Rotulo");
            Integer idxTipoVenta = findIndex(headers, "Tipo venta", "Tipo Venta");
            Integer idxRem = findIndex(headers, "Rem.", "Rem");
            Integer idxHorario = findIndex(headers, "Horario");
            Integer idxTipoServicio = findIndex(headers, "Tipo servicio"); // puede ser null en marítimo

            // detectar columnas de carburante (cabeceras que empiezan por "Precio " o por "%")
            List<Integer> fuelCols = new ArrayList<>();
            List<String> fuelNames = new ArrayList<>();
            for (int i = 0; i < headers.length; i++) {
                String h = headers[i];
                if (h == null || h.isEmpty()) continue;
                if (h.startsWith("Precio ")) {
                    fuelCols.add(i);
                    fuelNames.add(h.substring("Precio ".length()).trim());
                } else if (h.startsWith("%")) {
                    fuelCols.add(i);
                    fuelNames.add(h.trim()); // conservar el '%' y el nombre tal cual
                }
            }
            // leer líneas de datos
            List<PrecioRecord> priceBatch = new ArrayList<>();
            String line;
            int rowNum = 0;

            while ((line = br.readLine()) != null) {
                rowNum++;
                if (line.trim().isEmpty()) continue;

                String[] cols = splitCsvLine(line);

                // leer campos principales (trim en get)
                String provincia = get(cols, idxProvincia);
                String municipio = get(cols, idxMunicipio);
                String localidad = get(cols, idxLocalidad);
                String cp = get(cols, idxCp);
                String direccion = get(cols, idxDireccion);
                String margen = get(cols, idxMargen); // null para marítimo si no existe
                String longitudS = get(cols, idxLong);
                String latitudS = get(cols, idxLat);
                String tomaDatos = get(cols, idxTomaDatos); // solo para terrestre
                String rotulo = get(cols, idxRotulo);
                String tipoVenta = get(cols, idxTipoVenta);
                String rem = get(cols, idxRem);
                String horario = get(cols, idxHorario);
                String tipoServicio = get(cols, idxTipoServicio); // guardar tal cual (puede ser null)

                Double longitud = parseDouble(longitudS);
                Double latitud = parseDouble(latitudS);

                // empresa (findOrCreate)
                int idEmpresa = 0;
                try {
                    idEmpresa = EmpresaDAO.findOrCreate(conn, rotulo);
                } catch (Exception ex) {
                    conn.rollback();
                    throw ex;
                }

                // buscar estación existente
                Integer idEstacion = EstacionDAO.findByUnique(conn, idEmpresa == 0 ? null : idEmpresa, direccion, cp, longitud, latitud);

                if (idEstacion == null) {
                    // insertar estación
                    idEstacion = EstacionDAO.insert(
                            conn,
                            idEmpresa == 0 ? null : idEmpresa,
                            clase,
                            provincia,
                            municipio,
                            localidad,
                            cp,
                            direccion,
                            margen,         // margen: en marítimo puede ser null
                            longitud,
                            latitud,
                            rem,
                            tipoServicio,   // ahora guardamos TODO tal cual
                            horario,
                            tipoVenta
                    );
                }

                // hora_datos para precios
                LocalDateTime horaDatos;
                if ("terrestre".equalsIgnoreCase(clase)) {
                    if (tomaDatos != null && !tomaDatos.isBlank()) {
                        try {
                            horaDatos = LocalDateTime.parse(tomaDatos.trim(), TERRESTRIAL_DATE_FORMAT);
                        } catch (Exception ex) {
                            horaDatos = LocalDateTime.now();
                        }
                    } else {
                        horaDatos = LocalDateTime.now();
                    }
                } else {
                    horaDatos = LocalDateTime.now();
                }

                // recorrer carburantes detectados en cabecera
                for (int k = 0; k < fuelCols.size(); k++) {
                    int colIndex = fuelCols.get(k);
                    String fuelName = fuelNames.get(k);
                    String rawVal = get(cols, colIndex);

                    if (rawVal == null) continue;
                    if (rawVal.trim().isEmpty()) continue;
                    if ("NULL".equalsIgnoreCase(rawVal.trim())) continue;

                    BigDecimal precio = parseBigDecimal(rawVal);
                    if (precio == null) continue;

                    int idCarb = CarburanteDAO.findOrCreate(conn, fuelName);
                    priceBatch.add(new PrecioRecord(idEstacion, idCarb, precio, horaDatos));
                }

                if (priceBatch.size() >= 1000) {
                    PrecioDAO.batchInsert(conn, priceBatch);
                    priceBatch.clear();
                }
            } // end while

            // insertar resto del batch
            if (!priceBatch.isEmpty()) {
                PrecioDAO.batchInsert(conn, priceBatch);
                priceBatch.clear();
            }

            conn.commit();
            System.out.println("Procesado OK: " + filePath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* --------------------- helpers ---------------------- */

    // separa por ';' manteniendo campos vacíos
    private static String[] splitCsvLine(String line) {
        return line.split(";", -1);
    }

    // recuperar valor con trim y null si está vacío
    private static String get(String[] cols, Integer idx) {
        if (idx == null) return null;
        if (idx < 0 || idx >= cols.length) return null;
        String s = cols[idx];
        if (s == null) return null;
        s = s.trim();
        return s.isEmpty() ? null : s;
    }

    // busca índice de cabecera por nombres alternativos (case-insensitive)
    private static Integer findIndex(String[] headers, String... names) {
        for (String candidate : names) {
            for (int i = 0; i < headers.length; i++) {
                if (headers[i].equalsIgnoreCase(candidate)) return i;
            }
        }
        return null;
    }

    private static Double parseDouble(String s) {
        if (s == null) return null;
        try {
            return Double.parseDouble(s.replace(",", ".").trim());
        } catch (Exception e) {
            return null;
        }
    }

    private static BigDecimal parseBigDecimal(String s) {
        if (s == null) return null;
        // manejar "1.234,56" y "1,234" y "1.234"
        s = s.replace(".", "").replace(',', '.').trim();
        try {
            return new BigDecimal(s);
        } catch (Exception e) {
            return null;
        }
    }
}
