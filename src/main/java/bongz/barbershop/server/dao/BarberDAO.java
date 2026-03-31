package bongz.barbershop.server.dao;

import bongz.barbershop.model.BarberModel;
import bongz.barbershop.server.core.JDBC;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * BarberDAO (Data Access Object)
 * 
 * Think of a DAO as a "translator" between your Java code and the database.
 * 
 * WHY DO WE NEED THIS?
 * - Your database speaks SQL (a query language)
 * - Your Java program speaks Java (an object-oriented language)
 * - The DAO translates between these two languages
 * 
 * WHAT DOES THIS CLASS DO?
 * - It performs CRUD operations (Create, Read, Update, Delete) on the barbers
 * table
 * - It converts database rows (tables with columns) into Java objects
 * (BarberModel)
 * - It converts Java objects back into database rows when saving
 * 
 * ARCHITECTURE NOTE:
 * This follows the "Separation of Concerns" principle:
 * - Model (BarberModel): Holds the data (just a container)
 * - DAO (this class): Talks to the database (data access layer)
 * - Service/Controller: Contains business logic (brain of the app)
 * Each layer has ONE job, making code easier to understand and maintain.
 */
public class BarberDAO {

    /**
     * Find a barber by their ID
     * 
     * WHAT HAPPENS HERE:
     * 1. We write a SQL query with a "?" placeholder (this prevents SQL injection
     * attacks)
     * 2. We get a connection to the database (like opening a door to the database)
     * 3. We prepare the statement (fill in the "?" with the actual barberId)
     * 4. We execute the query and get back results
     * 5. If we find a barber, we convert the database row into a BarberModel object
     * 
     * TRY-WITH-RESOURCES:
     * The "try ( ... )" syntax automatically closes the connection when done,
     * even if an error occurs. Think of it like auto-closing a file after reading.
     * 
     * @param barberId - The unique ID of the barber we're looking for
     * @return BarberModel object if found, null if not found
     */
    public BarberModel findById(int barberId) {
        // SQL query: "SELECT *" means "get all columns from the barbers table where
        // barber_id matches"
        String sql = "SELECT * FROM barbers WHERE barber_id = ?";

        // Try-with-resources: Automatically closes connection and preparedStatement
        // when done
        try (Connection conn = JDBC.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            // Replace the "?" in the SQL with the actual barberId
            // This is SAFE because PreparedStatement prevents SQL injection
            ps.setInt(1, barberId); // The "1" means "first question mark"

            // Execute the query and get results
            try (ResultSet rs = ps.executeQuery()) {
                // ResultSet is like a cursor pointing at rows in a table
                // rs.next() moves to the next row and returns true if a row exists
                if (rs.next()) {
                    // We found a barber! Convert the database row to a Java object
                    return mapResultSetToBarber(rs);
                }
            }
        } catch (SQLException e) {
            // If something goes wrong with the database (connection lost, syntax error,
            // etc.)
            // Print the error so we can debug it
            e.printStackTrace();
        }

        // If we reach here, no barber was found (or an error occurred)
        return null;
    }

    /**
     * Find a barber by their name
     * 
     * ALMOST IDENTICAL to findById, but searches by name instead of ID
     * 
     * @param name - The name of the barber we're looking for
     * @return BarberModel object if found, null if not found
     */
    public BarberModel findByName(String name) {
        String sql = "SELECT * FROM barbers WHERE name = ?";

        try (Connection conn = JDBC.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            // setString because name is text (vs setInt for numbers)
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

    /**
     * Get ALL barbers (active and inactive)
     * 
     * USE CASE: Admin panel where you want to see all barbers, even deactivated
     * ones
     * 
     * @return List of all BarberModel objects (could be empty list if no barbers
     *         exist)
     */
    public List<BarberModel> getAllBarbers() {
        // No WHERE clause = get everything
        String sql = "SELECT * FROM barbers";
        // We extracted this logic to a helper method to avoid code duplication
        return executeQueryForList(sql);
    }

    /**
     * Get only ACTIVE barbers
     * 
     * USE CASE: Customer booking screen where you only show available barbers
     * 
     * is_active = 1 means active (1 = true in database terms)
     * is_active = 0 means inactive (0 = false)
     * 
     * @return List of active BarberModel objects
     */
    public List<BarberModel> getAllActiveBarbers() {
        String sql = "SELECT * FROM barbers WHERE is_active = 1";
        return executeQueryForList(sql);
    }

    /**
     * Check if a barber name already exists
     * 
     * USE CASE: Before creating a new barber, check if the name is taken
     * (to prevent duplicate names)
     * 
     * WHY "SELECT 1"?
     * We don't care about the actual data, just if a row exists.
     * "SELECT 1" is faster than "SELECT *" because it doesn't fetch all columns.
     * 
     * @param name - The name to check
     * @return true if name exists, false if available
     */
    public boolean nameExists(String name) {
        String sql = "SELECT 1 FROM barbers WHERE name = ?";

        try (Connection conn = JDBC.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);

            try (ResultSet rs = ps.executeQuery()) {
                // If rs.next() returns true, a row exists = name is taken
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // If error occurs, assume name doesn't exist (fail-safe approach)
        return false;
    }

    /**
     * Create a new barber in the database
     * 
     * INSERT = Add a new row to the table
     * 
     * NOTE: We don't insert barber_id because it's AUTO_INCREMENT
     * (the database automatically assigns the next available ID)
     * 
     * @param name - Name of the new barber
     * @return true if insert succeeded, false if it failed
     */
    public boolean insertBarber(String name) {
        // INSERT INTO barbers (columns) VALUES (values)
        // We set is_active = 1 by default (new barbers are active)
        String sql = "INSERT INTO barbers (name, is_active) VALUES (?, 1)";

        try (Connection conn = JDBC.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);

            // executeUpdate() returns the number of rows affected
            // For an INSERT, it should be 1 (one row added)
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Update an existing barber's information
     * 
     * UPDATE = Modify existing rows in the table
     * SET = Which columns to change
     * WHERE = Which row to change (IMPORTANT: without WHERE, ALL rows would be
     * updated!)
     * 
     * @param barber - BarberModel object containing the updated information
     * @return true if update succeeded, false if it failed
     */
    public boolean updateBarber(BarberModel barber) {
        // Update the name and is_active columns for a specific barber_id
        String sql = "UPDATE barbers SET name = ?, is_active = ? WHERE barber_id = ?";

        try (Connection conn = JDBC.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            // Fill in the "?" placeholders in order (1, 2, 3)
            ps.setString(1, barber.getName()); // First ? = name
            ps.setInt(2, barber.getIsActive()); // Second ? = is_active
            ps.setInt(3, barber.getBarberId()); // Third ? = barber_id (WHERE clause)

            // Should affect exactly 1 row (the barber we're updating)
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Activate or deactivate a barber
     * 
     * USE CASE:
     * - Barber goes on vacation? Set is_active = 0 (don't delete, just hide)
     * - Barber comes back? Set is_active = 1 (make visible again)
     * 
     * This is better than deleting because you preserve history
     * (appointments made with this barber in the past)
     * 
     * @param barberId - Which barber to update
     * @param isActive - 1 for active, 0 for inactive
     * @return true if update succeeded, false if it failed
     */
    public boolean setBarberActiveStatus(int barberId, int isActive) {
        String sql = "UPDATE barbers SET is_active = ? WHERE barber_id = ?";

        try (Connection conn = JDBC.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, isActive);
            ps.setInt(2, barberId);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Permanently delete a barber from the database
     * 
     * DELETE = Remove rows from the table (PERMANENT!)
     * 
     * WARNING: This is destructive! Once deleted, the data is gone forever.
     * Usually, it's better to use setBarberActiveStatus(id, 0) instead.
     * 
     * Only use DELETE if:
     * - The barber was created by mistake
     * - You're absolutely sure you want to remove all traces
     * 
     * @param barberId - ID of the barber to delete
     * @return true if delete succeeded, false if it failed
     */
    public boolean deleteBarber(int barberId) {
        String sql = "DELETE FROM barbers WHERE barber_id = ?";

        try (Connection conn = JDBC.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, barberId);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * HELPER METHOD: Convert a database row into a BarberModel object
     * 
     * WHY THIS EXISTS:
     * We do this conversion in multiple places (findById, findByName, findAll,
     * etc.)
     * Instead of copying the same code everywhere, we extract it into ONE method.
     * 
     * PRINCIPLE: DRY (Don't Repeat Yourself)
     * - Write code once
     * - Reuse it everywhere
     * - If you need to change how mapping works, change it in ONE place
     * 
     * HOW IT WORKS:
     * ResultSet is like a spreadsheet row. We read each column and put it into a
     * Java object.
     * 
     * @param rs - The ResultSet cursor pointing at a database row
     * @return A new BarberModel object with data from the database
     * @throws SQLException if there's an error reading from the ResultSet
     */
    private BarberModel mapResultSetToBarber(ResultSet rs) throws SQLException {
        // Create a new BarberModel by reading columns from the current row
        return new BarberModel(
                rs.getInt("barber_id"), // Get the barber_id column as an integer
                rs.getString("name"), // Get the name column as a string
                rs.getInt("is_active") // Get the is_active column as an integer
        );
    }

    /**
     * HELPER METHOD: Execute a query and return a list of barbers
     * 
     * WHY THIS EXISTS:
     * Both findAll() and findAllActive() do the same thing:
     * 1. Execute a query
     * 2. Loop through results
     * 3. Convert each row to a BarberModel
     * 4. Add to a list
     * 
     * Instead of duplicating this logic, we extract it here.
     * 
     * HOW IT WORKS:
     * - We pass in different SQL queries (with or without WHERE clause)
     * - This method executes the query and builds a list
     * - It uses mapResultSetToBarber() to convert each row
     * 
     * @param sql - The SQL query to execute
     * @return List of BarberModel objects (empty list if no results)
     */
    private List<BarberModel> executeQueryForList(String sql) {
        // Create an empty list to store barbers
        List<BarberModel> barbers = new ArrayList<>();

        try (Connection conn = JDBC.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            // Loop through all rows in the result
            // rs.next() moves to the next row and returns false when no more rows exist
            while (rs.next()) {
                // Convert current row to BarberModel and add to list
                barbers.add(mapResultSetToBarber(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Return the list (could be empty if no barbers found or error occurred)
        return barbers;
    }
}