package Components.Utilities;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class TextRenderer {
    
    public void drawTextPlain(
        Graphics2D g2,
        String text,
        Font font,
        float size,
        Color color,
        String type,
        int x,
        int y
        )
        {
            switch(type){
            
                case "PLAIN" -> {
                    g2.setFont(font.deriveFont(Font.PLAIN, size));
                    g2.setColor(color);
                    g2.drawString(text, x, y);
                }

                case "BOLD" -> {
                    g2.setFont(font.deriveFont(Font.BOLD, size));
                    g2.setColor(color);
                    g2.drawString(text, x, y);
                }

                case "ITALIC" -> {
                    g2.setFont(font.deriveFont(Font.ITALIC, size));
                    g2.setColor(color);
                    g2.drawString(text, x, y);
                }
            }
        }


    public void drawTextUniqueColor(
        Graphics2D g2,
        String text,
        Font font,
        float size,
        String color,
        String type,
        int x,
        int y
        )
        {
            switch(type){
            
                case "PLAIN" -> {
                    g2.setFont(font.deriveFont(Font.PLAIN, size));
                    g2.setColor(Color.decode(color));
                    g2.drawString(text, x, y);
                }

                case "BOLD" -> {
                    g2.setFont(font.deriveFont(Font.BOLD, size));
                    g2.setColor(Color.decode(color));
                    g2.drawString(text, x, y);
                }

                case "ITALIC" -> {
                    g2.setFont(font.deriveFont(Font.ITALIC, size));
                    g2.setColor(Color.decode(color));
                    g2.drawString(text, x, y);
                }
            }
        }
    
}
