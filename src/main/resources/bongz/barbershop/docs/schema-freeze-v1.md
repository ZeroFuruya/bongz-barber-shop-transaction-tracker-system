# Schema Freeze v1

## Scope
This system records walk-in haircut transactions for one barbershop.
One haircut = one transaction row.
No clients, no inventory, no session duration, no service catalog.

## Money Standard
All money is stored as INTEGER centavos.
Example: PHP 100.00 = 10000.

## Naming Standard
Primary keys end in `_id`.
Foreign keys end in `_id`.
Booleans use `is_`.
Timestamps use `_at`.
Percent fields use `_percent`.
Money fields use `_centavos`.

## Canonical Tables
users(user_id, username, password_hash, role, is_active, created_at)
barbers(barber_id, name, image_path, display_order, is_active, created_at)
pricing_categories(pricing_category_id, code, name, description, charged_amount_centavos, barber_commission_percent, is_default, is_active, sort_order, created_at)
shop_settings(settings_id, shop_name, currency_code, updated_at)
transactions(transaction_id, barber_id, pricing_category_id, logged_by_user_id, business_date, recorded_at, pricing_category_code_snapshot, pricing_category_name_snapshot, charged_amount_centavos, barber_commission_percent, barber_earning_amount_centavos, shop_earning_amount_centavos, status, void_reason, note)

## Business Rules
Exactly one active default pricing category exists.
Inactive barbers cannot be used for new transactions.
Inactive pricing categories cannot be used for new transactions.
Posted transactions are never deleted; mistakes are corrected by VOID.
Barbers and users are deactivated, not hard-deleted, once referenced by transactions.
All earnings math is done in TransactionService, not controllers.
barbers.image_path stores a relative path like `barber-images/roben.jpg`.
business_date uses `YYYY-MM-DD`.

## Earnings Formula
barber_earning_amount_centavos = round(charged_amount_centavos * barber_commission_percent / 100.0)
shop_earning_amount_centavos = charged_amount_centavos - barber_earning_amount_centavos

## Permissions
MANAGER can create and void transactions, and view daily totals.
OWNER can do manager actions plus manage barbers, pricing categories, users, and settings.

## Out of Scope
Clients
Inventory/tools
Session duration
Appointments
Multiple services per transaction
