package Components.Tasks;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TaskRepository {

    private final Database database;

    public TaskRepository(Database database) {
        this.database = Objects.requireNonNull(database, "Database cannot be null");
    }

    public List<Tasks> getTasks() throws SQLException {
        String sql = """
            SELECT id, title, description, deadline, priority, is_done
            FROM tasks
            ORDER BY is_done ASC, deadline ASC, id ASC
            """;

        List<Tasks> tasks = new ArrayList<>();

        try (Connection c = database.connect();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                tasks.add(mapRow(rs));
            }
        }

        return tasks;
    }

    public int add(String taskName, LocalDate deadline, Priority priority, String desc) throws SQLException {
        if (taskName == null || taskName.isBlank()) {
            throw new IllegalArgumentException("Task must contain a name");
        }
        Objects.requireNonNull(deadline, "Task must have a deadline");

        Priority safePriority = (priority == null) ? Priority.MEDIUM : priority;
        String safeDesc = (desc == null) ? "" : desc.trim();

        String sql = """
            INSERT INTO tasks (title, deadline, priority, description)
            VALUES (?, ?, ?, ?)
            RETURNING id
            """;

        try (Connection c = database.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, taskName.trim());
            ps.setDate(2, Date.valueOf(deadline));
            ps.setString(3, safePriority.name());
            ps.setString(4, safeDesc);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("id") : -1;
            }
        }
    }

    public boolean update(Tasks task) throws SQLException {
        Objects.requireNonNull(task, "Task cannot be null");

        String sql = """
            UPDATE tasks
            SET title = ?, description = ?, deadline = ?, priority = ?, is_done = ?
            WHERE id = ?
            """;

        try (Connection c = database.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, task.taskName());
            ps.setString(2, task.desc());
            ps.setDate(3, Date.valueOf(task.deadline()));
            ps.setString(4, task.priority().name());
            ps.setBoolean(5, task.is_done());
            ps.setInt(6, task.id());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean markCompleted(int id, boolean isDone) throws SQLException {
        String sql = "UPDATE tasks SET is_done = ? WHERE id = ?";

        try (Connection c = database.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setBoolean(1, isDone);
            ps.setInt(2, id);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM tasks WHERE id = ?";

        try (Connection c = database.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Tasks mapRow(ResultSet rs) throws SQLException {
        return new Tasks(
            rs.getInt("id"),
            rs.getString("title"),
            rs.getString("description"),
            rs.getDate("deadline").toLocalDate(),
            Priority.fromString(rs.getString("priority")),
            rs.getBoolean("is_done")
        );
    }
}
