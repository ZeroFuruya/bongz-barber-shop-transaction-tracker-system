package bongz.barbershop.server.dao;

import bongz.barbershop.dto.report.BarberDashboardCardDTO;
import bongz.barbershop.dto.report.DailyBarberTotalDTO;
import bongz.barbershop.dto.report.DailyShopTotalDTO;
import bongz.barbershop.dto.report.PricingCategorySummaryDTO;
import bongz.barbershop.dto.transaction.TransactionViewDTO;
import bongz.barbershop.server.core.JDBC;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO {

    public List<BarberDashboardCardDTO> getBarberDashboardCards(String businessDate) {
        String sql = """
                SELECT
                    b.barber_id,
                    b.name,
                    b.image_path,
                    COALESCE(d.haircut_count, 0) AS haircut_count_today,
                    COALESCE(d.barber_commission_total_pesos, 0) AS barber_commission_today,
                    COALESCE(d.shop_share_total_pesos, 0) AS shop_share_today
                FROM barbers b
                LEFT JOIN daily_barber_totals d
                    ON d.barber_id = b.barber_id
                   AND d.business_date = ?
                WHERE b.is_active = 1
                ORDER BY b.display_order, b.barber_id
                """;

        List<BarberDashboardCardDTO> cards = new ArrayList<>();

        try (Connection conn = JDBC.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, businessDate);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    cards.add(new BarberDashboardCardDTO(
                            rs.getInt("barber_id"),
                            rs.getString("name"),
                            rs.getString("image_path"),
                            rs.getInt("haircut_count_today"),
                            rs.getInt("barber_commission_today"),
                            rs.getInt("shop_share_today")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cards;
    }

    public List<DailyBarberTotalDTO> getDailyBarberTotals(String businessDate) {
        String sql = """
                SELECT
                    business_date,
                    barber_id,
                    barber_name,
                    haircut_count,
                    gross_sales_pesos,
                    barber_commission_total_pesos,
                    shop_share_total_pesos
                FROM daily_barber_totals
                WHERE business_date = ?
                ORDER BY barber_name, barber_id
                """;

        return executeDailyBarberTotalsQuery(sql, businessDate);
    }

    public DailyShopTotalDTO getDailyShopTotal(String businessDate) {
        String sql = """
                SELECT
                    business_date,
                    haircut_count,
                    gross_sales_pesos,
                    total_barber_commissions_pesos,
                    total_shop_earnings_pesos
                FROM daily_shop_totals
                WHERE business_date = ?
                LIMIT 1
                """;

        try (Connection conn = JDBC.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, businessDate);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new DailyShopTotalDTO(
                            rs.getString("business_date"),
                            rs.getInt("haircut_count"),
                            rs.getInt("gross_sales_pesos"),
                            rs.getInt("total_barber_commissions_pesos"),
                            rs.getInt("total_shop_earnings_pesos"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new DailyShopTotalDTO(businessDate, 0, 0, 0, 0);
    }

    public List<DailyBarberTotalDTO> getDateRangeBarberTotals(String fromDate, String toDate) {
        String sql = """
                SELECT
                    t.business_date,
                    b.barber_id,
                    b.name AS barber_name,
                    COUNT(*) AS haircut_count,
                    SUM(t.charged_amount_pesos) AS gross_sales_pesos,
                    SUM(t.barber_earning_amount_pesos) AS barber_commission_total_pesos,
                    SUM(t.shop_earning_amount_pesos) AS shop_share_total_pesos
                FROM transactions t
                JOIN barbers b ON b.barber_id = t.barber_id
                WHERE t.status = 'POSTED'
                  AND t.business_date BETWEEN ? AND ?
                GROUP BY t.business_date, b.barber_id, b.name
                ORDER BY t.business_date, b.name, b.barber_id
                """;

        return executeDailyBarberTotalsQuery(sql, fromDate, toDate);
    }

    public List<DailyShopTotalDTO> getDateRangeShopTotals(String fromDate, String toDate) {
        String sql = """
                SELECT
                    business_date,
                    COUNT(*) AS haircut_count,
                    SUM(charged_amount_pesos) AS gross_sales_pesos,
                    SUM(barber_earning_amount_pesos) AS total_barber_commissions_pesos,
                    SUM(shop_earning_amount_pesos) AS total_shop_earnings_pesos
                FROM transactions
                WHERE status = 'POSTED'
                  AND business_date BETWEEN ? AND ?
                GROUP BY business_date
                ORDER BY business_date
                """;

        List<DailyShopTotalDTO> totals = new ArrayList<>();

        try (Connection conn = JDBC.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, fromDate);
            ps.setString(2, toDate);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    totals.add(new DailyShopTotalDTO(
                            rs.getString("business_date"),
                            rs.getInt("haircut_count"),
                            rs.getInt("gross_sales_pesos"),
                            rs.getInt("total_barber_commissions_pesos"),
                            rs.getInt("total_shop_earnings_pesos")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return totals;
    }

    public List<PricingCategorySummaryDTO> getPricingCategorySummary(String fromDate, String toDate) {
        String sql = """
                SELECT
                    pricing_category_id,
                    pricing_category_code_snapshot AS code,
                    pricing_category_name_snapshot AS name,
                    COUNT(*) AS haircut_count,
                    SUM(charged_amount_pesos) AS gross_sales,
                    SUM(barber_earning_amount_pesos) AS barber_commission_total,
                    SUM(shop_earning_amount_pesos) AS shop_share_total
                FROM transactions
                WHERE status = 'POSTED'
                  AND business_date BETWEEN ? AND ?
                GROUP BY pricing_category_id, pricing_category_code_snapshot, pricing_category_name_snapshot
                ORDER BY name, pricing_category_id
                """;

        List<PricingCategorySummaryDTO> summaries = new ArrayList<>();

        try (Connection conn = JDBC.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, fromDate);
            ps.setString(2, toDate);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    summaries.add(new PricingCategorySummaryDTO(
                            rs.getInt("pricing_category_id"),
                            rs.getString("code"),
                            rs.getString("name"),
                            rs.getInt("haircut_count"),
                            rs.getInt("gross_sales"),
                            rs.getInt("barber_commission_total"),
                            rs.getInt("shop_share_total")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return summaries;
    }

    public List<TransactionViewDTO> getTransactionViewsByBusinessDate(String businessDate) {
        String sql = baseTransactionViewSql() + """
                WHERE t.business_date = ?
                ORDER BY t.recorded_at, t.transaction_id
                """;

        List<TransactionViewDTO> transactions = new ArrayList<>();

        try (Connection conn = JDBC.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, businessDate);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapTransactionView(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transactions;
    }

    public List<TransactionViewDTO> getTransactionViewsByBarberAndBusinessDate(int barberId, String businessDate) {
        String sql = baseTransactionViewSql() + """
                WHERE t.barber_id = ?
                  AND t.business_date = ?
                ORDER BY t.recorded_at, t.transaction_id
                """;

        List<TransactionViewDTO> transactions = new ArrayList<>();

        try (Connection conn = JDBC.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, barberId);
            ps.setString(2, businessDate);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapTransactionView(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transactions;
    }

    public List<TransactionViewDTO> getTransactionViewsByDateRange(String fromDate, String toDate) {
        String sql = baseTransactionViewSql() + """
                WHERE t.business_date BETWEEN ? AND ?
                ORDER BY t.business_date, t.recorded_at, t.transaction_id
                """;

        return executeTransactionViewsQuery(sql, fromDate, toDate);
    }

    public List<TransactionViewDTO> getTransactionViewsByBarberAndDateRange(int barberId, String fromDate, String toDate) {
        String sql = baseTransactionViewSql() + """
                WHERE t.barber_id = ?
                  AND t.business_date BETWEEN ? AND ?
                ORDER BY t.business_date, t.recorded_at, t.transaction_id
                """;

        return executeTransactionViewsQuery(sql, barberId, fromDate, toDate);
    }

    public List<TransactionViewDTO> getTransactionViewsByPricingCategoryAndDateRange(int pricingCategoryId, String fromDate,
            String toDate) {
        String sql = baseTransactionViewSql() + """
                WHERE t.pricing_category_id = ?
                  AND t.business_date BETWEEN ? AND ?
                ORDER BY t.business_date, t.recorded_at, t.transaction_id
                """;

        return executeTransactionViewsQuery(sql, pricingCategoryId, fromDate, toDate);
    }

    private List<DailyBarberTotalDTO> executeDailyBarberTotalsQuery(String sql, String... params) {
        List<DailyBarberTotalDTO> totals = new ArrayList<>();

        try (Connection conn = JDBC.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                ps.setString(i + 1, params[i]);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    totals.add(new DailyBarberTotalDTO(
                            rs.getString("business_date"),
                            rs.getInt("barber_id"),
                            rs.getString("barber_name"),
                            rs.getInt("haircut_count"),
                            rs.getInt("gross_sales_pesos"),
                            rs.getInt("barber_commission_total_pesos"),
                            rs.getInt("shop_share_total_pesos")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return totals;
    }

    private String baseTransactionViewSql() {
        return """
                SELECT
                    t.transaction_id,
                    t.barber_id,
                    b.name AS barber_name,
                    t.pricing_category_id,
                    t.pricing_category_code_snapshot AS pricing_category_code,
                    t.pricing_category_name_snapshot AS pricing_category_name,
                    t.logged_by_user_id,
                    u.username AS logged_by_username,
                    t.business_date,
                    t.recorded_at,
                    t.charged_amount_pesos AS charged_amount,
                    t.barber_commission_percent,
                    t.barber_earning_amount_pesos AS barber_earning_amount,
                    t.shop_earning_amount_pesos AS shop_earning_amount,
                    t.status,
                    t.void_reason,
                    t.note
                FROM transactions t
                JOIN barbers b ON b.barber_id = t.barber_id
                JOIN users u ON u.user_id = t.logged_by_user_id
                """;
    }

    private TransactionViewDTO mapTransactionView(ResultSet rs) throws SQLException {
        return new TransactionViewDTO(
                rs.getInt("transaction_id"),
                rs.getInt("barber_id"),
                rs.getString("barber_name"),
                rs.getInt("pricing_category_id"),
                rs.getString("pricing_category_code"),
                rs.getString("pricing_category_name"),
                rs.getInt("logged_by_user_id"),
                rs.getString("logged_by_username"),
                rs.getString("business_date"),
                rs.getString("recorded_at"),
                rs.getInt("charged_amount"),
                rs.getInt("barber_commission_percent"),
                rs.getInt("barber_earning_amount"),
                rs.getInt("shop_earning_amount"),
                rs.getString("status"),
                rs.getString("void_reason"),
                rs.getString("note"));
    }

    private List<TransactionViewDTO> executeTransactionViewsQuery(String sql, Object... params) {
        List<TransactionViewDTO> transactions = new ArrayList<>();

        try (Connection conn = JDBC.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                Object value = params[i];
                if (value instanceof Integer integerValue) {
                    ps.setInt(i + 1, integerValue);
                } else {
                    ps.setString(i + 1, String.valueOf(value));
                }
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapTransactionView(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return transactions;
    }
}
