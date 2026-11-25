package Components.Utilities;

import Components.UIComponents.ScreenManager;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class NavigationController {

    private final ScreenManager screenManager;
    private final CardLayout cardLayout;
    private final JPanel container;
    private final EventBus eventBus;

    public NavigationController(
            ScreenManager screenManager,
            CardLayout cardLayout,
            JPanel container,
            EventBus eventBus
    ) {
        this.screenManager = screenManager;
        this.cardLayout = cardLayout;
        this.container = container;
        this.eventBus = eventBus;
    }

    public void navigateTo(States next) {
        Objects.requireNonNull(next);
        requireEDT();

        States previous = screenManager.current();

        if (screenManager.push(next)) {
            showScreen(previous);
        }
    }

    public void goBack() {
        requireEDT();

        States previous = screenManager.current();

        if (screenManager.pop()) {
            showScreen(previous);
        }
    }

    private void showScreen(States previous) {
        States current = screenManager.current();

        cardLayout.show(container, current.name());

        eventBus.publish(
            new ScreenChangedEvent(previous, current)
        );
    }

    private void requireEDT() {
        if (!SwingUtilities.isEventDispatchThread()) {
            throw new IllegalStateException(
                "Navigation must run on the Swing EDT"
            );
        }
    }
}