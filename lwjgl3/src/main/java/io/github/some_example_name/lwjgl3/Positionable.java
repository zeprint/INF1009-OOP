package io.github.some_example_name.lwjgl3;

/**
 * Positionable - Minimal position contract required by movement components.
 */
public interface Positionable {

    float getX();

    boolean setX(float x);

    float getY();

    boolean setY(float y);
}
