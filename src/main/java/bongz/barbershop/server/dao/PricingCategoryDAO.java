package bongz.barbershop.server.dao;

import bongz.barbershop.model.PricingCategoryModel;
import bongz.barbershop.server.core.JDBC;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PricingCategoryDAO {

    public PricingCategoryModel findById(int pricingCategoryId) {
        String sql = "SELECT * FROM pricing_categories WHERE pricing_category_id = ?";

        try (Connection conn = JDBC.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, pricingCategoryId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPricingCategory(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public PricingCategoryModel findByCode(String code) {
        String sql = "SELECT * FROM pricing_categories WHERE code = ?";

        try (Connection conn = JDBC.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, code);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPricingCategory(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public PricingCategoryModel getDefaultPricingCategory() {
        String sql = """
                SELECT * FROM pricing_categories
                WHERE is_default = 1 AND is_active = 1
                LIMIT 1
                """;

        try (Connection conn = JDBC.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return mapResultSetToPricingCategory(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean insertPricingCategory(PricingCategoryModel category) {
        String sql = """
                INSERT INTO pricing_categories (
                    code, name, description, charged_amount_pesos,
                    barber_commission_percent, is_default, is_active, sort_order
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = JDBC.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, category.getCode());
            ps.setString(2, category.getName());
            ps.setString(3, category.getDescription());
            ps.setInt(4, category.getChargedAmountPesos());
            ps.setInt(5, category.getBarberCommissionPercent());
            ps.setInt(6, category.getIsDefault());
            ps.setInt(7, category.getIsActive());
            ps.setInt(8, category.getSortOrder());

            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updatePricingCategory(PricingCategoryModel category) {
        String sql = """
                UPDATE pricing_categories
                SET code = ?,
                    name = ?,
                    description = ?,
                    charged_amount_pesos = ?,
                    barber_commission_percent = ?,
                    is_default = ?,
                    is_active = ?,
                    sort_order = ?
                WHERE pricing_category_id = ?
                """;

        try (Connection conn = JDBC.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, category.getCode());
            ps.setString(2, category.getName());
            ps.setString(3, category.getDescription());
            ps.setInt(4, category.getChargedAmountPesos());
            ps.setInt(5, category.getBarberCommissionPercent());
            ps.setInt(6, category.getIsDefault());
            ps.setInt(7, category.getIsActive());
            ps.setInt(8, category.getSortOrder());
            ps.setInt(9, category.getPricingCategoryId());

            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean setPricingCategoryActiveStatus(int pricingCategoryId, int isActive) {
        String sql = "UPDATE pricing_categories SET is_active = ? WHERE pricing_category_id = ?";

        try (Connection conn = JDBC.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, isActive);
            ps.setInt(2, pricingCategoryId);

            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean setDefaultPricingCategory(int pricingCategoryId) {
        String clearDefaultsSql = "UPDATE pricing_categories SET is_default = 0 WHERE is_default = 1";
        String setDefaultSql = """
                UPDATE pricing_categories
                SET is_default = 1, is_active = 1
                WHERE pricing_category_id = ?
                """;

        try (Connection conn = JDBC.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement clearDefaults = conn.prepareStatement(clearDefaultsSql);
                    PreparedStatement setDefault = conn.prepareStatement(setDefaultSql)) {

                clearDefaults.executeUpdate();
                setDefault.setInt(1, pricingCategoryId);
                int updated = setDefault.executeUpdate();

                conn.commit();
                return updated == 1;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<PricingCategoryModel> getAllPricingCategories() {
        return executeQueryForList(
                "SELECT * FROM pricing_categories ORDER BY sort_order, pricing_category_id");
    }

    public List<PricingCategoryModel> getAllActivePricingCategories() {
        return executeQueryForList(
                "SELECT * FROM pricing_categories WHERE is_active = 1 ORDER BY sort_order, pricing_category_id");
    }

    private PricingCategoryModel mapResultSetToPricingCategory(ResultSet rs) throws SQLException {
        return new PricingCategoryModel(
                rs.getInt("pricing_category_id"),
                rs.getString("code"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getInt("charged_amount_pesos"),
                rs.getInt("barber_commission_percent"),
                rs.getInt("is_default"),
                rs.getInt("is_active"),
                rs.getInt("sort_order"),
                rs.getString("created_at"));
    }

    private List<PricingCategoryModel> executeQueryForList(String sql) {
        List<PricingCategoryModel> categories = new ArrayList<>();

        try (Connection conn = JDBC.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                categories.add(mapResultSetToPricingCategory(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return categories;
    }
}
