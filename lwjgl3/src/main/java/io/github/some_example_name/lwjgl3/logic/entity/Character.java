package io.github.some_example_name.lwjgl3.logic.entity;

import io.github.some_example_name.lwjgl3.AbstractEngine.entity.Entity;
import io.github.some_example_name.lwjgl3.AbstractEngine.entity.Transform;
import io.github.some_example_name.lwjgl3.AbstractEngine.entity.PhysicsBody;

import com.badlogic.gdx.math.Rectangle;

import io.github.some_example_name.lwjgl3.Collidable;
import io.github.some_example_name.lwjgl3.CollisionResult;
import io.github.some_example_name.lwjgl3.logic.Collision.CollisionHandler;

public class Character extends Entity implements Collidable {

    // ---- Dimensions ----
    private static final float DEFAULT_WIDTH  = 50f;
    private static final float DEFAULT_HEIGHT = 100f;

    // ---- Physics tunables ----
    private static final float JUMP_VELOCITY = 500f;
    private static final float GRAVITY       = -1200f;
    private static final float LANE_SWITCH_SPEED = 600f;  // pixels / sec towards target lane

    // ---- Lane setup (three lanes like Subway Surfers) ----
    private static final int   LANE_COUNT    = 3;
    private static final float LANE_SPACING  = 80f;       // distance between lane centres

    // ---- Hit flash ----
    private static final float HIT_FLASH_DURATION = 0.15f;

    // ---- Dimensions (not spatial — kept as fields) ----
    private float width;
    private float height;

    // ---- Grounded state (vertical logic) ----
    private boolean grounded;
    private float floorY;          // y-coordinate of the floor surface

    // ---- Lane state (horizontal logic) ----
    private int   currentLane;     // 0 = left, 1 = centre, 2 = right
    private float laneCentreX;     // x of lane 0 (leftmost lane)

    // ---- Collision handler (Observer pattern) ----
    private CollisionHandler collisionHandler;

    // ---- Hit flash state ----
    private boolean hitFlashing;
    private float hitFlashTimer;

    /**
     * Creates a Character in the centre lane.
     *
     * @param centreX  x-coordinate of the centre lane
     * @param floorY   y-coordinate of the floor surface (character stands on top)
     */
    public Character(float centreX, float floorY) {
        super();
        this.width  = DEFAULT_WIDTH;
        this.height = DEFAULT_HEIGHT;

        // Lane geometry: lane 0 is one LANE_SPACING to the left of centre
        this.laneCentreX = centreX - LANE_SPACING;
        this.currentLane = 1;  // start in the middle lane
        this.floorY = floorY;

        this.grounded  = true;

        this.collisionHandler = null;
        this.hitFlashing = false;
        this.hitFlashTimer = 0f;

        // ---- Attach engine components ----
        // Transform holds position (x, y)
        addComponent(new Transform(getLaneX(currentLane), floorY));
        // PhysicsBody holds velocity (used for vertical velocity / gravity)
        addComponent(new PhysicsBody(0f, 0f, 1f));
    }

    // ---- Update (called every frame) ----

    @Override
    public void update(float deltaTime) {
        if (!isActive()) return;

        Transform transform = getComponent(Transform.class);
        PhysicsBody body    = getComponent(PhysicsBody.class);

        // --- Gravity ---
        if (!grounded) {
            // Apply gravity to vertical velocity
            body.setVelocity(body.getVelocity().x, body.getVelocity().y + GRAVITY * deltaTime);
            transform.translate(0f, body.getVelocity().y * deltaTime);

            // Landing check
            if (transform.getY() <= floorY) {
                transform.setY(floorY);
                body.setVelocity(body.getVelocity().x, 0f);
                grounded = true;
            }
        }

        // --- Smooth lane interpolation ---
        float targetX = getLaneX(currentLane);
        if (Math.abs(transform.getX() - targetX) > 1f) {
            float direction = Math.signum(targetX - transform.getX());
            transform.translate(direction * LANE_SWITCH_SPEED * deltaTime, 0f);
            // Clamp so we don't overshoot
            if ((direction > 0 && transform.getX() > targetX) ||
                (direction < 0 && transform.getX() < targetX)) {
                transform.setX(targetX);
            }
        } else {
            transform.setX(targetX);
        }

        // --- Hit flash countdown ---
        if (hitFlashing) {
            hitFlashTimer -= deltaTime;
            if (hitFlashTimer <= 0f) {
                hitFlashing = false;
                hitFlashTimer = 0f;
            }
        }

        super.update(deltaTime);
    }

    // ---- Controls ----

    /** Move one lane to the left (if not already in lane 0). */
    public void moveLeft() {
        if (currentLane > 0) {
            currentLane--;
        }
    }

    /** Move one lane to the right (if not already in the rightmost lane). */
    public void moveRight() {
        if (currentLane < LANE_COUNT - 1) {
            currentLane++;
        }
    }

    /** Jump (only if currently on the ground). */
    public void jump() {
        if (grounded) {
            PhysicsBody body = getComponent(PhysicsBody.class);
            body.setVelocity(body.getVelocity().x, JUMP_VELOCITY);
            grounded = false;
        }
    }

    // ---- Collidable ----

    @Override
    public Rectangle getBounds() {
        Transform transform = getComponent(Transform.class);
        return new Rectangle(transform.getX() - width / 2f, transform.getY(), width, height);
    }

    @Override
    public void onCollision(CollisionResult result) {
        // Delegate to the CollisionHandler (Observer pattern).
        // The handler decides what gameplay effects the collision has.
        if (collisionHandler != null) {
            collisionHandler.onCharacterCollision(this, result);
        }
    }

    @Override
    public boolean isCollidable() {
        return isActive();
    }

    // ---- Collision handler injection ----

    /**
     * Sets the CollisionHandler that will receive collision callbacks.
     * Called by GameScene at scene creation time.
     *
     * @param handler the CollisionHandler (typically a CollisionDispatcher)
     */
    public void setCollisionHandler(CollisionHandler handler) {
        this.collisionHandler = handler;
    }

    public CollisionHandler getCollisionHandler() {
        return collisionHandler;
    }

    // ---- Hit flash ----

    /**
     * Triggers a brief visual hit flash on the character.
     * Called by CollisionDispatcher when the character takes damage.
     * The rendering layer should check isHitFlashing() to apply the visual effect.
     */
    public void triggerHitFlash() {
        this.hitFlashing = true;
        this.hitFlashTimer = HIT_FLASH_DURATION;
    }

    /** Returns whether the character is currently in a hit-flash state. */
    public boolean isHitFlashing() {
        return hitFlashing;
    }

    // ---- Helpers ----

    /** Returns the world-x of the given lane index. */
    private float getLaneX(int lane) {
        return laneCentreX + lane * LANE_SPACING;
    }

    // ---- Accessors (delegate to Transform / PhysicsBody) ----

    public float getX()      { return getComponent(Transform.class).getX(); }
    public float getY()      { return getComponent(Transform.class).getY(); }
    public float getWidth()  { return width; }
    public float getHeight() { return height; }
    public int   getCurrentLane() { return currentLane; }
    public boolean isGrounded()   { return grounded; }

    public void setX(float x) { getComponent(Transform.class).setX(x); }
    public void setY(float y) { getComponent(Transform.class).setY(y); }
    public void setWidth(float w)  { this.width = w; }
    public void setHeight(float h) { this.height = h; }
    public void setGrounded(boolean g) { this.grounded = g; }
}
