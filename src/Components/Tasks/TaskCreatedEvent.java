package Components.Tasks;

import Components.Utilities.AppEvent;

public record TaskCreatedEvent(int taskId) implements AppEvent{

    public TaskCreatedEvent{
        if(taskId <= 0){
            throw new IllegalArgumentException("Task ID cannot be negative");
        }
    }
}
