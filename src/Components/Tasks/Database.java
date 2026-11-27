package Components.Tasks;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Properties;

public class Database {

    private static final Path CONFIG_PATH = Path.of("config.properties");

    private Properties loadConfig() throws SQLException {
        if (!Files.exists(CONFIG_PATH)) {
            throw new SQLException(
                "Missing config.properties in project root. Copy config.properties.example and fill in your DB credentials."
            );
        }

        Properties props = new Properties();
        try (InputStream in = Files.newInputStream(CONFIG_PATH)) {
            props.load(in);
        } catch (IOException e) {
            throw new SQLException("Failed to read config.properties", e);
        }

        String url = props.getProperty("db.url");
        String user = props.getProperty("db.user");
        String password = props.getProperty("db.password");

        if (url == null || url.isBlank() || user == null || user.isBlank() || password == null) {
            throw new SQLException("config.properties must define db.url, db.user, and db.password");
        }

        return props;
    }

    // Method for Database Connection
    public Connection connect() throws SQLException {
        Properties props = loadConfig();
        return DriverManager.getConnection(
            props.getProperty("db.url").trim(),
            props.getProperty("db.user").trim(),
            props.getProperty("db.password")
        );
    }

    // Verifies PostgreSQL connection and that the tasks table is accessible to missyapp
    public void table_init() throws SQLException {
        try (Connection c = connect();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT 1 FROM tasks LIMIT 1")) {
            // Table exists and missyapp has SELECT permission
        } catch (SQLException e) {
            throw new SQLException(
                "Cannot access 'tasks' table. Ensure PostgreSQL is running, config.properties is valid, and tasks table/grants were created in pgAdmin.",
                e
            );
        }
    }

    // Uses SQL queries to add tasks to the database
    public int add(String name, LocalDate deadline, String priority, String desc) throws SQLException {
        String sql = "INSERT INTO tasks (title, deadline, priority, description) VALUES (?, ?, ?, ?) RETURNING id";
        String normalizedPriority = (priority == null || priority.isBlank())
            ? "MEDIUM"
            : priority.trim().toUpperCase();

        try (Connection c = connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setDate(2, Date.valueOf(deadline));
            ps.setString(3, normalizedPriority);
            ps.setString(4, desc);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("id") : -1;
            }
        }
    }

    public boolean delete(int id) throws SQLException {
        try (Connection c = connect();
             PreparedStatement ps = c.prepareStatement("DELETE FROM tasks WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // Prints the table of tasks in the database
    public void printTable(String label) throws SQLException {
        System.out.println("   --- " + label + " ---");
        try (Connection c = connect();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(
                 "SELECT id, title, deadline, priority, is_done, description FROM tasks ORDER BY id")) {

            boolean any = false;
            while (rs.next()) {
                any = true;
                System.out.printf("   #%d | %-14s | %s | %s | %-6s | done=%b%n",
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getDate("deadline"),
                    rs.getString("priority"),
                    rs.getBoolean("is_done")
                );
            }
            if (!any) System.out.println("   (no rows)");
        }
    }

    public void getTasks() {
        int[] tasks;
    }

    public int counts() throws SQLException {
        try (Connection c = connect();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM tasks")) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public void clear() throws SQLException {
        try (Connection c = connect(); Statement st = c.createStatement()) {
            st.executeUpdate("DELETE FROM tasks");
        }
    }
}
