package bongz.barbershop.server.core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBC {

    private static final String DB_URL = "jdbc:sqlite:src/main/resources/bongz/barbershop/db/bongz_barbershop.db";

    private JDBC() {
    }

    public static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(DB_URL);

        // SQLite requires foreign keys to be enabled on each connection.
        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
        }

        return connection;
    }
}
