package io.github.some_example_name.lwjgl3;

/* Supported primitive shape types for shape-based entities. */

public enum ShapeType {
    CIRCLE,
    RECTANGLE,
    TRIANGLE,
    SQUARE;

    public static ShapeType fromString(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        try {
            return ShapeType.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
