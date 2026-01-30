package io.github.some_example_name.lwjgl3;

/* Interface for movement components that support acceleration */
public interface Acceleratable {
    
    /* Get acceleration of X component */
    float getAccelerationX();
    
    /* Get acceleration of Y component */
    float getAccelerationY();
}
