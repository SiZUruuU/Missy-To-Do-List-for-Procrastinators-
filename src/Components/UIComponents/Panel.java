package Components.UIComponents;
    
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import Components.Utilities.MouseHandler;
import Components.Utilities.ButtonManager;
import Components.Utilities.FontManager;
import Components.Utilities.KeyHandler;



public class Panel extends JPanel{

    ButtonManager bm = new ButtonManager(this);
    KeyHandler key = new KeyHandler(this::repaint);
    MouseHandler mouse = new MouseHandler(bm);
    FontManager fm = new FontManager();
    General gen = new General(this);
    
    Font maruFont = fm.loadFont("/Assets/Fonts/x12y16pxMaruMonica.ttf");

    final int SCREEN_WIDTH = 1000;
    final int SCREEN_HEIGHT = 1000;

    
    public Panel(){

        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setLayout(null);
        this.setDoubleBuffered(true);

        this.addMouseListener(mouse);
        this.addKeyListener(key);

        this.addMouseListener(new MouseAdapter() {
        @Override public void mousePressed(MouseEvent e) {
            requestFocusInWindow();
            }
        });
    }


    @Override 
    public void paintComponent(Graphics g){
        super.paintComponent(g); 

        Graphics2D g2 = (Graphics2D)g; 

        gen.draw(g2);
    }
}
