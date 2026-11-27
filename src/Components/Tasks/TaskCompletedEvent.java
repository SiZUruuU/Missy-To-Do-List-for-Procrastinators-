package Components.Tasks;

import Components.Utilities.AppEvent;

public record TaskCompletedEvent(int taskId) implements  AppEvent{

    public TaskCompletedEvent{

        if(taskId <= 0){
            throw new IllegalArgumentException("Task ID cannot be negative");
        }
    }    
}
