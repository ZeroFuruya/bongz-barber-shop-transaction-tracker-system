package bongz.barbershop.server.dao;

import bongz.barbershop.model.BarberModel;
import bongz.barbershop.server.core.JDBC;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BarberDAO {

    public BarberModel findById(int barberId) {
        String sql = "SELECT * FROM barbers WHERE barber_id = ?";

        try (Connection conn = JDBC.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, barberId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBarber(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public BarberModel findByName(String name) {
        String sql = "SELECT * FROM barbers WHERE name = ?";

        try (Connection conn = JDBC.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBarber(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean nameExists(String name) {
        String sql = "SELECT 1 FROM barbers WHERE name = ?";

        try (Connection conn = JDBC.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean insertBarber(String name) {
        BarberModel barber = new BarberModel(0, name, null, getNextDisplayOrder(), 1, null);
        return insertBarber(barber);
    }

    public boolean insertBarber(BarberModel barber) {
        String sql = """
                INSERT INTO barbers (name, image_path, display_order, is_active)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection conn = JDBC.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, barber.getName());
            ps.setString(2, barber.getImagePath());
            ps.setInt(3, barber.getDisplayOrder());
            ps.setInt(4, barber.getIsActive());

            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateBarber(BarberModel barber) {
        String sql = """
                UPDATE barbers
                SET name = ?,
                    image_path = ?,
                    display_order = ?,
                    is_active = ?
                WHERE barber_id = ?
                """;

        try (Connection conn = JDBC.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, barber.getName());
            ps.setString(2, barber.getImagePath());
            ps.setInt(3, barber.getDisplayOrder());
            ps.setInt(4, barber.getIsActive());
            ps.setInt(5, barber.getBarberId());

            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateDisplayOrder(int barberId, int displayOrder) {
        String sql = "UPDATE barbers SET display_order = ? WHERE barber_id = ?";

        try (Connection conn = JDBC.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, displayOrder);
            ps.setInt(2, barberId);

            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean setBarberActiveStatus(int barberId, int isActive) {
        String sql = "UPDATE barbers SET is_active = ? WHERE barber_id = ?";

        try (Connection conn = JDBC.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, isActive);
            ps.setInt(2, barberId);

            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<BarberModel> getAllBarbers() {
        return executeQueryForList("SELECT * FROM barbers ORDER BY display_order, barber_id");
    }

    public List<BarberModel> getAllActiveBarbers() {
        return executeQueryForList(
                "SELECT * FROM barbers WHERE is_active = 1 ORDER BY display_order, barber_id");
    }

    private int getNextDisplayOrder() {
        String sql = "SELECT COALESCE(MAX(display_order), 0) + 1 FROM barbers";

        try (Connection conn = JDBC.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 1;
    }

    private BarberModel mapResultSetToBarber(ResultSet rs) throws SQLException {
        return new BarberModel(
                rs.getInt("barber_id"),
                rs.getString("name"),
                rs.getString("image_path"),
                rs.getInt("display_order"),
                rs.getInt("is_active"),
                rs.getString("created_at"));
    }

    private List<BarberModel> executeQueryForList(String sql) {
        List<BarberModel> barbers = new ArrayList<>();

        try (Connection conn = JDBC.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                barbers.add(mapResultSetToBarber(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return barbers;
    }
}
