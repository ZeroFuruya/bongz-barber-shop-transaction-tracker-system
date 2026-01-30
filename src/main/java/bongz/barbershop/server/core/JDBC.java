package bongz.barbershop.server.core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBC {

    private static final String DB_URL = "jdbc:sqlite:src/main/resources/bongz/barbershop/db/bongz_barbershop_db.db";

    private static Connection connection;

    private JDBC() {
    }

    public static Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(DB_URL);
                System.out.println("✅ SQLite connected");
            } catch (SQLException e) {
                throw new RuntimeException("❌ Database connection failed", e);
            }
        }
        return connection;
    }

    public static void close() {
        try {
            if (connection != null) {
                connection.close();
                connection = null;
                System.out.println("🔒 SQLite closed");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
