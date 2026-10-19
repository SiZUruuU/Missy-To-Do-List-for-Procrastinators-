package Components.Utilities;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    public boolean isUpPressed, isDownPressed, isEnterPressed;

    private final StringBuilder typed = new StringBuilder();
    private final Runnable onChange;

    public KeyHandler(Runnable onChange) {
        this.onChange = onChange;
    }

    //A method that returns what is typed in the text field
    public String getTyped() {
        return typed.toString();
    }

    //A method that clears the text field 
    public void clearTyped() {
        typed.setLength(0);
        onChange.run();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        
        char c = e.getKeyChar();
        System.out.println("Key typed: " + c);
        if(c == KeyEvent.VK_BACK_SPACE){
            if(typed.length() > 0){
                typed.setLength(typed.length() - 1);
            }
        }
        else if(!Character.isISOControl(c)){
            typed.append(c);
        } 
        onChange.run();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch(e.getKeyCode()) {
            case KeyEvent.VK_UP    -> isUpPressed = true;
            case KeyEvent.VK_DOWN  -> isDownPressed = true;
            case KeyEvent.VK_ENTER -> isEnterPressed = true;
        }
        onChange.run();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch(e.getKeyCode()) {
            case KeyEvent.VK_UP    -> isUpPressed = false;
            case KeyEvent.VK_DOWN  -> isDownPressed = false;
            case KeyEvent.VK_ENTER -> isEnterPressed = false;
        }
    }
    
}
