package bongz.barbershop.server.core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBC {

    private static final String DB_URL = "jdbc:sqlite:src/main/resources/bongz/barbershop/db/bongz_barbershop_db.db";

    private JDBC() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
}
