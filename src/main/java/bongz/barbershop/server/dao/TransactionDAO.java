package bongz.barbershop.server.dao;

import bongz.barbershop.model.TransactionModel;
import bongz.barbershop.model.enums.TransactionStatus;
import bongz.barbershop.server.core.JDBC;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    public boolean insertTransaction(TransactionModel transaction) {
        String sql = """
                INSERT INTO transactions (
                    barber_id,
                    pricing_category_id,
                    logged_by_user_id,
                    business_date,
                    recorded_at,
                    pricing_category_code_snapshot,
                    pricing_category_name_snapshot,
                    charged_amount_pesos,
                    barber_commission_percent,
                    barber_earning_amount_pesos,
                    shop_earning_amount_pesos,
                    status,
                    void_reason,
                    note
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = JDBC.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, transaction.getBarberId());
            ps.setInt(2, transaction.getPricingCategoryId());
            ps.setInt(3, transaction.getLoggedByUserId());
            ps.setString(4, transaction.getBusinessDate());
            ps.setString(5, transaction.getRecordedAt());
            ps.setString(6, transaction.getPricingCategoryCodeSnapshot());
            ps.setString(7, transaction.getPricingCategoryNameSnapshot());
            ps.setInt(8, transaction.getChargedAmountPesos());
            ps.setInt(9, transaction.getBarberCommissionPercent());
            ps.setInt(10, transaction.getBarberEarningAmountPesos());
            ps.setInt(11, transaction.getShopEarningAmountPesos());
            ps.setString(12, transaction.getStatus());
            ps.setString(13, transaction.getVoidReason());
            ps.setString(14, transaction.getNote());

            int affectedRows = ps.executeUpdate();
            if (affectedRows != 1) {
                return false;
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    transaction.setTransactionId(generatedKeys.getInt(1));
                }
            }

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public TransactionModel findById(int transactionId) {
        String sql = "SELECT * FROM transactions WHERE transaction_id = ?";

        try (Connection conn = JDBC.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, transactionId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTransaction(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean voidTransaction(int transactionId, String voidReason) {
        String sql = """
                UPDATE transactions
                SET status = 'VOID',
                    void_reason = ?
                WHERE transaction_id = ?
                  AND status = 'POSTED'
                """;

        try (Connection conn = JDBC.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, voidReason);
            ps.setInt(2, transactionId);

            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<TransactionModel> getAllTransactions() {
        return executeQueryForList(
                "SELECT * FROM transactions ORDER BY business_date, recorded_at, transaction_id");
    }

    public List<TransactionModel> getTransactionsByBusinessDate(String businessDate) {
        String sql = """
                SELECT * FROM transactions
                WHERE business_date = ?
                ORDER BY recorded_at, transaction_id
                """;

        List<TransactionModel> transactions = new ArrayList<>();

        try (Connection conn = JDBC.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, businessDate);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToTransaction(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transactions;
    }

    public List<TransactionModel> getTransactionsByBarberAndBusinessDate(int barberId, String businessDate) {
        String sql = """
                SELECT * FROM transactions
                WHERE barber_id = ?
                  AND business_date = ?
                ORDER BY recorded_at, transaction_id
                """;

        List<TransactionModel> transactions = new ArrayList<>();

        try (Connection conn = JDBC.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, barberId);
            ps.setString(2, businessDate);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToTransaction(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transactions;
    }

    private TransactionModel mapResultSetToTransaction(ResultSet rs) throws SQLException {
        return new TransactionModel(
                rs.getInt("transaction_id"),
                rs.getInt("barber_id"),
                rs.getInt("pricing_category_id"),
                rs.getInt("logged_by_user_id"),
                rs.getString("business_date"),
                rs.getString("recorded_at"),
                rs.getString("pricing_category_code_snapshot"),
                rs.getString("pricing_category_name_snapshot"),
                rs.getInt("charged_amount_pesos"),
                rs.getInt("barber_commission_percent"),
                rs.getInt("barber_earning_amount_pesos"),
                rs.getInt("shop_earning_amount_pesos"),
                TransactionStatus.fromValue(rs.getString("status")).name(),
                rs.getString("void_reason"),
                rs.getString("note"));
    }

    private List<TransactionModel> executeQueryForList(String sql) {
        List<TransactionModel> transactions = new ArrayList<>();

        try (Connection conn = JDBC.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transactions;
    }
}
