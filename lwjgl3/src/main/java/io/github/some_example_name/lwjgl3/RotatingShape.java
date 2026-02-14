package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * RotatingShape - Entity rendered as a rotating circle, triangle, or square.
 *
 * OCP FIX: Uses ShapeType enum instead of boolean flags (isCircle, isSquare).
 * Adding a new shape type only requires a new enum value and a new case in draw(),
 * rather than adding another boolean and restructuring if-else chains.
 */

public class RotatingShape extends Entity implements HasRotation {

    private static final String TAG = "RotatingShape";

    private float radius;
    private ShapeType shapeType;
    private float rotationAngle;

    public RotatingShape(float x, float y, float radius, Color color, ShapeType shapeType) {
        super(x, y);

        if (!Float.isFinite(radius) || radius <= 0f) {
            throw new IllegalArgumentException(
                "RotatingShape radius must be finite and positive: " + radius);
        }
        if (color == null) {
            throw new IllegalArgumentException("RotatingShape colour must not be null");
        }
        if (shapeType == null) {
            throw new IllegalArgumentException("RotatingShape shapeType must not be null");
        }

        this.radius = radius;
        this.color = color;
        this.shapeType = shapeType;
        this.rotationAngle = 0f;
    }

    // --- Accessors ---

    public float getRadius() {
        return radius;
    }

    public boolean setRadius(float r) {
        if (!Float.isFinite(r) || r <= 0f) {
            Gdx.app.error(TAG, "setRadius rejected invalid value: " + r);
            return false;
        }
        radius = r;
        return true;
    }

    public ShapeType getShapeType() {
        return shapeType;
    }

    public boolean setShapeType(ShapeType type) {
        if (type == null) {
            Gdx.app.error(TAG, "setShapeType rejected null type");
            return false;
        }
        shapeType = type;
        return true;
    }

    // --- HasRotation ---

    @Override
    public boolean setRotationAngle(float angle) {
        if (!Float.isFinite(angle)) {
            Gdx.app.error(TAG, "setRotationAngle rejected non-finite value: " + angle);
            return false;
        }
        this.rotationAngle = ((angle % 360f) + 360f) % 360f;
        return true;
    }

    @Override
    public float getRotationAngle() {
        return rotationAngle;
    }

    // --- Renderable ---

    @Override
    public boolean draw(ShapeRenderer shape) {
        if (shape == null) {
            return false;
        }
        if (shapeType == null) {
            Gdx.app.error(TAG, "draw skipped: shapeType is null");
            return false;
        }

        try {
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
                    Gdx.app.error(TAG, "draw: unhandled ShapeType " + shapeType);
                    return false;
            }
            return true;
        } catch (Exception e) {
            Gdx.app.error(TAG, "Exception during draw", e);
            return false;
        }
    }

    /* Draw a rotated square as two filled triangles. */
    private void drawSquare(ShapeRenderer shape) {
        float rad = (float) Math.toRadians(rotationAngle);
        float cos = (float) Math.cos(rad);
        float sin = (float) Math.sin(rad);
        float h = radius; // half-size

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

    /* Draw a rotated equilateral triangle. */
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