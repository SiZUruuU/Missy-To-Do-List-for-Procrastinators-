package Components.Tasks;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import Components.Utilities.EventBus;

public class TaskService {

    private final TaskRepository repository;
    private final EventBus eventBus;

    public TaskService(TaskRepository repository, EventBus eventBus) {
        this.repository = Objects.requireNonNull(repository, "TaskRepository cannot be null");
        this.eventBus = Objects.requireNonNull(eventBus, "EventBus cannot be null");
    }

    public List<Tasks> getTasks() throws SQLException {
        return repository.getTasks();
    }

    public int createTask(
            String taskName,
            LocalDate deadline,
            Priority priority,
            String desc
    ) throws SQLException {
        int id = repository.add(taskName, deadline, priority, desc);

        if (id > 0) {
            eventBus.publish(new TaskCreatedEvent(id));
        }

        return id;
    }

    public boolean updateTask(Tasks task) throws SQLException {
        Objects.requireNonNull(task, "Task cannot be null");

        boolean updated = repository.update(task);

        if (updated) {
            eventBus.publish(new TaskUpdatedEvent(task.id()));
        }

        return updated;
    }

    public boolean markTaskCompleted(int id, boolean isDone) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("Task ID must be positive");
        }

        boolean updated = repository.markCompleted(id, isDone);

        if (updated) {
            if (isDone) {
                eventBus.publish(new TaskCompletedEvent(id));
            } else {
                eventBus.publish(new TaskUpdatedEvent(id));
            }
        }

        return updated;
    }

    public boolean deleteTask(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("Task ID must be positive");
        }

        boolean deleted = repository.delete(id);

        if (deleted) {
            eventBus.publish(new TaskDeletedEvent(id));
        }

        return deleted;
    }
}
