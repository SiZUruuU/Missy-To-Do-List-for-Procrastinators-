package Components.Tasks;

// //This is for Window Enums
// public enum States {
//     INTRO,
//     MAIN,
//     SETTINGS,
//     MISSY,
//     LOCKIN,
//     POMODORO, 
// }

//Enum for Priority label kay kapoy 
public enum Priority{
    LOW,
    MEDIUM,
    HIGH;

    public static Priority fromString(String value){
        if(value == null || value.isBlank()){
            return MEDIUM;
        }else{
            return Priority.valueOf(value.trim().toUpperCase());
        }
    }
}
