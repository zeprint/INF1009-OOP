package io.github.some_example_name.lwjgl3;

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

    protected float posX;
    protected float posY;
    protected Color color;

    public Entity(float x, float y) {
        this.posX = x;
        this.posY = y;
        this.color = Color.WHITE;
    }

    // --- Position ---

    public float getX() {
        return posX;
    }

    public void setX(float x) {
        posX = x;
    }

    public float getY() {
        return posY;
    }

    public void  setY(float y) {
        posY = y;
    }

    // --- Color ---

    public Color getColor() { 
        return color;
    }

    public void  setColor(Color c) {
        color = c;
    }

    // --- Renderable (no-op defaults) ---

    @Override
    public void draw(SpriteBatch batch) {
        /* override in texture subclasses */
    }

    @Override
    public void draw(ShapeRenderer shape) {
        /* override in shape subclasses */
    }

    // --- IEntity lifecycle (no-op defaults) ---

    @Override public void initialize() {
        /* override for one-time setup */
    }

    @Override public void update(float deltaTime) { 
        /* override if per-frame logic */
    }

    @Override public void dispose() {
        /* override to release resources */
    }
}
