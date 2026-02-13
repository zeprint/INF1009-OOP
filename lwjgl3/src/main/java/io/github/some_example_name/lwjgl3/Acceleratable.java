package io.github.some_example_name.lwjgl3;

/**
 * Acceleratable - Contract for movement components that support acceleration (ISP).
 *
 * Implementing class: GravityMovement
 */
public interface Acceleratable {

    /** @return acceleration along the X axis. */
    float getAccelerationX();

    /** @return acceleration along the Y axis. */
    float getAccelerationY();
}
