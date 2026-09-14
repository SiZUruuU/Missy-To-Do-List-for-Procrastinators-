package Components.UIComponents;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import Components.Utilities.MouseHandler;

public class Panel extends JPanel{

    final int SCREEN_WIDTH = 800;
    final int SCREEN_HEIGHT = 400;

    MouseHandler mouse = new MouseHandler();
    General gen = new General(this);
    
    public Panel(){

        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setLayout(null);
        this.setDoubleBuffered(true);

        this.addMouseListener(mouse);
    }

    @Override 
    public void paintComponent(Graphics g){
        super.paintComponent(g); 

        Graphics2D g2 = (Graphics2D)g; 

        gen.draw(g2);
    }
}
