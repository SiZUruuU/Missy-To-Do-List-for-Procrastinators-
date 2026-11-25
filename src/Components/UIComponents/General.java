package Components.UIComponents;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.FontMetrics;
import Components.Utilities.TextRenderer;

public class General {

    TextRenderer textRender = new TextRenderer();

    Panel panel;
    Font maruMonica;

    public General(Panel panel){
        this.panel = panel;
        panel.setBackground(Color.decode("#f2f1f3"));

    }

    public void draw(Graphics2D g2){
        // intro(g2);
        // keyboardInput(g2);
        test(g2);
    }

    public void test(Graphics2D g2){
        String text = "Hi Im Paul";
        textRender.drawTextPlain(g2, text, panel.maruFont, 40, Color.black, "PLAIN", panel.SCREEN_WIDTH / 2, panel.SCREEN_HEIGHT / 2);
    }

    // public void intro(Graphics2D g2){
    //     FontMetrics fm = g2.getFontMetrics();

    //     g2.setFont(maruMonica.deriveFont(Font.BOLD, 60F));
    //     g2.setColor(Color.BLACK);
    //     String text = "Hello, Missy";

    //     int textW = (fm.stringWidth(text)) * 3;

    //     g2.drawString(text, (panel.SCREEN_WIDTH - textW) / 2, panel.SCREEN_HEIGHT / 6);

    //     g2.setFont(maruMonica.deriveFont(Font.BOLD, 60F));
    //     g2.setColor(Color.BLACK);
    //     text = "Database testing phase";

    //     textW = (fm.stringWidth(text)) * 3;

    //     g2.drawString(text, (panel.SCREEN_WIDTH - textW) / 2, panel.SCREEN_HEIGHT / 3);

    //     g2.setFont(maruMonica.deriveFont(Font.BOLD, 40F));
    //     g2.setColor(Color.BLACK);
    // }

    public void keyboardInput(Graphics2D g2){

        FontMetrics fm = g2.getFontMetrics();

        g2.setFont(maruMonica.deriveFont(Font.BOLD, 40F));
        g2.setColor(Color.BLACK);
        String text = "Keyboard input: " + panel.key.getTyped();


        g2.drawString(text, panel.SCREEN_WIDTH / 4, panel.SCREEN_HEIGHT / 2);
    }
    
}
