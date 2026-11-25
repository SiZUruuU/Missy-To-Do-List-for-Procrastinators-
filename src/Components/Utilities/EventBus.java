package Components.Utilities;

import java.util.*;
import java.util.function.Consumer;

public class EventBus {

    private final Map<Class<? extends AppEvent>,
            List<Consumer<? extends AppEvent>>> listeners =
            new HashMap<>();

    public <T extends AppEvent> void subscribe(
            Class<T> type,
            Consumer<T> listener
    ) {
        listeners.computeIfAbsent(
            type, ignored -> new ArrayList<>()
        ).add(listener);
    }

    @SuppressWarnings("unchecked")
    public <T extends AppEvent> void publish(T event) {

        List<Consumer<? extends AppEvent>> subscribers =
                listeners.getOrDefault(
                    event.getClass(),
                    List.of()
                );

        for (Consumer<? extends AppEvent> subscriber :
                List.copyOf(subscribers)) {

            ((Consumer<T>) subscriber).accept(event);
        }
    }
}