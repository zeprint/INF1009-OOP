package io.github.some_example_name.lwjgl3.logic.entity;

import io.github.some_example_name.lwjgl3.AbstractEngine.entity.Entity;
import io.github.some_example_name.lwjgl3.AbstractEngine.entity.Transform;

import com.badlogic.gdx.math.Rectangle;

import io.github.some_example_name.lwjgl3.Collidable;
import io.github.some_example_name.lwjgl3.CollisionResult;
import io.github.some_example_name.lwjgl3.logic.Collision.CollisionHandler;
import io.github.some_example_name.lwjgl3.logic.movement.CoordinateTarget;
import io.github.some_example_name.lwjgl3.logic.movement.MotionState;

/**
 * Character - The player entity in a lane-based infinite runner.
 *
 * Movement (lane switching, jumping, gravity) is no longer handled here.
 * It is delegated to LaneSwitchMovement and GravityJumpMovement, which are
 * registered with the MovementManager and write position back via the
 * CoordinateTarget interface.
 *
 * Character retains ownership of:
 * - dimensions (width / height)
 * - collision (Collidable)
 * - hit-flash visual state
 * - a reference to the shared MotionState for grounded queries
 */
public class Character extends Entity implements Collidable, CoordinateTarget {

    // ---- Dimensions ----
    private static final float DEFAULT_WIDTH  = 50f;
    private static final float DEFAULT_HEIGHT = 100f;

    // ---- Hit flash ----
    private static final float HIT_FLASH_DURATION = 0.15f;

    private float width;
    private float height;

    // ---- Shared motion state (owned by the game scene, read here) ----
    private MotionState motionState;

    // ---- Collision handler (Observer pattern) ----
    private CollisionHandler collisionHandler;

    // ---- Hit flash state ----
    private boolean hitFlashing;
    private float hitFlashTimer;

    /**
     * Creates a Character at the given position.
     *
     * @param centreX  x-coordinate of the centre lane
     * @param floorY   y-coordinate of the floor surface (character stands on top)
     */
    public Character(float centreX, float floorY) {
        super();
        this.width  = DEFAULT_WIDTH;
        this.height = DEFAULT_HEIGHT;

        this.motionState = null;
        this.collisionHandler = null;
        this.hitFlashing = false;
        this.hitFlashTimer = 0f;

        // Transform holds position — the only engine component needed now
        addComponent(new Transform(centreX, floorY));
    }

    // ---- CoordinateTarget (movement components write position here) ----

    @Override
    public float getX() { return getComponent(Transform.class).getX(); }

    @Override
    public float getY() { return getComponent(Transform.class).getY(); }

    @Override
    public void setX(float x) { getComponent(Transform.class).setX(x); }

    @Override
    public void setY(float y) { getComponent(Transform.class).setY(y); }

    // ---- Update (only non-movement logic remains) ----

    @Override
    public void update(float deltaTime) {
        if (!isActive()) return;

        // Hit flash countdown
        if (hitFlashing) {
            hitFlashTimer -= deltaTime;
            if (hitFlashTimer <= 0f) {
                hitFlashing = false;
                hitFlashTimer = 0f;
            }
        }

        super.update(deltaTime);
    }

    // ---- Collidable ----

    @Override
    public Rectangle getBounds() {
        Transform transform = getComponent(Transform.class);
        return new Rectangle(transform.getX() - width / 2f, transform.getY(), width, height);
    }

    @Override
    public void onCollision(CollisionResult result) {
        if (collisionHandler != null) {
            collisionHandler.onCharacterCollision(this, result);
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

    // ---- Shared MotionState ----

    /**
     * Injects the shared MotionState created by the game scene.
     * Used to query grounded state for rendering / collision purposes.
     */
    public void setMotionState(MotionState motionState) {
        this.motionState = motionState;
    }

    public MotionState getMotionState() {
        return motionState;
    }

    public boolean isGrounded() {
        return motionState != null && motionState.isGrounded();
    }

    // ---- Hit flash ----

    public void triggerHitFlash() {
        this.hitFlashing = true;
        this.hitFlashTimer = HIT_FLASH_DURATION;
    }

    public boolean isHitFlashing() {
        return hitFlashing;
    }

    // ---- Accessors ----

    public float getWidth()  { return width; }
    public float getHeight() { return height; }

    public void setWidth(float w)  { this.width = w; }
    public void setHeight(float h) { this.height = h; }
}