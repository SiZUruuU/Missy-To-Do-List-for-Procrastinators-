
package Components.Utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class EventBus {

    // Event type -> listeners interested in that event.
    private final Map<Class<? extends AppEvent>,
            List<Consumer<? extends AppEvent>>> listeners = new HashMap<>();

    // Register a listener for one event type.
    public <T extends AppEvent> void subscribe(
            Class<T> type,
            Consumer<T> listener) {

        Objects.requireNonNull(type, "Event type cannot be null");
        Objects.requireNonNull(listener, "Listener cannot be null");

        listeners.computeIfAbsent(type, ignored -> new ArrayList<>())
                 .add(listener);
    }

    // Remove a previously registered listener.
    public <T extends AppEvent> void unsubscribe(
            Class<T> type,
            Consumer<T> listener) {

        Objects.requireNonNull(type, "Event type cannot be null");
        Objects.requireNonNull(listener, "Listener cannot be null");

        List<Consumer<? extends AppEvent>> subscribers = listeners.get(type);

        if (subscribers == null) {
            return;
        }

        subscribers.remove(listener);

        if (subscribers.isEmpty()) {
            listeners.remove(type);
        }
    }

    // Notify every listener registered for the event's exact class.
    @SuppressWarnings("unchecked")
    public <T extends AppEvent> void publish(T event) {

        Objects.requireNonNull(event, "Event cannot be null");

        List<Consumer<? extends AppEvent>> subscribers =
                listeners.get(event.getClass());

        if (subscribers == null) {
            return;
        }

        for (Consumer<? extends AppEvent> subscriber :
                List.copyOf(subscribers)) {

            try {
                ((Consumer<T>) subscriber).accept(event);

            } catch (RuntimeException error) {
                System.err.println("Listener failed for "
                        + event.getClass().getSimpleName());

                error.printStackTrace();
            }
        }
    }
}