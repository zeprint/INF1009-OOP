package io.github.some_example_name.lwjgl3;

/**
 * IInputSystem - Contract for input management (DIP).
 *
 * Callers depend on this interface rather than InputManager directly,
 * allowing the input system to be swapped (e.g. replay/AI input)
 * without touching call sites.
 *
 * Implementing class: InputManager
 */
public interface IInputSystem {

    /** @return axis value in [-1, 1] for the given axis. */
    float getAxis(InputAxis axis);

    /** @return true only on the frame the action key was first pressed. */
    boolean isActionTriggered(InputAction action);

    /** Poll input state. Call once per frame. */
    void update();

    /** Release resources. */
    void dispose();
}
