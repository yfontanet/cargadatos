package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EmpresaDAO {
    // Buscar por nombre, insertar si no existe
    public static int findOrCreate(Connection conn, String nombre) throws SQLException {
        if (nombre == null || nombre.trim().isEmpty()) return 0; // 0 indica sin empresa
        nombre = nombre.trim();

        // buscar
        try (PreparedStatement ps = conn.prepareStatement("SELECT id_empresa FROM empresa WHERE nombre = ?")) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }

        // insertar y devolver id (usando RETURNING)
        String insertSql = "INSERT INTO empresa(nombre) VALUES (?) RETURNING id_empresa";
        try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        throw new SQLException("No se pudo insertar empresa " + nombre);
    }
}