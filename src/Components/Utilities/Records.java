package Components.Utilities;

//Just to rename the file to "Records"
public enum Records{}

record TaskCompletionEvent(int taskID) implements AppEvent{}

record TaskCreatedEvent(int taskID) implements  AppEvent{}
