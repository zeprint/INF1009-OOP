package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.math.Rectangle;

/**
 * TallObstacle - A full-height obstacle equal to the Character's height.
 *
 * Because it matches the character's height, jumping will <b>not</b> clear it.
 * The player must dodge <b>left or right</b> into an adjacent lane to avoid
 * a collision.
 *
 * Each TallObstacle occupies exactly one lane.
 */
public class TallObstacle extends Entity implements Collidable {

    private static final float DEFAULT_CHARACTER_HEIGHT = 100f;
    private static final float DEFAULT_WIDTH = 50f;

    private float x;
    private float y;
    private final float width;
    private final float height;
    private float scrollSpeed;
    private final CollisionType collisionType;

    /**
     * Creates a TallObstacle.
     *
     * @param x           spawn x-coordinate (lane centre)
     * @param floorY      floor surface y-coordinate
     * @param scrollSpeed approach speed (pixels / sec)
     */
    public TallObstacle(float x, float floorY, float scrollSpeed) {
        this(x, floorY, scrollSpeed, DEFAULT_CHARACTER_HEIGHT);
    }

    /**
     * Creates a TallObstacle with a custom reference character height.
     *
     * @param x               spawn x-coordinate
     * @param floorY          floor surface y
     * @param scrollSpeed     approach speed (pixels / sec)
     * @param characterHeight character height this obstacle should match
     */
    public TallObstacle(float x, float floorY, float scrollSpeed, float characterHeight) {
        super();
        this.width  = DEFAULT_WIDTH;
        this.height = characterHeight;  // full character height
        this.x = x;
        this.y = floorY;
        this.scrollSpeed = scrollSpeed;
        this.collisionType = new CollisionType("tall_obstacle", true, true);
    }

    // ---- Update ----

    @Override
    public void update(float deltaTime) {
        if (!isActive()) return;

        // Scroll towards the player (leftward)
        x -= scrollSpeed * deltaTime;

        super.update(deltaTime);
    }

    // ---- Collidable ----

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x - width / 2f, y, width, height);
    }

    @Override
    public void onCollision(Entity other) {
        // Collision response delegated to game scene.
    }

    @Override
    public boolean isCollidable() {
        return isActive();
    }

    public CollisionType getType() {
        return collisionType;
    }

    // ---- Accessors ----

    public float getX()      { return x; }
    public float getY()      { return y; }
    public float getWidth()  { return width; }
    public float getHeight() { return height; }
    public float getScrollSpeed() { return scrollSpeed; }

    public void setX(float x) { this.x = x; }
    public void setY(float y) { this.y = y; }
    public void setScrollSpeed(float speed) { this.scrollSpeed = speed; }
}
