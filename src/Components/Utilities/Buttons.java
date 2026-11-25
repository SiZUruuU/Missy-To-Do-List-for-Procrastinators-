package Components.Utilities;

import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Buttons {

    private final Rectangle bounds;
    private final Runnable action;
    private String label;

    public Buttons(
        Rectangle bounds, 
        Runnable action,
        String label
    ){
        this.bounds = bounds;
        this.action = action;
        this.label = label;
    }

    public boolean contains(int x, int y) {
        return bounds.contains(x, y);
    }

    public void click() {
        if (action != null) {
            action.run();
        }
    }

    public void draw(Graphics2D g2){
        //blank for now
    }
}