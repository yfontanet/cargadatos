package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // Lee de variables de entorno o usa valores por defecto
    private static final String URL = System.getenv().getOrDefault("DB_URL", "jdbc:postgresql://localhost:5432/act1_xml");
    private static final String USER = System.getenv().getOrDefault("DB_USER", "alumna");
    private static final String PASS = System.getenv().getOrDefault("DB_PASS", "postgres");

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Postgres driver not found. Añade dependencia en pom.xml");
            e.printStackTrace();
        }
    }
    // Método para obtener la conexión a la base de datos
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}