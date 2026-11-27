package Components.UIComponents;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import Components.Tasks.Priority;
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
import Components.Utilities.KeyHandler;
import Components.Utilities.MouseHandler;
import Components.Utilities.NavigationController;
import Components.Utilities.ScreenChangedEvent;
import Components.Utilities.States;

public class Panel extends JPanel {

    public static final int SCREEN_WIDTH = 960;
    public static final int SCREEN_HEIGHT = 680;

    private final TaskService taskService;
    private final EventBus eventBus;
    private final NavigationController navigationController;

    private final ButtonManager bm;
    final KeyHandler key;
    private final MouseHandler mouse;
    private final FontManager fm = new FontManager();
    private final General gen;
    final Font maruFont;

    private List<Tasks> tasks = List.of();
    private int selectedTaskId = -1;
    private int scrollOffset = 0;
    private String statusMessage = "Connected to PostgreSQL | Live EventBus active";

    private final Consumer<TaskCreatedEvent> onTaskCreated =
        e -> onTaskMutated("EventBus: TaskCreatedEvent (#" + e.taskId() + ")");
    private final Consumer<TaskUpdatedEvent> onTaskUpdated =
        e -> onTaskMutated("EventBus: TaskUpdatedEvent (#" + e.taskId() + ")");
    private final Consumer<TaskCompletedEvent> onTaskCompleted =
        e -> onTaskMutated("EventBus: TaskCompletedEvent (#" + e.taskId() + ")");
    private final Consumer<TaskDeletedEvent> onTaskDeleted =
        e -> onTaskMutated("EventBus: TaskDeletedEvent (#" + e.taskId() + ")");
    private final Consumer<ScreenChangedEvent> onScreenChanged = e -> {
        if (e.current() == States.MAIN) {
            SwingUtilities.invokeLater(() -> {
                statusMessage = "Returned to MAIN from " + e.previous();
                loadTasksFromDb();
                repaint();
            });
        }
    };

    public Panel(
            TaskService taskService,
            EventBus eventBus,
            NavigationController navigationController
    ) {
        this.taskService = Objects.requireNonNull(taskService, "TaskService cannot be null");
        this.eventBus = Objects.requireNonNull(eventBus, "EventBus cannot be null");
        this.navigationController = Objects.requireNonNull(navigationController, "NavigationController cannot be null");

        this.maruFont = loadMaruFont();
        this.bm = new ButtonManager(this);
        this.key = new KeyHandler(this::repaint);
        this.mouse = new MouseHandler(bm, this::handleRowClick);
        this.gen = new General(this);

        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setLayout(null);
        this.setDoubleBuffered(true);

        this.addMouseListener(mouse);
        this.addKeyListener(key);
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();
            }
        });
        this.addMouseWheelListener(e -> {
            int maxScroll = gen.getMaxScrollOffset(tasks.size());
            scrollOffset = Math.max(0, Math.min(maxScroll, scrollOffset + e.getWheelRotation() * 36));
            repaint();
        });

        setupButtons();
        subscribeToEvents();
        loadTasksFromDb();
    }

    private Font loadMaruFont() {
        try {
            return fm.loadFont("/Assets/Fonts/x12y16pxMaruMonica.ttf");
        } catch (RuntimeException e) {
            return new Font("SansSerif", Font.PLAIN, 16);
        }
    }

    private void setupButtons() {
        Color accentBg = Color.decode("#5e60ce");
        Color accentBorder = Color.decode("#4849a8");
        Color dangerBg = Color.decode("#c1121f");
        Color dangerBorder = Color.decode("#9b0d17");
        Color navBg = Color.decode("#e9e6f5");
        Color navBorder = Color.decode("#bdb5dc");
        Color darkText = Color.decode("#22223b");

        // Top-right navigation buttons
        bm.addButton(new Buttons(
            new Rectangle(700, 20, 110, 38),
            () -> navigationController.navigateTo(States.MISSY),
            "Missy",
            navBg, navBorder, darkText, maruFont
        ));
        bm.addButton(new Buttons(
            new Rectangle(822, 20, 110, 38),
            () -> navigationController.navigateTo(States.SETTINGS),
            "Settings",
            navBg, navBorder, darkText, maruFont
        ));

        // Task CRUD toolbar buttons
        bm.addButton(new Buttons(
            new Rectangle(28, 88, 142, 38),
            this::openAddTaskDialog,
            "+ Add Task",
            accentBg, accentBorder, Color.WHITE, maruFont
        ));
        bm.addButton(new Buttons(
            new Rectangle(182, 88, 96, 38),
            this::openEditTaskDialog,
            "Edit",
            Color.WHITE, Color.decode("#c7c2d6"), darkText, maruFont
        ));
        bm.addButton(new Buttons(
            new Rectangle(290, 88, 144, 38),
            this::toggleSelectedTaskDone,
            "Toggle Done",
            Color.WHITE, Color.decode("#c7c2d6"), darkText, maruFont
        ));
        bm.addButton(new Buttons(
            new Rectangle(446, 88, 104, 38),
            this::deleteSelectedTask,
            "Delete",
            dangerBg, dangerBorder, Color.WHITE, maruFont
        ));
        bm.addButton(new Buttons(
            new Rectangle(562, 88, 108, 38),
            () -> {
                statusMessage = "Reloaded tasks from PostgreSQL";
                loadTasksFromDb();
                repaint();
            },
            "Refresh",
            Color.WHITE, Color.decode("#c7c2d6"), darkText, maruFont
        ));
    }

    private void subscribeToEvents() {
        eventBus.subscribe(TaskCreatedEvent.class, onTaskCreated);
        eventBus.subscribe(TaskUpdatedEvent.class, onTaskUpdated);
        eventBus.subscribe(TaskCompletedEvent.class, onTaskCompleted);
        eventBus.subscribe(TaskDeletedEvent.class, onTaskDeleted);
        eventBus.subscribe(ScreenChangedEvent.class, onScreenChanged);
    }

    public void dispose() {
        eventBus.unsubscribe(TaskCreatedEvent.class, onTaskCreated);
        eventBus.unsubscribe(TaskUpdatedEvent.class, onTaskUpdated);
        eventBus.unsubscribe(TaskCompletedEvent.class, onTaskCompleted);
        eventBus.unsubscribe(TaskDeletedEvent.class, onTaskDeleted);
        eventBus.unsubscribe(ScreenChangedEvent.class, onScreenChanged);
    }

    private void onTaskMutated(String message) {
        SwingUtilities.invokeLater(() -> {
            statusMessage = message;
            loadTasksFromDb();
            repaint();
        });
    }

    private void loadTasksFromDb() {
        try {
            this.tasks = taskService.getTasks();
            boolean selectionStillExists = tasks.stream().anyMatch(t -> t.id() == selectedTaskId);
            if (!selectionStillExists) {
                selectedTaskId = -1;
            }
            int maxScroll = gen.getMaxScrollOffset(tasks.size());
            scrollOffset = Math.min(scrollOffset, maxScroll);
        } catch (SQLException e) {
            statusMessage = "DB load error: " + e.getMessage();
            JOptionPane.showMessageDialog(
                this,
                "Failed to load tasks from PostgreSQL:\n" + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void handleRowClick(int x, int y) {
        selectedTaskId = gen.findTaskIdAt(x, y);
        repaint();
    }

    private Tasks getSelectedTask() {
        if (selectedTaskId <= 0) {
            return null;
        }
        return tasks.stream()
            .filter(t -> t.id() == selectedTaskId)
            .findFirst()
            .orElse(null);
    }

    private void openAddTaskDialog() {
        JTextField titleField = new JTextField();
        JTextField descField = new JTextField();
        JTextField deadlineField = new JTextField(LocalDate.now().plusDays(1).toString());
        JComboBox<Priority> priorityBox = new JComboBox<>(Priority.values());
        priorityBox.setSelectedItem(Priority.MEDIUM);

        JPanel form = new JPanel(new GridLayout(0, 1, 6, 6));
        form.add(new JLabel("Task Name:"));
        form.add(titleField);
        form.add(new JLabel("Description:"));
        form.add(descField);
        form.add(new JLabel("Deadline (YYYY-MM-DD):"));
        form.add(deadlineField);
        form.add(new JLabel("Priority:"));
        form.add(priorityBox);

        int result = JOptionPane.showConfirmDialog(
            this,
            form,
            "Add New Task",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String title = titleField.getText().trim();
        if (title.isBlank()) {
            JOptionPane.showMessageDialog(this, "Task name cannot be blank.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDate deadline;
        try {
            deadline = LocalDate.parse(deadlineField.getText().trim());
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Please enter deadline as YYYY-MM-DD (e.g., 2026-12-01).", "Invalid Date", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Priority priority = (Priority) priorityBox.getSelectedItem();
        String desc = descField.getText().trim();

        try {
            int newId = taskService.createTask(title, deadline, priority, desc);
            selectedTaskId = newId;
        } catch (SQLException | IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Could not create task:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openEditTaskDialog() {
        Tasks selected = getSelectedTask();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Click a task row first to edit it.", "No Task Selected", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JTextField titleField = new JTextField(selected.taskName());
        JTextField descField = new JTextField(selected.desc());
        JTextField deadlineField = new JTextField(selected.deadline().toString());
        JComboBox<Priority> priorityBox = new JComboBox<>(Priority.values());
        priorityBox.setSelectedItem(selected.priority());
        JCheckBox doneCheck = new JCheckBox("Completed", selected.is_done());

        JPanel form = new JPanel(new GridLayout(0, 1, 6, 6));
        form.add(new JLabel("Task Name:"));
        form.add(titleField);
        form.add(new JLabel("Description:"));
        form.add(descField);
        form.add(new JLabel("Deadline (YYYY-MM-DD):"));
        form.add(deadlineField);
        form.add(new JLabel("Priority:"));
        form.add(priorityBox);
        form.add(doneCheck);

        int result = JOptionPane.showConfirmDialog(
            this,
            form,
            "Edit Task #" + selected.id(),
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String title = titleField.getText().trim();
        if (title.isBlank()) {
            JOptionPane.showMessageDialog(this, "Task name cannot be blank.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDate deadline;
        try {
            deadline = LocalDate.parse(deadlineField.getText().trim());
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Please enter deadline as YYYY-MM-DD.", "Invalid Date", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Priority priority = (Priority) priorityBox.getSelectedItem();
        String desc = descField.getText().trim();
        boolean isDone = doneCheck.isSelected();

        try {
            Tasks updated = new Tasks(selected.id(), title, desc, deadline, priority, isDone);
            taskService.updateTask(updated);
        } catch (SQLException | IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Could not update task:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void toggleSelectedTaskDone() {
        Tasks selected = getSelectedTask();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Click a task row first to toggle completion.", "No Task Selected", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try {
            taskService.markTaskCompleted(selected.id(), !selected.is_done());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Could not update task status:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedTask() {
        Tasks selected = getSelectedTask();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Click a task row first to delete it.", "No Task Selected", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Delete task #" + selected.id() + " (\"" + selected.taskName() + "\")?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            taskService.deleteTask(selected.id());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Could not delete task:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public List<Tasks> getTasks() {
        return tasks;
    }

    public int getSelectedTaskId() {
        return selectedTaskId;
    }

    public int getScrollOffset() {
        return scrollOffset;
    }

    public Font getMaruFont() {
        return maruFont;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        gen.draw(g2);
        bm.render(g2);
    }
}
