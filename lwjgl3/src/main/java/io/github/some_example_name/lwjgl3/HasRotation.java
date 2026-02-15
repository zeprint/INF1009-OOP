package io.github.some_example_name.lwjgl3;

/**
 * HasRotation - Contract for entities that expose a visual rotation angle.
 */
public interface HasRotation {

    boolean setRotationAngle(float angle);

    float getRotationAngle();
}
