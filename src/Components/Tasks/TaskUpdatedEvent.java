package Components.Tasks;

import Components.Utilities.AppEvent;

public record TaskUpdatedEvent(int taskId) implements AppEvent {

    public TaskUpdatedEvent{

        if(taskId <= 0){
            throw new IllegalArgumentException("taskID must be positive");
        }
    }

}
    

