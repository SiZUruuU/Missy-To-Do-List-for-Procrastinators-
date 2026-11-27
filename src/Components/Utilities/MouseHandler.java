package Components.Utilities;

import java.awt.event.MouseEvent;
import java.util.function.BiConsumer;
import javax.swing.event.MouseInputListener;

public class MouseHandler implements MouseInputListener {

    private final ButtonManager buttonManager;
    private final BiConsumer<Integer, Integer> onUnhandledClick;

    public MouseHandler(ButtonManager buttonManager) {
        this(buttonManager, null);
    }

    public MouseHandler(ButtonManager buttonManager, BiConsumer<Integer, Integer> onUnhandledClick) {
        this.buttonManager = buttonManager;
        this.onUnhandledClick = onUnhandledClick;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();

        boolean handled = (buttonManager != null) && buttonManager.handleClick(x, y);
        if (!handled && onUnhandledClick != null) {
            onUnhandledClick.accept(x, y);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }
}
