package bongz.barbershop.server.dao;

import bongz.barbershop.model.UserModel;
import bongz.barbershop.server.core.JDBC;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public UserModel findById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";

        try (Connection conn = JDBC.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUserModel(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public UserModel findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = JDBC.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUserModel(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean usernameExists(String username) {
        String sql = "SELECT 1 FROM users WHERE username = ?";

        try (Connection conn = JDBC.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean insertUser(String username, String passwordHash, String role) {
        String sql = """
                INSERT INTO users (username, password_hash, role, is_active)
                VALUES (?, ?, ?, 1)
                """;

        try (Connection conn = JDBC.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, passwordHash);
            ps.setString(3, role);

            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean insertUser(UserModel user) {
        String sql = """
                INSERT INTO users (username, password_hash, role, is_active)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection conn = JDBC.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getRole());
            ps.setInt(4, user.getIsActive());

            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateUser(UserModel user) {
        String sql = """
                UPDATE users
                SET username = ?,
                    password_hash = ?,
                    role = ?,
                    is_active = ?
                WHERE user_id = ?
                """;

        try (Connection conn = JDBC.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getRole());
            ps.setInt(4, user.getIsActive());
            ps.setInt(5, user.getId());

            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updatePassword(int userId, String newPasswordHash) {
        String sql = "UPDATE users SET password_hash = ? WHERE user_id = ?";

        try (Connection conn = JDBC.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newPasswordHash);
            ps.setInt(2, userId);

            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean setUserActiveStatus(int userId, int isActive) {
        String sql = "UPDATE users SET is_active = ? WHERE user_id = ?";

        try (Connection conn = JDBC.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, isActive);
            ps.setInt(2, userId);

            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<UserModel> getAllUsers() {
        return executeQueryForList("SELECT * FROM users ORDER BY username");
    }

    public List<UserModel> getAllActiveUsers() {
        return executeQueryForList("SELECT * FROM users WHERE is_active = 1 ORDER BY username");
    }

    private UserModel mapResultSetToUserModel(ResultSet rs) throws SQLException {
        return new UserModel(
                rs.getInt("user_id"),
                rs.getString("username"),
                rs.getString("password_hash"),
                rs.getString("role"),
                rs.getInt("is_active"),
                rs.getString("created_at"));
    }

    private UserModel mapResultSetToUserModelWithoutPassword(ResultSet rs) throws SQLException {
        return new UserModel(
                rs.getInt("user_id"),
                rs.getString("username"),
                null,
                rs.getString("role"),
                rs.getInt("is_active"),
                rs.getString("created_at"));
    }

    private List<UserModel> executeQueryForList(String sql) {
        List<UserModel> users = new ArrayList<>();

        try (Connection conn = JDBC.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                users.add(mapResultSetToUserModelWithoutPassword(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }
}
