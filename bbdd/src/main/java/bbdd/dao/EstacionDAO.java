package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EstacionDAO {

    // Buscar por combinación de campos para evitar duplicados
    public static Integer findByUnique(Connection conn, Integer idEmpresa, String direccion, String cp, Double longitud, Double latitud) throws SQLException {
        String sql = "SELECT id_estacion FROM estacion_servicio WHERE direccion = ? AND cp = ? AND longitud = ? AND latitud = ? AND (id_empresa = ? OR ? = 0)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, direccion);
            ps.setString(2, cp);
            if (longitud == null) ps.setNull(3, java.sql.Types.NUMERIC); else ps.setDouble(3, longitud);
            if (latitud == null) ps.setNull(4, java.sql.Types.NUMERIC); else ps.setDouble(4, latitud);
            if (idEmpresa == null) { ps.setNull(5, java.sql.Types.INTEGER); ps.setInt(6, 0); }
            else { ps.setInt(5, idEmpresa); ps.setInt(6, idEmpresa); }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return null;
    }
    // Insertar nueva estación
    public static int insert(Connection conn,
                             Integer idEmpresa,
                             String clase,
                             String provincia,
                             String municipio,
                             String localidad,
                             String cp,
                             String direccion,
                             String margen,
                             Double longitud,
                             Double latitud,
                             String rem,
                             String tipoServicio,
                             String horario,
                             String tipoVenta) throws SQLException {
        String sql = "INSERT INTO estacion_servicio (id_empresa, clase, provincia, municipio, localidad, cp, direccion, margen, longitud, latitud, rem, tipo_servicio, horario, tipo_venta) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id_estacion";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            if (idEmpresa == null || idEmpresa == 0) ps.setNull(1, java.sql.Types.INTEGER); else ps.setInt(1, idEmpresa);
            ps.setString(2, clase);
            ps.setString(3, provincia);
            ps.setString(4, municipio);
            ps.setString(5, localidad);
            ps.setString(6, cp);
            ps.setString(7, direccion);
            ps.setString(8, margen);
            if (longitud == null) ps.setNull(9, java.sql.Types.NUMERIC); else ps.setDouble(9, longitud);
            if (latitud == null) ps.setNull(10, java.sql.Types.NUMERIC); else ps.setDouble(10, latitud);
            ps.setString(11, rem);
            ps.setString(12, tipoServicio);
            ps.setString(13, horario);
            ps.setString(14, tipoVenta);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        throw new SQLException("No se pudo insertar estación: " + direccion);
    }
}