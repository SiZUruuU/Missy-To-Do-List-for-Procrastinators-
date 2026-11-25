package Components.Utilities;


public record ScreenChangedEvent(States previous,States current) implements AppEvent {
    
}
