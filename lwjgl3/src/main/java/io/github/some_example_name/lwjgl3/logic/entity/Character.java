package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.math.Rectangle;

/**
 * Character - The player-controlled entity in a Subway-Surfers-style game.
 *
 * Supports three-lane horizontal movement (left / right) and jumping.
 * Gravity is applied every frame; when the character lands on the floor
 * it is considered grounded and can jump again.
 *
 * The Character implements {@link Collidable} so the engine's
 * {@link CollisionManager} can detect hits with obstacles and the floor.
 */
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

    // ---- State ----
    private float x;
    private float y;
    private float width;
    private float height;
    private float velocityY;
    private boolean grounded;
    private float floorY;          // y-coordinate of the floor surface

    private int   currentLane;     // 0 = left, 1 = centre, 2 = right
    private float laneCentreX;     // x of lane 0 (leftmost lane)

    private final CollisionType collisionType;

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
        this.x = getLaneX(currentLane);
        this.floorY = floorY;
        this.y = floorY;

        this.velocityY = 0f;
        this.grounded  = true;

        this.collisionType = new CollisionType("character", true, true);
    }

    // ---- Update (called every frame) ----

    @Override
    public void update(float deltaTime) {
        if (!isActive()) return;

        // --- Gravity ---
        if (!grounded) {
            velocityY += GRAVITY * deltaTime;
            y += velocityY * deltaTime;

            // Landing check
            if (y <= floorY) {
                y = floorY;
                velocityY = 0f;
                grounded = true;
            }
        }

        // --- Smooth lane interpolation ---
        float targetX = getLaneX(currentLane);
        if (Math.abs(x - targetX) > 1f) {
            float direction = Math.signum(targetX - x);
            x += direction * LANE_SWITCH_SPEED * deltaTime;
            // Clamp so we don't overshoot
            if ((direction > 0 && x > targetX) || (direction < 0 && x < targetX)) {
                x = targetX;
            }
        } else {
            x = targetX;
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
            velocityY = JUMP_VELOCITY;
            grounded = false;
        }
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

    // ---- Helpers ----

    /** Returns the world-x of the given lane index. */
    private float getLaneX(int lane) {
        return laneCentreX + lane * LANE_SPACING;
    }

    // ---- Accessors ----

    public float getX()      { return x; }
    public float getY()      { return y; }
    public float getWidth()  { return width; }
    public float getHeight() { return height; }
    public int   getCurrentLane() { return currentLane; }
    public boolean isGrounded()   { return grounded; }

    public void setX(float x) { this.x = x; }
    public void setY(float y) { this.y = y; }
    public void setWidth(float w)  { this.width = w; }
    public void setHeight(float h) { this.height = h; }
    public void setGrounded(boolean g) { this.grounded = g; }
}
