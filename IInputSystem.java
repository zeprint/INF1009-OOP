package io.github.some_example_name.lwjgl3;

// IInputSystem interface - Defines contract for input handling
public interface IInputSystem {
    
    // Gets the current value of an input axis
    float getAxis(InputAxis axis);
    
    // Checks if an input action was triggered this frame
    boolean isActionTriggered(InputAction action);
}
