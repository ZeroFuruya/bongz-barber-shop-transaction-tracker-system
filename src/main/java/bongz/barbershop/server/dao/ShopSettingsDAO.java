package bongz.barbershop.server.dao;

import bongz.barbershop.model.ShopSettingsModel;
import bongz.barbershop.server.core.JDBC;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ShopSettingsDAO {

    public ShopSettingsModel getSettings() {
        String sql = "SELECT * FROM shop_settings WHERE settings_id = 1";

        try (Connection conn = JDBC.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return mapResultSetToSettings(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean updateSettings(ShopSettingsModel settings) {
        String sql = """
                UPDATE shop_settings
                SET shop_name = ?,
                    currency_code = ?,
                    owner_notes = ?,
                    updated_at = CURRENT_TIMESTAMP
                WHERE settings_id = 1
                """;

        try (Connection conn = JDBC.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, settings.getShopName());
            ps.setString(2, settings.getCurrencyCode());
            ps.setString(3, settings.getOwnerNotes());

            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    private ShopSettingsModel mapResultSetToSettings(ResultSet rs) throws SQLException {
        return new ShopSettingsModel(
                rs.getInt("settings_id"),
                rs.getString("shop_name"),
                rs.getString("currency_code"),
                rs.getString("owner_notes"),
                rs.getString("updated_at"));
    }
}
