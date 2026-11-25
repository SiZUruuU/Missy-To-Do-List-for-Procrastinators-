package Components.Utilities;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import Components.UIComponents.Panel;

//A class that contains all interactable buttons
public class ButtonManager {

    Panel panel;
    private final List<Buttons> buttons = new ArrayList<>();

    public ButtonManager(Panel panel){
        this.panel = panel;
    }

    public void addButton(Buttons button){
        buttons.add(button);
    }

    public void removeButton(Buttons button){
        buttons.remove(button);
    }

    public void render(Graphics2D g2){
        for(Buttons button : buttons){
            button.draw(g2);
        }
    }

    public void handleClick(int x, int y) {
        for (int i = buttons.size() - 1; i >= 0; i--) {
            Buttons button = buttons.get(i);

            if (button.contains(x, y)) {
                button.click();
                break;
            }
        }
    }  
}
