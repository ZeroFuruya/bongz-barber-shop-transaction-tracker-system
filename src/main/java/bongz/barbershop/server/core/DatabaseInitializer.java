package bongz.barbershop.server.core;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * Creates the SQLite schema the first time the app boots.
 *
 * This class is intentionally small and explicit:
 * - schema creation lives here
 * - sample/dev seed data lives in DatabaseSeeder
 * - application logic stays out of the bootstrap layer
 */
public final class DatabaseInitializer {

    private static boolean initialized;

    private DatabaseInitializer() {
    }

    public static synchronized void initialize() {
        if (initialized) {
            return;
        }

        try (Connection connection = JDBC.getConnection()) {
            connection.setAutoCommit(false);

            executeStatements(connection, schemaStatements());
            DatabaseSeeder.seed(connection);

            connection.commit();
            initialized = true;
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to initialize the SQLite database", exception);
        }
    }

    private static void executeStatements(Connection connection, List<String> statements) throws SQLException {
        for (String sql : statements) {
            try (Statement statement = connection.createStatement()) {
                statement.execute(sql);
            }
        }
    }

    private static List<String> schemaStatements() {
        return List.of(
                """
                        CREATE TABLE IF NOT EXISTS users (
                            user_id INTEGER PRIMARY KEY AUTOINCREMENT,
                            username TEXT NOT NULL COLLATE NOCASE UNIQUE,
                            password_hash TEXT NOT NULL,
                            role TEXT NOT NULL CHECK (role IN ('OWNER', 'MANAGER')),
                            is_active INTEGER NOT NULL DEFAULT 1 CHECK (is_active IN (0, 1)),
                            created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
                        )
                        """,
                """
                        CREATE TABLE IF NOT EXISTS barbers (
                            barber_id INTEGER PRIMARY KEY AUTOINCREMENT,
                            name TEXT NOT NULL COLLATE NOCASE UNIQUE,
                            image_path TEXT,
                            display_order INTEGER NOT NULL DEFAULT 0,
                            is_active INTEGER NOT NULL DEFAULT 1 CHECK (is_active IN (0, 1)),
                            created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
                        )
                        """,
                """
                        CREATE TABLE IF NOT EXISTS pricing_categories (
                            pricing_category_id INTEGER PRIMARY KEY AUTOINCREMENT,
                            code TEXT NOT NULL COLLATE NOCASE UNIQUE,
                            name TEXT NOT NULL,
                            description TEXT,
                            charged_amount_pesos INTEGER NOT NULL CHECK (charged_amount_pesos >= 0),
                            barber_commission_percent INTEGER NOT NULL CHECK (barber_commission_percent BETWEEN 0 AND 100),
                            is_default INTEGER NOT NULL DEFAULT 0 CHECK (is_default IN (0, 1)),
                            is_active INTEGER NOT NULL DEFAULT 1 CHECK (is_active IN (0, 1)),
                            sort_order INTEGER NOT NULL DEFAULT 0,
                            created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            CHECK (is_default = 0 OR is_active = 1)
                        )
                        """,
                """
                        CREATE TABLE IF NOT EXISTS shop_settings (
                            settings_id INTEGER PRIMARY KEY CHECK (settings_id = 1),
                            shop_name TEXT NOT NULL DEFAULT 'Bongz Barbershop',
                            currency_code TEXT NOT NULL DEFAULT 'PHP',
                            updated_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
                        )
                        """,
                """
                        CREATE TABLE IF NOT EXISTS transactions (
                            transaction_id INTEGER PRIMARY KEY AUTOINCREMENT,
                            barber_id INTEGER NOT NULL,
                            pricing_category_id INTEGER NOT NULL,
                            logged_by_user_id INTEGER NOT NULL,
                            business_date TEXT NOT NULL
                                CHECK (business_date GLOB '[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]'),
                            recorded_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            pricing_category_code_snapshot TEXT NOT NULL,
                            pricing_category_name_snapshot TEXT NOT NULL,
                            charged_amount_pesos INTEGER NOT NULL CHECK (charged_amount_pesos >= 0),
                            barber_commission_percent INTEGER NOT NULL CHECK (barber_commission_percent BETWEEN 0 AND 100),
                            barber_earning_amount_pesos INTEGER NOT NULL CHECK (barber_earning_amount_pesos >= 0),
                            shop_earning_amount_pesos INTEGER NOT NULL CHECK (shop_earning_amount_pesos >= 0),
                            status TEXT NOT NULL DEFAULT 'POSTED'
                                CHECK (status IN ('POSTED', 'VOID')),
                            void_reason TEXT,
                            note TEXT,
                            FOREIGN KEY (barber_id)
                                REFERENCES barbers(barber_id)
                                ON DELETE RESTRICT
                                ON UPDATE RESTRICT,
                            FOREIGN KEY (pricing_category_id)
                                REFERENCES pricing_categories(pricing_category_id)
                                ON DELETE RESTRICT
                                ON UPDATE RESTRICT,
                            FOREIGN KEY (logged_by_user_id)
                                REFERENCES users(user_id)
                                ON DELETE RESTRICT
                                ON UPDATE RESTRICT,
                            CHECK (charged_amount_pesos = barber_earning_amount_pesos + shop_earning_amount_pesos),
                            CHECK (
                                (status = 'POSTED' AND void_reason IS NULL) OR
                                (status = 'VOID' AND void_reason IS NOT NULL)
                            )
                        )
                        """,
                """
                        CREATE UNIQUE INDEX IF NOT EXISTS idx_pricing_categories_one_default
                            ON pricing_categories (is_default)
                            WHERE is_default = 1
                        """,
                """
                        CREATE INDEX IF NOT EXISTS idx_transactions_business_date
                            ON transactions (business_date)
                        """,
                """
                        CREATE INDEX IF NOT EXISTS idx_transactions_barber_date
                            ON transactions (barber_id, business_date)
                        """,
                """
                        CREATE INDEX IF NOT EXISTS idx_transactions_pricing_date
                            ON transactions (pricing_category_id, business_date)
                        """,
                """
                        CREATE VIEW IF NOT EXISTS daily_barber_totals AS
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
                        GROUP BY t.business_date, b.barber_id, b.name
                        """,
                """
                        CREATE VIEW IF NOT EXISTS daily_shop_totals AS
                        SELECT
                            business_date,
                            COUNT(*) AS haircut_count,
                            SUM(charged_amount_pesos) AS gross_sales_pesos,
                            SUM(barber_earning_amount_pesos) AS total_barber_commissions_pesos,
                            SUM(shop_earning_amount_pesos) AS total_shop_earnings_pesos
                        FROM transactions
                        WHERE status = 'POSTED'
                        GROUP BY business_date
                        """);
    }
}
