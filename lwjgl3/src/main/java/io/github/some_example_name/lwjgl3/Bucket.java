package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

/**
 * Bucket - Logic Engine entity: player-controlled bucket (contextual).
 *
 * Extends TextureObject for texture rendering, implements Collidable
 * so the CollisionManager can detect bucket-droplet collisions.
 * On collision, plays a click sound via IAudioSystem (DIP).
 *
 * Change: width and height parameters are now float to match TextureObject.
 */
public class Bucket extends TextureObject implements Collidable {

    private final CollisionType collisionType;
    private final IAudioSystem audioSystem;
    private final float speed;
    private final Rectangle bounds;

    /**
     * Create a new bucket entity.
     *
     * @param texture     Texture to render. Must not be {@code null}.
     * @param x           Initial X position. Must be finite.
     * @param y           Initial Y position. Must be finite.
     * @param width       Width in pixels. Must be finite and positive.
     * @param height      Height in pixels. Must be finite and positive.
     * @param audioSystem Audio system for collision sounds. May be {@code null}.
     * @throws IllegalArgumentException if texture is null or dimensions are invalid.
     */
    public Bucket(Texture texture, float x, float y, float width, float height,
                  IAudioSystem audioSystem) {
        super(texture, x, y, height, width);
        this.audioSystem = audioSystem;
        this.speed = 300f;
        this.collisionType = new CollisionType("bucket", true, true);
        this.bounds = new Rectangle(x, y, width, height);
    }

    // --- Collidable ---

    @Override
    public Rectangle getBounds() {
        bounds.set(getX(), getY(), getWidth(), getHeight());
        return bounds;
    }

    @Override
    public CollisionType getType() {
        return collisionType;
    }

    @Override
    public void onCollision(CollisionResult result) {
        // Play click sound when a droplet hits the bucket
        if (audioSystem != null) {
            audioSystem.playSound("click");
        }
    }

    // --- Movement ---

    /** @return movement speed in pixels per second. */
    public float getSpeed() {
        return speed;
    }
}
