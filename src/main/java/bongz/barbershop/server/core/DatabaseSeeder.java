package bongz.barbershop.server.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Seeds a small but realistic dataset so the database is useful immediately for
 * development, query testing, and UI prototyping.
 */
public final class DatabaseSeeder {

    private DatabaseSeeder() {
    }

    public static void seed(Connection connection) throws SQLException {
        executeStatements(connection, seedStatements());
    }

    private static void executeStatements(Connection connection, List<String> statements) throws SQLException {
        for (String sql : statements) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.executeUpdate();
            }
        }
    }

    private static List<String> seedStatements() {
        return List.of(
                """
                        INSERT OR IGNORE INTO shop_settings (
                            settings_id,
                            shop_name,
                            currency_code
                        ) VALUES (
                            1,
                            'Bongz Barbershop',
                            'PHP'
                        )
                        """,
                """
                        INSERT OR IGNORE INTO pricing_categories (
                            pricing_category_id,
                            code,
                            name,
                            description,
                            charged_amount_pesos,
                            barber_commission_percent,
                            is_default,
                            is_active,
                            sort_order
                        ) VALUES
                            (1, 'STANDARD', 'Standard', 'Default regular haircut price', 100, 40, 1, 1, 1),
                            (2, 'STUDENT', 'Student', 'Student discounted haircut', 90, 40, 0, 1, 2),
                            (3, 'PWD', 'PWD', 'PWD discounted haircut', 80, 40, 0, 1, 3),
                            (4, 'SENIOR', 'Senior', 'Senior citizen discounted haircut', 80, 40, 0, 1, 4),
                            (5, 'APRILPROMO', 'April Promo', 'Limited time promo haircut', 70, 40, 0, 1, 5),
                            (6, 'OLDPROMO', 'Old Promo', 'Previous promo kept for historical reporting', 75, 40, 0, 0, 6)
                        """,
                """
                        INSERT OR IGNORE INTO users (
                            user_id,
                            username,
                            password_hash,
                            role,
                            is_active
                        ) VALUES
                            (-99, 'qwe', 'qweqwe`', 'OWNER', 1),
                            (1, 'owner_bongz', 'owner123', 'OWNER', 1),
                            (2, 'mila_manager', 'manager123', 'MANAGER', 1),
                            (3, 'dave_manager', 'manager456', 'MANAGER', 1),
                            (4, 'old_manager', 'oldmanager', 'MANAGER', 0),
                            (1001, 'owner_prince', 'asdasd', 'OWNER', 1)
                        """,
                """
                        INSERT OR IGNORE INTO barbers (
                            barber_id,
                            name,
                            image_path,
                            display_order,
                            is_active
                        ) VALUES
                            (1, 'Roben', 'barber-images/roben.jpg', 1, 1),
                            (2, 'Prince', 'barber-images/prince.jpg', 2, 1),
                            (3, 'Uli', 'barber-images/uli.jpg', 3, 1),
                            (4, 'Pop', 'barber-images/pop.jpg', 4, 1),
                            (5, 'Marco', 'barber-images/marco.jpg', 5, 0)
                        """,
                """
                        INSERT OR IGNORE INTO transactions (
                            transaction_id,
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
                        ) VALUES
                            (1, 5, 1, 4, '2026-03-29', '2026-03-29 08:10:00', 'STANDARD', 'Standard', 100, 40, 40, 60, 'POSTED', NULL, 'Historic haircut before barber deactivation'),
                            (2, 5, 1, 4, '2026-03-29', '2026-03-29 09:00:00', 'STANDARD', 'Standard', 100, 40, 40, 60, 'POSTED', NULL, NULL),
                            (3, 1, 1, 2, '2026-03-29', '2026-03-29 10:15:00', 'STANDARD', 'Standard', 100, 40, 40, 60, 'POSTED', NULL, NULL),
                            (4, 2, 2, 2, '2026-03-29', '2026-03-29 11:05:00', 'STUDENT', 'Student', 90, 40, 36, 54, 'POSTED', NULL, NULL),
                            (5, 3, 6, 4, '2026-03-29', '2026-03-29 12:00:00', 'OLDPROMO', 'Old Promo', 75, 40, 30, 45, 'POSTED', NULL, 'Historical promo category'),
                            (6, 4, 1, 3, '2026-03-29', '2026-03-29 13:20:00', 'STANDARD', 'Standard', 100, 40, 40, 60, 'POSTED', NULL, NULL),
                            (7, 1, 1, 2, '2026-03-30', '2026-03-30 09:00:00', 'STANDARD', 'Standard', 100, 40, 40, 60, 'POSTED', NULL, NULL),
                            (8, 1, 2, 2, '2026-03-30', '2026-03-30 09:40:00', 'STUDENT', 'Student', 90, 40, 36, 54, 'POSTED', NULL, NULL),
                            (9, 2, 1, 2, '2026-03-30', '2026-03-30 10:10:00', 'STANDARD', 'Standard', 100, 40, 40, 60, 'POSTED', NULL, NULL),
                            (10, 2, 3, 3, '2026-03-30', '2026-03-30 10:50:00', 'PWD', 'PWD', 80, 40, 32, 48, 'POSTED', NULL, NULL),
                            (11, 3, 1, 3, '2026-03-30', '2026-03-30 11:30:00', 'STANDARD', 'Standard', 100, 40, 40, 60, 'POSTED', NULL, NULL),
                            (12, 4, 4, 3, '2026-03-30', '2026-03-30 12:10:00', 'SENIOR', 'Senior', 80, 40, 32, 48, 'POSTED', NULL, NULL),
                            (13, 4, 1, 3, '2026-03-30', '2026-03-30 13:00:00', 'STANDARD', 'Standard', 100, 40, 40, 60, 'VOID', 'Duplicate entry', 'Voided test case'),
                            (14, 1, 1, 2, '2026-03-31', '2026-03-31 09:05:00', 'STANDARD', 'Standard', 100, 40, 40, 60, 'POSTED', NULL, NULL),
                            (15, 1, 3, 3, '2026-03-31', '2026-03-31 09:45:00', 'PWD', 'PWD', 80, 40, 32, 48, 'POSTED', NULL, NULL),
                            (16, 2, 1, 2, '2026-03-31', '2026-03-31 10:00:00', 'STANDARD', 'Standard', 100, 40, 40, 60, 'POSTED', NULL, NULL),
                            (17, 2, 1, 2, '2026-03-31', '2026-03-31 10:35:00', 'STANDARD', 'Standard', 100, 40, 40, 60, 'POSTED', NULL, NULL),
                            (18, 3, 2, 3, '2026-03-31', '2026-03-31 11:10:00', 'STUDENT', 'Student', 90, 40, 36, 54, 'POSTED', NULL, NULL),
                            (19, 3, 1, 3, '2026-03-31', '2026-03-31 11:40:00', 'STANDARD', 'Standard', 100, 40, 40, 60, 'POSTED', NULL, NULL),
                            (20, 4, 5, 2, '2026-03-31', '2026-03-31 12:15:00', 'APRILPROMO', 'April Promo', 70, 40, 28, 42, 'POSTED', NULL, 'Promo haircut'),
                            (21, 4, 1, 2, '2026-03-31', '2026-03-31 12:50:00', 'STANDARD', 'Standard', 100, 40, 40, 60, 'VOID', 'Wrong barber selected', 'Voided test case'),
                            (22, 1, 1, 2, '2026-04-01', '2026-04-01 08:55:00', 'STANDARD', 'Standard', 100, 40, 40, 60, 'POSTED', NULL, NULL),
                            (23, 1, 1, 2, '2026-04-01', '2026-04-01 09:20:00', 'STANDARD', 'Standard', 100, 40, 40, 60, 'POSTED', NULL, NULL),
                            (24, 2, 2, 3, '2026-04-01', '2026-04-01 10:00:00', 'STUDENT', 'Student', 90, 40, 36, 54, 'POSTED', NULL, NULL),
                            (25, 2, 1, 3, '2026-04-01', '2026-04-01 10:30:00', 'STANDARD', 'Standard', 100, 40, 40, 60, 'POSTED', NULL, NULL),
                            (26, 3, 3, 2, '2026-04-01', '2026-04-01 11:00:00', 'PWD', 'PWD', 80, 40, 32, 48, 'POSTED', NULL, NULL),
                            (27, 4, 1, 2, '2026-04-01', '2026-04-01 11:45:00', 'STANDARD', 'Standard', 100, 40, 40, 60, 'POSTED', NULL, NULL),
                            (28, 4, 4, 2, '2026-04-01', '2026-04-01 12:30:00', 'SENIOR', 'Senior', 80, 40, 32, 48, 'POSTED', NULL, NULL),
                            (29, 1, 5, 1, '2026-04-01', '2026-04-01 13:00:00', 'APRILPROMO', 'April Promo', 70, 40, 28, 42, 'POSTED', NULL, 'Owner logged promo transaction'),
                            (30, 2, 1, 1, '2026-04-01', '2026-04-01 13:40:00', 'STANDARD', 'Standard', 100, 40, 40, 60, 'POSTED', NULL, 'Owner logged standard transaction')
                        """);
    }
}
