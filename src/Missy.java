import java.awt.CardLayout;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import Components.Tasks.Database;
import Components.Tasks.TaskRepository;
import Components.Tasks.TaskService;
import Components.UIComponents.Panel;
import Components.UIComponents.ScreenManager;
import Components.UIComponents.SimpleScreenPanel;
import Components.Utilities.EventBus;
import Components.Utilities.NavigationController;
import Components.Utilities.States;

public class Missy {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Database db = new Database();
            try {
                db.table_init();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(
                    null,
                    "Database startup check failed:\n" + e.getMessage(),
                    "MISSY TO-DO — Database Error",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            // One shared EventBus for the entire application
            EventBus eventBus = new EventBus();
            TaskRepository taskRepository = new TaskRepository(db);
            TaskService taskService = new TaskService(taskRepository, eventBus);

            CardLayout cardLayout = new CardLayout();
            JPanel container = new JPanel(cardLayout);

            ScreenManager screenManager = new ScreenManager();
            NavigationController navController =
                new NavigationController(screenManager, cardLayout, container, eventBus);

            Panel mainPanel = new Panel(taskService, eventBus, navController);
            SimpleScreenPanel missyPanel = new SimpleScreenPanel(
                States.MISSY,
                "Missy Companion",
                "Hi! I'm Missy. Your gamified companion features arrive in Stage 2+.",
                taskService, eventBus, navController
            );
            SimpleScreenPanel settingsPanel = new SimpleScreenPanel(
                States.SETTINGS,
                "Settings",
                "Database: PostgreSQL (missydb via config.properties) | Stage 1 Vertical Slice",
                taskService, eventBus, navController
            );
            SimpleScreenPanel introPanel = new SimpleScreenPanel(
                States.INTRO,
                "Welcome to Missy",
                "Intro screen scaffold.",
                taskService, eventBus, navController
            );
            SimpleScreenPanel lockInPanel = new SimpleScreenPanel(
                States.LOCKIN,
                "Lock-In Mode",
                "Planned for Stage 2+.",
                taskService, eventBus, navController
            );
            SimpleScreenPanel pomodoroPanel = new SimpleScreenPanel(
                States.POMODORO,
                "Pomodoro Timer",
                "Planned for Stage 2+.",
                taskService, eventBus, navController
            );

            container.add(mainPanel, States.MAIN.name());
            container.add(missyPanel, States.MISSY.name());
            container.add(settingsPanel, States.SETTINGS.name());
            container.add(introPanel, States.INTRO.name());
            container.add(lockInPanel, States.LOCKIN.name());
            container.add(pomodoroPanel, States.POMODORO.name());

            cardLayout.show(container, screenManager.current().name());

            JFrame frame = new JFrame("MISSY TO-DO");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.add(container);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            mainPanel.requestFocusInWindow();
        });
    }
}
