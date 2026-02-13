package io.github.some_example_name.lwjgl3;

/**
 * HasRotation - Contract for entities that expose a visual rotation angle (ISP / LSP).
 *
 * Fixes the LSP violation where RotationComponent blindly cast its Entity
 * to RotatingShape. Now RotationComponent checks (entity instanceof HasRotation)
 * before updating the angle, making it safe to attach to any Entity subclass.
 *
 * Implementing class: RotatingShape
 */
public interface HasRotation {

    /* Set the visual rotation angle in degrees. */
    void setRotationAngle(float angle);

    /** @return current visual rotation angle in degrees. */
    float getRotationAngle();
}
