package dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class PrecioDAO {
    private static final int BATCH_SIZE = 500;

    public static void batchInsert(Connection conn, List<PrecioRecord> records) throws SQLException {
        String sql = "INSERT INTO precio (id_estacion, id_carburante, precio, hora_datos) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            int count = 0;
            for (PrecioRecord r : records) {
                if (r.idEstacion == null || r.idCarburante == null || r.precio == null) continue;
                ps.setInt(1, r.idEstacion);
                ps.setInt(2, r.idCarburante);
                ps.setBigDecimal(3, r.precio);
                if (r.horaDatos == null) ps.setTimestamp(4, null); else ps.setTimestamp(4, Timestamp.valueOf(r.horaDatos));
                ps.addBatch();
                if (++count % BATCH_SIZE == 0) ps.executeBatch();
            }
            if (count % BATCH_SIZE != 0) ps.executeBatch();
        }
    }

    // helper class
    public static class PrecioRecord {
        public Integer idEstacion;
        public Integer idCarburante;
        public BigDecimal precio;
        public java.time.LocalDateTime horaDatos;

        public PrecioRecord(Integer idEstacion, Integer idCarburante, BigDecimal precio, java.time.LocalDateTime horaDatos) {
            this.idEstacion = idEstacion;
            this.idCarburante = idCarburante;
            this.precio = precio;
            this.horaDatos = horaDatos;
        }
    }
}