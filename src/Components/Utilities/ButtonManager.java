package Components.Utilities;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;

// A class that contains all interactable buttons
public class ButtonManager {

    private final JPanel panel;
    private final List<Buttons> buttons = new ArrayList<>();

    public ButtonManager(JPanel panel) {
        this.panel = panel;
    }

    public void addButton(Buttons button) {
        if (button != null) {
            buttons.add(button);
        }
    }

    public void removeButton(Buttons button) {
        buttons.remove(button);
    }

    public void clearButtons() {
        buttons.clear();
    }

    public void render(Graphics2D g2) {
        for (Buttons button : List.copyOf(buttons)) {
            button.draw(g2);
        }
    }

    public boolean handleClick(int x, int y) {
        List<Buttons> snapshot = List.copyOf(buttons);
        for (int i = snapshot.size() - 1; i >= 0; i--) {
            Buttons button = snapshot.get(i);

            if (button.contains(x, y)) {
                button.click();
                if (panel != null) {
                    panel.repaint();
                }
                return true;
            }
        }
        return false;
    }
}
