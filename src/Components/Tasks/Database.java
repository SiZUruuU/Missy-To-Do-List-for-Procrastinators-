package Components.Tasks;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;


public class Database {

    private static final Path DIR = Path.of(System.getProperty("user.home"), "missy-todo");

    //Method for Database Connection
    public Connection connect() throws SQLException{

        try { 
            Files.createDirectories(DIR); 
        } catch (IOException e) { 
            throw new SQLException("Cannot create data folder", e); 
        } 

        return DriverManager.getConnection("jdbc:sqlite:" + DIR.resolve("todo.db")); 
    }


    //Method for Table Creation 
    public void table_init() throws SQLException {
        try (Connection c = connect(); Statement st = c.createStatement()){
            st.execute("""
                CREATE TABLE IF NOT EXISTS tasks (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    title TEXT NOT NULL,
                    deadline TEXT NOT NULL,
                    priority TEXT NOT NULL DEFAULT 'MEDIUM'
                        CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH')),
                    is_done INTEGER NOT NULL DEFAULT 0 CHECK (is_done IN (0, 1))
                )"""
            );
        }
    }


    //Uses SQL queries to add tasks to the database
    public int add(String name, LocalDate deadline, String priority) throws SQLException {
        String sql = "INSERT INTO tasks (title, deadline, priority) VALUES (?, ?, ?)"; 
        try (Connection c = connect(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) { 
            ps.setString(1, name); 
            ps.setString(2, deadline.toString()); 
            ps.setString(3, priority); 
            ps.executeUpdate(); 

            try (ResultSet k = ps.getGeneratedKeys()) { return k.next() ? k.getInt(1) : -1; } 
        }
    }

    public boolean delete(int id) throws SQLException {
        try (Connection c = connect(); PreparedStatement ps = c.prepareStatement("DELETE FROM tasks WHERE id = ?")) { 
            ps.setInt(1, id); 
            return ps.executeUpdate() > 0; 
        }
    }


    //Prints the table of tasks in the database
    public void printTable(String label) throws SQLException {
        System.out.println("   --- " + label + " ---");
        try (Connection c = connect();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(
                 "SELECT id, title, deadline, priority, is_done FROM tasks ORDER BY id")) {

            boolean any = false;
            while (rs.next()) {
                any = true;
                System.out.printf("   #%d | %-14s | %s | %-6s | done=%d%n",
                        rs.getInt("id"), rs.getString("title"), rs.getString("deadline"),
                        rs.getString("priority"), rs.getInt("is_done"));
            }
            if (!any) System.out.println("   (no rows)");
        }
    }

    public int counts() throws SQLException {
        try (Connection c = connect();
            Statement st = c.createStatement();
            ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM tasks")) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }
}
