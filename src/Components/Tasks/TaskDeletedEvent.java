package Components.Tasks;

import Components.Utilities.AppEvent;

public record TaskDeletedEvent(int taskId) implements AppEvent{
    
    public TaskDeletedEvent{
        
        if(taskId <= 0){
            throw new IllegalArgumentException("ID must be positive");
        }
    }
}
