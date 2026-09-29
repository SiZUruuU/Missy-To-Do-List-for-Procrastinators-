package Components.UIComponents;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.FontMetrics;
import java.io.IOException;
import java.io.InputStream;

public class General {

    Panel panel;
    Font maruMonica;

    public General(Panel panel){
        this.panel = panel;
        panel.setBackground(Color.decode("#f2f1f3"));
        loadFont();
    }

     private void loadFont(){
        String path = "/Assets/Fonts/x12y16pxMaruMonica.ttf";
 
        try (InputStream is = getClass().getResourceAsStream(path)) {
            maruMonica = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(Font.BOLD, 60F);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            maruMonica = new Font(Font.MONOSPACED, Font.BOLD, 60);
        }
    }

    public void draw(Graphics2D g2){

        FontMetrics fm = g2.getFontMetrics();

        g2.setFont(maruMonica.deriveFont(Font.BOLD, 60F));
        g2.setColor(Color.BLACK);
        String text = "Hello, Missy";

        int textW = (fm.stringWidth(text)) * 3;
        System.out.println(textW);

        g2.drawString(text, (panel.SCREEN_WIDTH - textW) / 2, panel.SCREEN_HEIGHT / 6);
    }
    
}
