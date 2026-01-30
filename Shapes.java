package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import java.util.HashMap;
import java.util.Map;

public class Shapes extends Entity {
    
    private ShapeType shapeType;
    private Map<String, Float> dimensions;

    // --- Constructors ---

    // 1. Default Constructor
    public Shapes() {
        super();
        this.dimensions = new HashMap<>();
    }

    // 2. Static Shape Constructor (No speed)
    // We pass 0 for speedX and speedY to the Entity constructor
    public Shapes(ShapeType shapeType, float x, float y, Color color) {
        super(x, y, color); 
        this.shapeType = shapeType;
        this.dimensions = new HashMap<>();
    }

    // 3. Dynamic Shape Constructor (With speed)
    // We pass the 'speed' to speedY (assuming these fall down like the bucket game)
    public Shapes(ShapeType shapeType, float x, float y, Color color, float speed) {
        super(x, y, color, 0, speed); 
        this.shapeType = shapeType;
        this.dimensions = new HashMap<>();
    }

    // --- Dimension Helpers ---

    public void setDimensions(String key, float value) {
        dimensions.put(key, value);
    }

    public float getDimension(String key) {
        return dimensions.getOrDefault(key, 0f);
    }

    // --- The Combined Draw Logic ---
    
    @Override
    public void draw(ShapeRenderer shape) {
        // Set the color for this entity
        if (getColor() != null) {
            shape.setColor(getColor());
        }

        // SWITCH based on the type
        switch (shapeType) {
            case CIRCLE:
                // Requires: "radius"
                float r = getDimension("radius");
                shape.circle(getX(), getY(), r);
                break;

            case RECTANGLE:
                // Requires: "width", "height"
                float w = getDimension("width");
                float h = getDimension("height");
                shape.rect(getX(), getY(), w, h);
                break;

            case TRIANGLE:
                // Requires: "size"
                float s = getDimension("size");
                // Math to draw a simple triangle pointing up
                shape.triangle(
                    getX(), getY(),                  // Bottom-left
                    getX() + s, getY(),              // Bottom-right
                    getX() + (s / 2), getY() + s     // Top-center
                );
                break;
        }
    }
}