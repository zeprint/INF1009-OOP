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
 * FIX: Caches the Rectangle returned by getBounds() to avoid allocating
 * a new object every frame during collision checks.
 */
public class Bucket extends TextureObject implements Collidable {

    private final CollisionType collisionType;
    private final IAudioSystem  audioSystem;
    private final float         speed;
    private final Rectangle     bounds;

    public Bucket(Texture texture, float x, float y, int width, int height,
                  IAudioSystem audioSystem) {
        super(texture, x, y, height, width);
        this.audioSystem   = audioSystem;
        this.speed         = 300f;
        this.collisionType = new CollisionType("bucket", true, true);
        this.bounds        = new Rectangle(x, y, width, height);
    }

    // --- Collidable ---

    @Override
    public Rectangle getBounds() {
        bounds.set(getX(), getY(), getWidth(), getHeight());
        return bounds;
    }

    @Override
    public CollisionType getType() { return collisionType; }

    @Override
    public void onCollision(CollisionResult result) {
        // Play click sound when a droplet hits the bucket
        if (audioSystem != null) {
            audioSystem.playSound("click");
        }
    }

    // --- Movement ---

    /** @return movement speed in pixels per second. */
    public float getSpeed() { return speed; }
}
