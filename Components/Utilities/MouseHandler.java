package Components.Utilities;

import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputListener;

public class MouseHandler implements  MouseInputListener{


    @Override
    public void mouseClicked(MouseEvent e) {

        int x = e.getX();
        int y = e.getY();

        System.out.println("Pressed at " + x + " " + y);


    }

    @Override
    public void mousePressed(MouseEvent e) {

        int x = e.getX();
        int y = e.getY();

    }

    @Override
    public void mouseReleased(MouseEvent e) {

        int x = e.getX();
        int y = e.getY();

    }

    @Override
    public void mouseEntered(MouseEvent e) {

        int x = e.getX();
        int y = e.getY();


    }

    @Override
    public void mouseExited(MouseEvent e) {

        int x = e.getX();
        int y = e.getY();

    }

    @Override
    public void mouseDragged(MouseEvent e) {

        int x = e.getX();
        int y = e.getY();

    }

    @Override
    public void mouseMoved(MouseEvent e) {

        int x = e.getX();
        int y = e.getY();

    }
    
}
