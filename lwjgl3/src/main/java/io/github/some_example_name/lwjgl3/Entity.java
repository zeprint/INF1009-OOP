package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Entity - Abstract base for every object managed by the engine.
 *
 * Implements Renderable and IEntity with no-op defaults so that
 * EntityManager can iterate a single typed list for drawing and updating.
 * Subclasses override only the methods they need.
 */

public abstract class Entity implements Renderable, IEntity {

    private static final String TAG = "Entity";

    protected float posX;
    protected float posY;
    protected Color color;

    public Entity(float x, float y) {
        if (!Float.isFinite(x) || !Float.isFinite(y)) {
            throw new IllegalArgumentException(
                "Entity position must be finite: x=" + x + ", y=" + y);
        }
        this.posX = x;
        this.posY = y;
        this.color = Color.WHITE;
    }

    // Position

    public float getX() {
        return posX;
    }

    public boolean setX(float x) {
        if (!Float.isFinite(x)) {
            Gdx.app.error(TAG, "setX rejected non-finite value: " + x);
            return false;
        }
        posX = x;
        return true;
    }

    public float getY() {
        return posY;
    }

    public boolean setY(float y) {
        if (!Float.isFinite(y)) {
            Gdx.app.error(TAG, "setY rejected non-finite value: " + y);
            return false;
        }
        posY = y;
        return true;
    }

    // Color

    public Color getColor() {
        return color;
    }

    public boolean setColor(Color c) {
        if (c == null) {
            Gdx.app.error(TAG, "setColor rejected null colour");
            return false;
        }
        color = c;
        return true;
    }

    // Renderable (safe no-op defaults)

    @Override
    public boolean draw(SpriteBatch batch) {
        return false;
    }

    @Override
    public boolean draw(ShapeRenderer shape) {
        return false;
    }

    // IEntity lifecycle (safe no-op defaults)

    @Override
    public boolean initialize() {
        return true;
    }

    @Override
    public boolean update(float deltaTime) {
        if (!Float.isFinite(deltaTime) || deltaTime < 0f) {
            Gdx.app.error(TAG, "update rejected invalid deltaTime: " + deltaTime);
            return false;
        }
        return true;
    }

      @Override
    public boolean dispose() {
        return true;
    }
}