package Components.Tasks;

import java.time.LocalDate;
import java.util.Objects;

public record Tasks(
    int id,
    String taskName,
    String desc,
    LocalDate deadline,
    Priority priority,
    boolean is_done
) {

    public Tasks{

        if(id <= 0){throw new IllegalArgumentException("ID must be greater than 0");}
        if(taskName == null || taskName.isBlank()){throw new IllegalArgumentException("Task must contain a name");}

        taskName = taskName.trim();
        desc = (desc == null)? "" : desc.trim();
        Objects.requireNonNull(deadline, "Task must have a deadline");

        priority = (priority == null)? Priority.MEDIUM : priority;
    }

    public String statusLabel(){
        if(is_done){
            return "COMPLETED";
        }
        if(deadline.isBefore(LocalDate.now())){
            return "OVERDUE";
        }
        else return "PENDING";
    }
    
}
