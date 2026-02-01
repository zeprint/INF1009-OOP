package io.github.some_example_name.lwjgl3;

public interface InputProvider {
    void update();

    float getAxis(InputAxis axis);                 // -1..+1
    boolean isActionTriggered(InputAction action); // "just pressed" style
    boolean isActionDown(InputAction action);      // "held down" style (optional)

    float getMouseX();
    float getMouseY();
}
