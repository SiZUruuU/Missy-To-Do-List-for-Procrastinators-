package Components.Utilities;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;

public class Buttons {

    private static final Color DEFAULT_BG = Color.decode("#ffffff");
    private static final Color DEFAULT_BORDER = Color.decode("#c7c2d6");
    private static final Color DEFAULT_TEXT = Color.decode("#22223b");

    private final Rectangle bounds;
    private final Runnable action;
    private String label;
    private final Color bgColor;
    private final Color borderColor;
    private final Color textColor;
    private final Font font;

    public Buttons(
        Rectangle bounds,
        Runnable action,
        String label
    ) {
        this(bounds, action, label, DEFAULT_BG, DEFAULT_BORDER, DEFAULT_TEXT, null);
    }

    public Buttons(
        Rectangle bounds,
        Runnable action,
        String label,
        Color bgColor,
        Color borderColor,
        Color textColor,
        Font font
    ) {
        this.bounds = bounds;
        this.action = action;
        this.label = label;
        this.bgColor = (bgColor != null) ? bgColor : DEFAULT_BG;
        this.borderColor = (borderColor != null) ? borderColor : DEFAULT_BORDER;
        this.textColor = (textColor != null) ? textColor : DEFAULT_TEXT;
        this.font = font;
    }

    public boolean contains(int x, int y) {
        return bounds.contains(x, y);
    }

    public void click() {
        if (action != null) {
            action.run();
        }
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void draw(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g2.setColor(bgColor);
        g2.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 12, 12);

        Stroke oldStroke = g2.getStroke();
        g2.setColor(borderColor);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 12, 12);
        g2.setStroke(oldStroke);

        if (label != null && !label.isBlank()) {
            Font renderFont = (font != null)
                ? font.deriveFont(Font.BOLD, 18f)
                : new Font("SansSerif", Font.BOLD, 14);
            g2.setFont(renderFont);
            g2.setColor(textColor);

            FontMetrics fm = g2.getFontMetrics();
            int textX = bounds.x + (bounds.width - fm.stringWidth(label)) / 2;
            int textY = bounds.y + ((bounds.height - fm.getHeight()) / 2) + fm.getAscent();
            g2.drawString(label, textX, textY);
        }
    }
}
