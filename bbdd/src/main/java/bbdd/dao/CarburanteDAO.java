package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CarburanteDAO {
    // Buscar por nombre, insertar si no existe
    public static int findOrCreate(Connection conn, String nombre) throws SQLException {
        if (nombre == null || nombre.trim().isEmpty()) return 0;
        nombre = nombre.trim();
        // buscar
        try (PreparedStatement ps = conn.prepareStatement("SELECT id_carburante FROM carburante WHERE nombre = ?")) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        // insertar y devolver id
        String insertSql = "INSERT INTO carburante(nombre) VALUES (?) RETURNING id_carburante";
        try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        throw new SQLException("No se pudo insertar carburante " + nombre);
    }
}