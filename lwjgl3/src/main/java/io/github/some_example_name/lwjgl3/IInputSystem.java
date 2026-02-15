package io.github.some_example_name.lwjgl3;

/**
 * IInputSystem - Contract for input management.
 */
public interface IInputSystem {

    float getAxis(InputAxis axis);

    boolean isActionTriggered(InputAction action);

    void update();

    void dispose();
}
