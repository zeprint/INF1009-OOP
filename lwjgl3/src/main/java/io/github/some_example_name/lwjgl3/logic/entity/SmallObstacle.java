package io.github.some_example_name.lwjgl3.logic.entity;

import io.github.some_example_name.lwjgl3.AbstractEngine.entity.Entity;
import io.github.some_example_name.lwjgl3.AbstractEngine.entity.Transform;
import io.github.some_example_name.lwjgl3.AbstractEngine.entity.PhysicsBody;

import com.badlogic.gdx.math.Rectangle;

import io.github.some_example_name.lwjgl3.Collidable;
import io.github.some_example_name.lwjgl3.CollisionResult;
import io.github.some_example_name.lwjgl3.logic.Collision.CollisionHandler;

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

    private final float width;
    private final float height;

    // ---- Collision handler (Observer pattern) ----
    private CollisionHandler collisionHandler;

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
        this.collisionHandler = null;

        // ---- Attach engine components ----
        // Transform holds position (x, y)
        addComponent(new Transform(x, floorY));
        // PhysicsBody holds velocity (scrolling leftward)
        addComponent(new PhysicsBody(-scrollSpeed, 0f, 1f));
    }

    // ---- Update ----

    @Override
    public void update(float deltaTime) {
        if (!isActive()) return;

        // Move using PhysicsBody velocity
        Transform transform = getComponent(Transform.class);
        PhysicsBody body    = getComponent(PhysicsBody.class);
        transform.translate(body.getVelocity().x * deltaTime, body.getVelocity().y * deltaTime);

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
        // Delegate to the CollisionHandler (Observer pattern).
        if (collisionHandler != null) {
            collisionHandler.onObstacleCollision(this, result);
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

    // ---- Accessors (delegate to Transform / PhysicsBody) ----

    public float getX()      { return getComponent(Transform.class).getX(); }
    public float getY()      { return getComponent(Transform.class).getY(); }
    public float getWidth()  { return width; }
    public float getHeight() { return height; }

    public float getScrollSpeed() {
        return -getComponent(PhysicsBody.class).getVelocity().x;
    }

    public void setX(float x) { getComponent(Transform.class).setX(x); }
    public void setY(float y) { getComponent(Transform.class).setY(y); }

    public void setScrollSpeed(float speed) {
        PhysicsBody body = getComponent(PhysicsBody.class);
        body.setVelocity(-speed, body.getVelocity().y);
    }
}
