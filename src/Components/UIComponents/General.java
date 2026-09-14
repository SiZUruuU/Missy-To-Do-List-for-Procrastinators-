package Components.UIComponents;

import java.awt.Color;
import java.awt.Graphics2D;

public class General {

    Panel panel;

    public General(Panel panel){
        this.panel = panel;
    }

    public void draw(Graphics2D g2){

        panel.setBackground(Color.decode("#2b035f"));
    }
    
}
