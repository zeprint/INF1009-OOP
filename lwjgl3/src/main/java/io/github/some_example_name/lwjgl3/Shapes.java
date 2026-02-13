package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * Shapes - Entity rendered as a primitive shape via ShapeRenderer.
 *
 * Dimensions are stored in a flexible String-keyed map so different
 * shape types can declare their own required keys (e.g. "radius", "width").
 */
public class Shapes extends Entity {

    private ShapeType shapeType;
    private final ObjectMap<String, Float> dimensions;

    /* Default constructor (origin 0,0, no shape type set). */
    public Shapes() {
        super(0f, 0f);
        this.dimensions = new ObjectMap<>();
    }

    /* Static shape at (x, y) with a given type and colour. */
    public Shapes(ShapeType shapeType, float x, float y, Color color) {
        super(x, y);
        this.shapeType = shapeType;
        this.color = color;
        this.dimensions = new ObjectMap<>();
    }

    public ShapeType getShapeType() {
        return shapeType;
    }

    public void setDimensions(String key, float value) {
        dimensions.put(key, value);
    }

    public float getDimension(String key) {
        Float v = dimensions.get(key);
        return (v != null) ? v : 0f;
    }

    // --- Renderable ---

    @Override
    public void draw(ShapeRenderer shape) {
        if (shapeType == null) return;          // guard against uninitialised type
        if (color != null) shape.setColor(color);

        switch (shapeType) {
            case CIRCLE:
                shape.circle(getX(), getY(), getDimension("radius"));
                break;

            case RECTANGLE:
                shape.rect(getX(), getY(), getDimension("width"), getDimension("height"));
                break;

            case TRIANGLE:
                float s = getDimension("size");
                shape.triangle(
                    getX(), getY(),
                    getX() + s, getY(),
                    getX() + (s / 2f), getY() + s
                );

                break;
                
        }
    }
}
