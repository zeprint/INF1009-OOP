package io.github.some_example_name.lwjgl3.logic.entity;

import io.github.some_example_name.lwjgl3.AbstractEngine.entity.Entity;

import com.badlogic.gdx.math.Rectangle;

import io.github.some_example_name.lwjgl3.Collidable;
import io.github.some_example_name.lwjgl3.CollisionResult;
import io.github.some_example_name.lwjgl3.CollisionType;

/**
 * SmallObstacle - A low obstacle that is one-quarter of the Character's height.
 *
 * The player must <b>jump</b> to clear this obstacle.  It sits on the floor
 * surface and scrolls towards the character as the game progresses.
 *
 * Height = {@code CHARACTER_HEIGHT / 4 = 25 px} by default.
 */
public class SmallObstacle extends Entity implements Collidable {

    /** Fraction of the character's height used for this obstacle. */
    private static final float HEIGHT_RATIO = 0.25f;
    private static final float DEFAULT_CHARACTER_HEIGHT = 100f;
    private static final float DEFAULT_WIDTH = 50f;

    private float x;
    private float y;
    private final float width;
    private final float height;
    private float scrollSpeed;
    private final CollisionType collisionType;

    /**
     * Creates a SmallObstacle.
     *
     * @param x           spawn x-coordinate (centre of obstacle)
     * @param floorY      y-coordinate of the floor surface
     * @param scrollSpeed horizontal speed at which the obstacle approaches (pixels/sec, positive = leftward)
     */
    public SmallObstacle(float x, float floorY, float scrollSpeed) {
        this(x, floorY, scrollSpeed, DEFAULT_CHARACTER_HEIGHT);
    }

    /**
     * Creates a SmallObstacle with a custom reference character height.
     *
     * @param x               spawn x-coordinate
     * @param floorY          floor surface y
     * @param scrollSpeed     approach speed (pixels/sec)
     * @param characterHeight the character's height used to derive this obstacle's height
     */
    public SmallObstacle(float x, float floorY, float scrollSpeed, float characterHeight) {
        super();
        this.width  = DEFAULT_WIDTH;
        this.height = characterHeight * HEIGHT_RATIO;
        this.x = x;
        this.y = floorY;
        this.scrollSpeed = scrollSpeed;
        this.collisionType = new CollisionType("small_obstacle", true, true);
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
    public void onCollision(CollisionResult result) {
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
