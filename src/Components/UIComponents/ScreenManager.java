package Components.UIComponents;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

import Components.Utilities.States;

public class ScreenManager {

    private final Deque<States> stateStack =
            new ArrayDeque<>();

    public ScreenManager() {
        stateStack.push(States.MAIN);
    }

    public States current() {
        return stateStack.peek();
    }

    public boolean push(States next) {
        Objects.requireNonNull(next);

        if (current() == next) {
            return false;
        }

        stateStack.push(next);
        return true;
    }

    public boolean pop() {
        if (stateStack.size() <= 1) {
            return false;
        }

        stateStack.pop();
        return true;
    }

    public void reset(States screen) {
        Objects.requireNonNull(screen);

        stateStack.clear();
        stateStack.push(screen);
    }
}