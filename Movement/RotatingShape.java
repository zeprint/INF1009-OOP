package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * RotatingShape - Entity rendered as a rotating circle, triangle, or square.
 *
 * OCP FIX: Uses ShapeType enum instead of boolean flags (isCircle, isSquare).
 * Adding a new shape type only requires a new enum value and a new case in draw(),
 * rather than adding another boolean and restructuring if-else chains.
 *
 * Also implements HasRotation so that RotationComponent can set the angle
 * via the interface rather than an unsafe cast (LSP fix).
 */
public class RotatingShape extends Entity implements HasRotation {

    private float     radius;
    private ShapeType shapeType;
    private float     rotationAngle;

    public RotatingShape(float x, float y, float radius, Color color, ShapeType shapeType) {
        super(x, y);
        this.radius        = radius;
        this.color         = color;
        this.shapeType     = shapeType;
        this.rotationAngle = 0f;
    }

    // --- Accessors ---

    public float     getRadius()                  { return radius;    }
    public void      setRadius(float r)           { radius = r;      }
    public ShapeType getShapeType()               { return shapeType; }
    public void      setShapeType(ShapeType type) { shapeType = type; }

    // --- HasRotation ---

    @Override
    public void setRotationAngle(float angle) { this.rotationAngle = angle; }

    @Override
    public float getRotationAngle()           { return rotationAngle; }

    // --- Renderable ---

    @Override
    public void draw(ShapeRenderer shape) {
        if (shapeType == null) return;
        shape.setColor(color);

        switch (shapeType) {
            case CIRCLE:
                shape.circle(posX, posY, radius);
                break;
            case SQUARE:
                drawSquare(shape);
                break;
            case TRIANGLE:
                drawTriangle(shape);
                break;
            default:
                break;
        }
    }

    /** Draw a rotated square as two filled triangles. */
    private void drawSquare(ShapeRenderer shape) {
        float rad = (float) Math.toRadians(rotationAngle);
        float cos = (float) Math.cos(rad);
        float sin = (float) Math.sin(rad);
        float h   = radius; // half-size

        float x1 = posX + (-h * cos - (-h) * sin);
        float y1 = posY + (-h * sin + (-h) * cos);
        float x2 = posX + ( h * cos - (-h) * sin);
        float y2 = posY + ( h * sin + (-h) * cos);
        float x3 = posX + ( h * cos -   h  * sin);
        float y3 = posY + ( h * sin +   h  * cos);
        float x4 = posX + (-h * cos -   h  * sin);
        float y4 = posY + (-h * sin +   h  * cos);

        shape.triangle(x1, y1, x2, y2, x3, y3);
        shape.triangle(x1, y1, x3, y3, x4, y4);
    }

    /** Draw a rotated equilateral triangle. */
    private void drawTriangle(ShapeRenderer shape) {
        float a1 = (float) Math.toRadians(rotationAngle);
        float a2 = a1 + (float) (2 * Math.PI / 3);
        float a3 = a1 + (float) (4 * Math.PI / 3);

        shape.triangle(
            posX + radius * (float) Math.cos(a1), posY + radius * (float) Math.sin(a1),
            posX + radius * (float) Math.cos(a2), posY + radius * (float) Math.sin(a2),
            posX + radius * (float) Math.cos(a3), posY + radius * (float) Math.sin(a3)
        );
    }
}
