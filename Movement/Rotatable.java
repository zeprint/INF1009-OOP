package io.github.some_example_name.lwjgl3;

/**
 * Rotatable - Contract for movement components that support rotation (ISP).
 *
 * Implementing class: RotationComponent
 */
public interface Rotatable {

    /** @return angular velocity in degrees per second. */
    float getAngularVelocity();
}
