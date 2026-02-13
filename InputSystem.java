package io.github.some_example_name.lwjgl3;

public interface InputSystem {

    void update();   // called every frame

    float getAxis(InputAxis axis);

    boolean isActionTriggered(InputAction action);

    boolean isMouseMode();

    float getMouseX();

    float getMouseY();

    void dispose();
}
