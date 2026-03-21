package io.github.some_example_name.lwjgl3.logic.entity;

import io.github.some_example_name.lwjgl3.AbstractEngine.entity.Entity;
import io.github.some_example_name.lwjgl3.AbstractEngine.entity.Transform;

import com.badlogic.gdx.math.Rectangle;

import io.github.some_example_name.lwjgl3.Collidable;
import io.github.some_example_name.lwjgl3.CollisionResult;
import io.github.some_example_name.lwjgl3.logic.Collision.CollisionHandler;
import io.github.some_example_name.lwjgl3.logic.movement.CoordinateTarget;

/**
 * TallObstacle - A full-height obstacle equal to the Character's height.
 *
 * Because it matches the character's height, jumping will not clear it.
 * The player must switch left or right into an adjacent lane to avoid
 * a collision.
 *
 * Movement (scrolling leftward) is now delegated to ObstacleScrollMovement,
 * which is registered with the MovementManager and writes position back
 * via the CoordinateTarget interface.
 */
public class TallObstacle extends Entity implements Collidable, CoordinateTarget {

    private static final float DEFAULT_CHARACTER_HEIGHT = 100f;
    private static final float DEFAULT_WIDTH = 50f;

    private final float width;
    private final float height;

    // ---- Collision handler (Observer pattern) ----
    private CollisionHandler collisionHandler;

    /**
     * Creates a TallObstacle.
     *
     * @param x      spawn x-coordinate (lane centre)
     * @param floorY floor surface y-coordinate
     */
    public TallObstacle(float x, float floorY) {
        this(x, floorY, DEFAULT_CHARACTER_HEIGHT);
    }

    /**
     * Creates a TallObstacle with a custom reference character height.
     *
     * @param x               spawn x-coordinate
     * @param floorY          floor surface y
     * @param characterHeight character height this obstacle should match
     */
    public TallObstacle(float x, float floorY, float characterHeight) {
        super();
        this.width  = DEFAULT_WIDTH;
        this.height = characterHeight;
        this.collisionHandler = null;

        // Transform holds position — the only engine component needed now
        addComponent(new Transform(x, floorY));
    }

    // ---- CoordinateTarget (ObstacleScrollMovement writes position here) ----

    @Override
    public float getX() { return getComponent(Transform.class).getX(); }

    @Override
    public float getY() { return getComponent(Transform.class).getY(); }

    @Override
    public void setX(float x) { getComponent(Transform.class).setX(x); }

    @Override
    public void setY(float y) { getComponent(Transform.class).setY(y); }

    // No update() override — ObstacleScrollMovement handles scrolling via MovementManager

    // ---- Collidable ----

    @Override
    public Rectangle getBounds() {
        Transform transform = getComponent(Transform.class);
        return new Rectangle(transform.getX() - width / 2f, transform.getY(), width, height);
    }

    @Override
    public void onCollision(CollisionResult result) {
        if (collisionHandler != null) {
            collisionHandler.onObstacleCollision(this, result);
        }
    }

    @Override
    public boolean isCollidable() {
        return isActive();
    }

    // ---- Collision handler injection ----

    public void setCollisionHandler(CollisionHandler handler) {
        this.collisionHandler = handler;
    }

    public CollisionHandler getCollisionHandler() {
        return collisionHandler;
    }

    // ---- Accessors ----

    public float getWidth()  { return width; }
    public float getHeight() { return height; }
}