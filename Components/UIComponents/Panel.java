package Components.UIComponents;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import Components.Utilities.MouseHandler;

public class Panel extends JPanel{

    final int screenWidth = 1000;
    final int screenHeight = 1000;

    MouseHandler mouse = new MouseHandler();
    General gen = new General(this);
    
    public Panel(){

        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
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
