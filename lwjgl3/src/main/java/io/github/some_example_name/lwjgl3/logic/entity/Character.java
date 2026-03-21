package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.math.Rectangle;
import io.github.some_example_name.lwjgl3.logic.movement.CoordinateTarget;
import io.github.some_example_name.lwjgl3.logic.movement.StateTarget;

/**
 * Character - The player-controlled entity in a Subway-Surfers-style game.
 *
 * Position and movement are driven externally by the Movement subsystem
 * ({@link io.github.some_example_name.lwjgl3.logic.movement.HorizontalMovement},
 *  {@link io.github.some_example_name.lwjgl3.logic.movement.JumpMovement},
 *  {@link io.github.some_example_name.lwjgl3.logic.movement.DodgeMovement}).
 *
 * Character implements {@link CoordinateTarget} so HorizontalMovement and
 * JumpMovement can write position back, and {@link StateTarget} so
 * DodgeMovement can toggle the dodge flag.
 *
 * The Character implements {@link Collidable} so the engine's
 * {@link CollisionManager} can detect hits with obstacles and the floor.
 */
public class Character extends Entity implements Collidable, CoordinateTarget, StateTarget {

    // ---- Dimensions ----
    private static final float DEFAULT_WIDTH  = 50f;
    private static final float DEFAULT_HEIGHT = 100f;

    // ---- State ----
    private float x;
    private float y;
    private float width;
    private float height;
    private boolean dodging;

    private final CollisionType collisionType;

    /**
     * Creates a Character at the given position.
     *
     * @param centreX  x-coordinate the character starts at (centre lane)
     * @param floorY   y-coordinate of the floor surface (character stands on top)
     */
    public Character(float centreX, float floorY) {
        super();
        this.width  = DEFAULT_WIDTH;
        this.height = DEFAULT_HEIGHT;
        this.x = centreX;
        this.y = floorY;
        this.dodging = false;
        this.collisionType = new CollisionType("character", true, true);
    }

    // ---- Update ----

    @Override
    public void update(float deltaTime) {
        if (!isActive()) return;

        // Position and physics are handled by MovementComponents via
        // MovementManager.  Character.update() only ticks attached
        // engine Components (Renderable, etc.).
        super.update(deltaTime);
    }

    // ---- CoordinateTarget (written to by HorizontalMovement / JumpMovement) ----

    @Override
    public float getX() { return x; }

    @Override
    public void setX(float x) { this.x = x; }

    @Override
    public float getY() { return y; }

    @Override
    public void setY(float y) { this.y = y; }

    // ---- StateTarget (written to by DodgeMovement) ----

    @Override
    public void setStateActive(boolean active) {
        this.dodging = active;
    }

    public boolean isDodging() {
        return dodging;
    }

    // ---- Collidable ----

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x - width / 2f, y, width, height);
    }

    @Override
    public void onCollision(Entity other) {
        // Collision response is handled by the game scene / logic engine.
    }

    @Override
    public boolean isCollidable() {
        return isActive();
    }

    public CollisionType getType() {
        return collisionType;
    }

    // ---- Accessors ----

    public float getWidth()  { return width; }
    public float getHeight() { return height; }

    public void setWidth(float w)  { this.width = w; }
    public void setHeight(float h) { this.height = h; }
}
