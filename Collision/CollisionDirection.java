package io.github.some_example_name.lwjgl3;

/**
 * CollisionDirection - Type-safe direction labels for collision resolution.
 *
 * Replaces the previous String-based directions ("LEFT", "RIGHT", etc.)
 * for compile-time safety and OCP compliance.
 */
public enum CollisionDirection {
    LEFT,
    RIGHT,
    TOP,
    BOTTOM
}
