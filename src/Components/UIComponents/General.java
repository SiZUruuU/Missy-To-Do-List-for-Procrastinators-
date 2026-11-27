package Components.UIComponents;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.util.List;

import Components.Tasks.Priority;
import Components.Tasks.Tasks;
import Components.Utilities.TextRenderer;

public class General {

    static final int LIST_X = 28;
    static final int LIST_Y = 148;
    static final int LIST_WIDTH = 904;
    static final int LIST_HEIGHT = 486;
    static final int ROW_PADDING = 14;
    static final int ROW_HEIGHT = 72;
    static final int ROW_GAP = 10;

    private static final Color BG_COLOR = Color.decode("#f2f1f3");
    private static final Color CARD_BG = Color.decode("#ffffff");
    private static final Color CARD_BORDER = Color.decode("#d8d5de");
    private static final Color ROW_BG = Color.decode("#faf9fc");
    private static final Color ROW_SELECTED_BG = Color.decode("#ede9fe");
    private static final Color ROW_SELECTED_BORDER = Color.decode("#5e60ce");
    private static final Color TEXT_PRIMARY = Color.decode("#22223b");
    private static final Color TEXT_MUTED = Color.decode("#6c6a7c");

    private final TextRenderer textRender = new TextRenderer();
    private final Panel panel;

    public General(Panel panel) {
        this.panel = panel;
        panel.setBackground(BG_COLOR);
    }

    public void draw(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        List<Tasks> tasks = panel.getTasks();
        drawHeader(g2, tasks);
        drawSelectionHint(g2, tasks);
        drawTaskListContainer(g2, tasks);
        drawFooter(g2, tasks);
    }

    public int findTaskIdAt(int x, int y) {
        if (x < LIST_X + ROW_PADDING || x > LIST_X + LIST_WIDTH - ROW_PADDING
                || y < LIST_Y + ROW_PADDING || y > LIST_Y + LIST_HEIGHT - ROW_PADDING) {
            return -1;
        }

        List<Tasks> tasks = panel.getTasks();
        int scrollOffset = panel.getScrollOffset();

        for (int i = 0; i < tasks.size(); i++) {
            int rowY = LIST_Y + ROW_PADDING + i * (ROW_HEIGHT + ROW_GAP) - scrollOffset;
            Rectangle rowBounds = new Rectangle(
                LIST_X + ROW_PADDING,
                rowY,
                LIST_WIDTH - (ROW_PADDING * 2),
                ROW_HEIGHT
            );
            if (rowBounds.contains(x, y)) {
                return tasks.get(i).id();
            }
        }
        return -1;
    }

    public int getMaxScrollOffset(int taskCount) {
        if (taskCount <= 0) {
            return 0;
        }
        int contentHeight = (ROW_PADDING * 2) + taskCount * ROW_HEIGHT + (taskCount - 1) * ROW_GAP;
        return Math.max(0, contentHeight - LIST_HEIGHT);
    }

    private void drawHeader(Graphics2D g2, List<Tasks> tasks) {
        Font font = panel.getMaruFont();
        textRender.drawTextPlain(g2, "MISSY TO-DO", font, 40f, TEXT_PRIMARY, "BOLD", 28, 46);

        long completed = tasks.stream().filter(Tasks::is_done).count();
        long overdue = tasks.stream().filter(t -> "OVERDUE".equals(t.statusLabel())).count();
        long pending = tasks.size() - completed - overdue;

        String summary = String.format(
            "%d total   |   %d pending   |   %d completed   |   %d overdue",
            tasks.size(), pending, completed, overdue
        );
        textRender.drawTextPlain(g2, summary, font, 20f, TEXT_MUTED, "PLAIN", 30, 72);
    }

    private void drawSelectionHint(Graphics2D g2, List<Tasks> tasks) {
        Font font = panel.getMaruFont();
        int selectedId = panel.getSelectedTaskId();
        String hint = "Click any task row to select it";

        if (selectedId > 0) {
            for (Tasks task : tasks) {
                if (task.id() == selectedId) {
                    hint = "Selected: #" + task.id() + " " + truncate(task.taskName(), 24);
                    break;
                }
            }
        }

        textRender.drawTextPlain(g2, hint, font, 19f, TEXT_MUTED, "PLAIN", 698, 113);
    }

    private void drawTaskListContainer(Graphics2D g2, List<Tasks> tasks) {
        g2.setColor(CARD_BG);
        g2.fillRoundRect(LIST_X, LIST_Y, LIST_WIDTH, LIST_HEIGHT, 16, 16);

        Stroke oldStroke = g2.getStroke();
        g2.setColor(CARD_BORDER);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(LIST_X, LIST_Y, LIST_WIDTH, LIST_HEIGHT, 16, 16);
        g2.setStroke(oldStroke);

        if (tasks.isEmpty()) {
            Font font = panel.getMaruFont().deriveFont(Font.PLAIN, 26f);
            g2.setFont(font);
            g2.setColor(TEXT_MUTED);
            String emptyText = "No tasks yet. Click '+ Add Task' to create your first task!";
            FontMetrics fm = g2.getFontMetrics();
            int textX = LIST_X + (LIST_WIDTH - fm.stringWidth(emptyText)) / 2;
            int textY = LIST_Y + LIST_HEIGHT / 2;
            g2.drawString(emptyText, textX, textY);
            return;
        }

        Graphics2D clipG = (Graphics2D) g2.create();
        clipG.clipRect(LIST_X + 2, LIST_Y + 2, LIST_WIDTH - 4, LIST_HEIGHT - 4);

        int scrollOffset = panel.getScrollOffset();
        int selectedId = panel.getSelectedTaskId();

        for (int i = 0; i < tasks.size(); i++) {
            Tasks task = tasks.get(i);
            int rowY = LIST_Y + ROW_PADDING + i * (ROW_HEIGHT + ROW_GAP) - scrollOffset;

            if (rowY + ROW_HEIGHT < LIST_Y || rowY > LIST_Y + LIST_HEIGHT) {
                continue;
            }

            drawTaskRow(clipG, task, LIST_X + ROW_PADDING, rowY, LIST_WIDTH - (ROW_PADDING * 2), task.id() == selectedId);
        }

        clipG.dispose();
    }

    private void drawTaskRow(Graphics2D g2, Tasks task, int x, int y, int width, boolean selected) {
        Color statusColor = statusColor(task.statusLabel());
        Color priorityColor = priorityColor(task.priority());

        g2.setColor(selected ? ROW_SELECTED_BG : ROW_BG);
        g2.fillRoundRect(x, y, width, ROW_HEIGHT, 12, 12);

        Stroke oldStroke = g2.getStroke();
        g2.setColor(selected ? ROW_SELECTED_BORDER : CARD_BORDER);
        g2.setStroke(new BasicStroke(selected ? 2f : 1f));
        g2.drawRoundRect(x, y, width, ROW_HEIGHT, 12, 12);
        g2.setStroke(oldStroke);

        // Left status accent stripe
        g2.setColor(statusColor);
        g2.fillRoundRect(x + 4, y + 8, 6, ROW_HEIGHT - 16, 4, 4);

        Font font = panel.getMaruFont();
        String donePrefix = task.is_done() ? "[x] " : "[ ] ";
        String titleLine = donePrefix + "#" + task.id() + "  " + truncate(task.taskName(), 42);
        Color titleColor = task.is_done() ? TEXT_MUTED : TEXT_PRIMARY;
        textRender.drawTextPlain(g2, titleLine, font, 24f, titleColor, "BOLD", x + 22, y + 30);

        String descText = task.desc().isBlank() ? "" : truncate(task.desc(), 55);
        String metaLine = "Due: " + task.deadline() + "   |   " + descText;
        textRender.drawTextPlain(g2, metaLine, font, 19f, TEXT_MUTED, "PLAIN", x + 22, y + 56);

        // Right badges: Priority + Status
        drawBadge(g2, task.priority().name(), x + width - 220, y + 20, 92, 30, priorityColor);
        drawBadge(g2, task.statusLabel(), x + width - 118, y + 20, 106, 30, statusColor);
    }

    private void drawBadge(Graphics2D g2, String text, int x, int y, int w, int h, Color bg) {
        g2.setColor(bg);
        g2.fillRoundRect(x, y, w, h, 10, 10);

        Font badgeFont = panel.getMaruFont().deriveFont(Font.BOLD, 16f);
        g2.setFont(badgeFont);
        g2.setColor(Color.WHITE);

        FontMetrics fm = g2.getFontMetrics();
        int textX = x + (w - fm.stringWidth(text)) / 2;
        int textY = y + ((h - fm.getHeight()) / 2) + fm.getAscent();
        g2.drawString(text, textX, textY);
    }

    private void drawFooter(Graphics2D g2, List<Tasks> tasks) {
        Font font = panel.getMaruFont();
        String status = panel.getStatusMessage();
        textRender.drawTextPlain(g2, status, font, 18f, TEXT_MUTED, "PLAIN", 28, 660);

        if (getMaxScrollOffset(tasks.size()) > 0) {
            textRender.drawTextPlain(g2, "Use mouse wheel to scroll", font, 18f, TEXT_MUTED, "PLAIN", 752, 660);
        }
    }

    private Color statusColor(String status) {
        return switch (status) {
            case "COMPLETED" -> Color.decode("#2a9d8f");
            case "OVERDUE" -> Color.decode("#d62828");
            default -> Color.decode("#d99b16");
        };
    }

    private Color priorityColor(Priority priority) {
        return switch (priority) {
            case HIGH -> Color.decode("#9b2226");
            case MEDIUM -> Color.decode("#c99700");
            case LOW -> Color.decode("#e76f51");
        };
    }

    private String truncate(String value, int maxLen) {
        if (value == null || value.length() <= maxLen) {
            return (value == null) ? "" : value;
        }
        return value.substring(0, maxLen - 3) + "...";
    }
}
