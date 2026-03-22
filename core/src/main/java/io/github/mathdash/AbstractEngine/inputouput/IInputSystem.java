package io.github.mathdash.AbstractEngine.inputouput;

/**
 * IInputSystem - Contract for input management.
 */
public interface IInputSystem {

    float getAxis(InputAxis axis);

    boolean isActionTriggered(InputAction action);

    boolean isActionHeld(InputAction action);

    void update();

    void dispose();
}
