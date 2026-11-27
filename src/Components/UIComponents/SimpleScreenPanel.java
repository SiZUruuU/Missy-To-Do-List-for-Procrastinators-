package Components.UIComponents;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import Components.Tasks.TaskCompletedEvent;
import Components.Tasks.TaskCreatedEvent;
import Components.Tasks.TaskDeletedEvent;
import Components.Tasks.TaskService;
import Components.Tasks.TaskUpdatedEvent;
import Components.Tasks.Tasks;
import Components.Utilities.ButtonManager;
import Components.Utilities.Buttons;
import Components.Utilities.EventBus;
import Components.Utilities.FontManager;
import Components.Utilities.MouseHandler;
import Components.Utilities.NavigationController;
import Components.Utilities.ScreenChangedEvent;
import Components.Utilities.States;
import Components.Utilities.TextRenderer;

public class SimpleScreenPanel extends JPanel {

    private final States state;
    private final String title;
    private final String subtitle;
    private final TaskService taskService;
    private final EventBus eventBus;
    private final NavigationController navigationController;

    private final ButtonManager bm;
    private final MouseHandler mouse;
    private final TextRenderer textRender = new TextRenderer();
    private final Font maruFont;

    private String liveSummary = "";

    private final Consumer<TaskCreatedEvent> onCreated = e -> refreshSummaryLater();
    private final Consumer<TaskUpdatedEvent> onUpdated = e -> refreshSummaryLater();
    private final Consumer<TaskCompletedEvent> onCompleted = e -> refreshSummaryLater();
    private final Consumer<TaskDeletedEvent> onDeleted = e -> refreshSummaryLater();
    private final Consumer<ScreenChangedEvent> onScreenChanged;

    public SimpleScreenPanel(
            States state,
            String title,
            String subtitle,
            TaskService taskService,
            EventBus eventBus,
            NavigationController navigationController
    ) {
        this.state = Objects.requireNonNull(state, "State cannot be null");
        this.title = Objects.requireNonNull(title, "Title cannot be null");
        this.subtitle = (subtitle == null) ? "" : subtitle;
        this.taskService = taskService;
        this.eventBus = Objects.requireNonNull(eventBus, "EventBus cannot be null");
        this.navigationController = Objects.requireNonNull(navigationController, "NavigationController cannot be null");
        this.onScreenChanged = e -> {
            if (e.current() == this.state) {
                refreshSummaryLater();
            }
        };

        this.maruFont = loadMaruFont();
        this.bm = new ButtonManager(this);
        this.mouse = new MouseHandler(bm);

        this.setPreferredSize(new Dimension(Panel.SCREEN_WIDTH, Panel.SCREEN_HEIGHT));
        this.setBackground(Color.decode("#f2f1f3"));
        this.setLayout(null);
        this.setDoubleBuffered(true);
        this.addMouseListener(mouse);

        setupButtons();
        subscribeToEvents();
        refreshSummary();
    }

    private Font loadMaruFont() {
        try {
            return new FontManager().loadFont("/Assets/Fonts/x12y16pxMaruMonica.ttf");
        } catch (RuntimeException e) {
            return new Font("SansSerif", Font.PLAIN, 16);
        }
    }

    private void setupButtons() {
        Color navBg = Color.decode("#e9e6f5");
        Color navBorder = Color.decode("#bdb5dc");
        Color darkText = Color.decode("#22223b");

        bm.addButton(new Buttons(
            new Rectangle(28, 24, 110, 38),
            navigationController::goBack,
            "< Back",
            navBg, navBorder, darkText, maruFont
        ));

        if (state == States.SETTINGS) {
            bm.addButton(new Buttons(
                new Rectangle(154, 24, 140, 38),
                () -> navigationController.navigateTo(States.MISSY),
                "Open Missy",
                Color.WHITE, Color.decode("#c7c2d6"), darkText, maruFont
            ));
        } else if (state == States.MISSY) {
            bm.addButton(new Buttons(
                new Rectangle(154, 24, 140, 38),
                () -> navigationController.navigateTo(States.SETTINGS),
                "Settings",
                Color.WHITE, Color.decode("#c7c2d6"), darkText, maruFont
            ));
        }
    }

    private void subscribeToEvents() {
        eventBus.subscribe(TaskCreatedEvent.class, onCreated);
        eventBus.subscribe(TaskUpdatedEvent.class, onUpdated);
        eventBus.subscribe(TaskCompletedEvent.class, onCompleted);
        eventBus.subscribe(TaskDeletedEvent.class, onDeleted);
        eventBus.subscribe(ScreenChangedEvent.class, onScreenChanged);
    }

    public void dispose() {
        eventBus.unsubscribe(TaskCreatedEvent.class, onCreated);
        eventBus.unsubscribe(TaskUpdatedEvent.class, onUpdated);
        eventBus.unsubscribe(TaskCompletedEvent.class, onCompleted);
        eventBus.unsubscribe(TaskDeletedEvent.class, onDeleted);
        eventBus.unsubscribe(ScreenChangedEvent.class, onScreenChanged);
    }

    private void refreshSummaryLater() {
        SwingUtilities.invokeLater(() -> {
            refreshSummary();
            repaint();
        });
    }

    private void refreshSummary() {
        if (taskService == null) {
            liveSummary = "";
            return;
        }
        try {
            List<Tasks> all = taskService.getTasks();
            long completed = all.stream().filter(Tasks::is_done).count();
            long overdue = all.stream().filter(t -> "OVERDUE".equals(t.statusLabel())).count();
            long pending = all.size() - completed - overdue;
            liveSummary = String.format(
                "Live DB Status: %d total tasks  (%d pending, %d completed, %d overdue)",
                all.size(), pending, completed, overdue
            );
        } catch (SQLException e) {
            liveSummary = "Could not load task summary: " + e.getMessage();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g2.setColor(Color.WHITE);
        g2.fillRoundRect(28, 96, Panel.SCREEN_WIDTH - 56, 260, 16, 16);
        g2.setColor(Color.decode("#d8d5de"));
        g2.drawRoundRect(28, 96, Panel.SCREEN_WIDTH - 56, 260, 16, 16);

        Color darkText = Color.decode("#22223b");
        Color mutedText = Color.decode("#6c6a7c");

        textRender.drawTextPlain(g2, title, maruFont, 42f, darkText, "BOLD", 56, 162);
        textRender.drawTextPlain(g2, subtitle, maruFont, 22f, mutedText, "PLAIN", 56, 208);

        if (!liveSummary.isBlank()) {
            textRender.drawTextPlain(g2, liveSummary, maruFont, 22f, Color.decode("#5e60ce"), "BOLD", 56, 264);
        }

        textRender.drawTextPlain(
            g2,
            "Screen State: " + state.name() + "   |   Navigation stack + shared EventBus active",
            maruFont, 18f, mutedText, "PLAIN", 56, 324
        );

        bm.render(g2);
    }
}
