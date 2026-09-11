import java.awt.Dimension;

import javax.swing.JPanel;

public class Panel extends JPanel{

    final int screenWidth = 1000;
    final int screenHeight = 1000;
    
    public Panel(){

        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setLayout(null);
        this.setDoubleBuffered(true);
    }
}
