package Components.Utilities;

import Components.Utilities.States;

public record ScreenChangedEvent(States previous,States current) implements AppEvent {
    
}
