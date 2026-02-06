package io.github.some_example_name.lwjgl3;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ObjectMap;

public class Shapes extends Entity {
    
    private ShapeType shapeType;
    private ObjectMap<String, Float> dimensions;

    // --- Constructors ---

    // 1. Default Constructor
    public Shapes() {
        super(0f, 0f);
        this.dimensions = new ObjectMap<>();
    }

    // 2. Static Shape Constructor (No speed)
    // We pass 0 for speedX and speedY to the Entity constructor
    public Shapes(ShapeType shapeType, float x, float y, Color color) {
        super(x, y); 
        this.shapeType = shapeType;
        this.color = color;
        this.dimensions = new ObjectMap<>();
    }

    public ShapeType getShapeType() {
        return shapeType;
    }

    // --- Dimension Helpers ---

    public void setDimensions(String key, float value) {
        dimensions.put(key, value);
    }

    public float getDimension(String key) {
        Float value = dimensions.get(key);
        return value != null ? value : 0f;
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
