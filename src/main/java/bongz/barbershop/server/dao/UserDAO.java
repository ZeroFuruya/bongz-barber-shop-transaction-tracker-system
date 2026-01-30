package bongz.barbershop.server.dao;

import bongz.barbershop.model.UserModel;
import bongz.barbershop.server.core.JDBC;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDAO {

    public UserModel findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";

        try {
            Connection conn = JDBC.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new UserModel(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getString("role"),
                        rs.getInt("is_active"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean usernameExists(String username) {
        return findByUsername(username) != null;
    }

    public boolean insertUser(String username, String passwordHash, String role) {
        String sql = """
                    INSERT INTO users (username, password_hash, role)
                    VALUES (?, ?, ?)
                """;

        try {
            Connection conn = JDBC.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, passwordHash);
            ps.setString(3, role);

            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
